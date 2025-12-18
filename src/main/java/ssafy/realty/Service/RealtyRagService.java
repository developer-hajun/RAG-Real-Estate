package ssafy.realty.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import ssafy.realty.DTO.Response.RealtyResponseDto;
import ssafy.realty.Entity.Realty;
import ssafy.realty.DTO.Response.RealtyRecommendationResponse;
import ssafy.realty.Mapper.RealtyMapper;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RealtyRagService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final RealtyMapper realtyMapper;

    private static final int TARGET_RESULT_SIZE = 5;

    public RealtyRecommendationResponse getRealtyRecommendation(String userQuery) {

        // [STEP 1] 지역키워드들(콤마)|거래유형|최대보증금|최대월세
        String keywordPrompt = """
            질문을 분석하여 "지역키워드들|거래유형|최대보증금|최대월세" 형식으로 한 줄로만 답해.

            [규칙]
            1. 지역키워드들: 콤마(,)로 여러 개 가능.
               - 동/구/시 같은 행정명 + 도로명(~~로/~~길/~~대로)도 포함 가능
               - 없으면 "NONE"
            2. 거래유형: "전세", "월세". 모르면 "ALL"
            3. 최대보증금: 만원 단위 숫자. 조건 없으면 0
            4. 최대월세: 만원 단위 숫자. 조건 없으면 0

            [예시]
            "역삼동 매물 추천" -> 역삼동,강남구,서울|ALL|0|0
            "테헤란로 근처 월세" -> 테헤란로,강남구,서울|월세|0|0
            """;

        String llmResponse = chatClient.prompt()
                .system(keywordPrompt)
                .user(userQuery)
                .call()
                .content();

        if (llmResponse == null) llmResponse = "";
        llmResponse = llmResponse.trim();

        log.info("LLM 분석 결과: {}", llmResponse);

        String[] parts = llmResponse.split("\\|");
        String regionPart = parts.length > 0 ? parts[0].trim() : "NONE";
        String targetType = parts.length > 1 ? parts[1].trim() : "ALL";
        int limitDeposit = parts.length > 2 ? parseSafeInt(parts[2]) : 0;
        int limitMonthly = parts.length > 3 ? parseSafeInt(parts[3]) : 0;

        List<String> regionKeywords = parseRegionKeywords(regionPart);
        if (!(targetType.equals("전세") || targetType.equals("월세") || targetType.equals("ALL"))) {
            targetType = "ALL";
        }

        // [STEP 2] 1차 유사도 검색 (기본 쿼리)
        List<Document> docs1 = similaritySearch(userQuery, 200);

        // (샘플 로그)
        if (!docs1.isEmpty()) {
            log.info("sample doc text={}", docs1.get(0).getText());
            log.info("sample doc meta={}", docs1.get(0).getMetadata());
        }

        // [STEP 3] 1차: 메타+키워드 “정확 매칭” 필터
        FilterStats s1 = new FilterStats();
        List<Document> filtered = filterDocs(docs1, regionKeywords, targetType, limitDeposit, limitMonthly,
                RegionMatchMode.STRICT, s1);

        log.info("filter stats(STRICT #1): pass={}, regionFail={}, typeFail={}, depositFail={}, monthlyFail={}, idMissing={}",
                filtered.size(), s1.regionFail, s1.typeFail, s1.depositFail, s1.monthlyFail, s1.idMissing);

        // [STEP 2-2] ✅ 결과가 부족하면 2차 유사도 검색을 “지역 키워드 강화 쿼리”로 한 번 더
        // (이게 실제로 서울 문서를 topK에 올리는 데 도움이 큼)
        if (filtered.size() < TARGET_RESULT_SIZE && !regionKeywords.isEmpty()) {
            String enrichedQuery = buildEnrichedQuery(userQuery, regionKeywords, targetType);
            List<Document> docs2 = similaritySearch(enrichedQuery, 600);

            // realty_id 기준으로 merge (중복 제거)
            List<Document> merged = mergeByRealtyId(docs1, docs2);

            FilterStats s2 = new FilterStats();
            filtered = filterDocs(merged, regionKeywords, targetType, limitDeposit, limitMonthly,
                    RegionMatchMode.STRICT, s2);

            log.info("filter stats(STRICT #2): pass={}, regionFail={}, typeFail={}, depositFail={}, monthlyFail={}, idMissing={}",
                    filtered.size(), s2.regionFail, s2.typeFail, s2.depositFail, s2.monthlyFail, s2.idMissing);

            // [STEP 3-2] ✅ 그래도 부족하면: 텍스트 유사도 기반(키워드 토큰 유사)으로 region 매칭 완화
            if (filtered.size() < TARGET_RESULT_SIZE) {
                FilterStats s3 = new FilterStats();
                filtered = filterDocs(merged, regionKeywords, targetType, limitDeposit, limitMonthly,
                        RegionMatchMode.FUZZY, s3);

                log.info("filter stats(FUZZY): pass={}, regionFail={}, typeFail={}, depositFail={}, monthlyFail={}, idMissing={}",
                        filtered.size(), s3.regionFail, s3.typeFail, s3.depositFail, s3.monthlyFail, s3.idMissing);
            }
        }

        // 결과 없음
        if (filtered.isEmpty()) {
            return RealtyRecommendationResponse.builder()
                    .aiMessage("죄송합니다. 조건에 맞는 매물을 찾을 수 없습니다. (조건: " + llmResponse + ")\n"
                            + "※ 현재 벡터 검색 결과 상위(topK)에서 해당 지역 문서를 찾지 못했습니다. "
                            + "데이터에 서울/강남구/역삼동 매물이 실제로 존재하는지(주소 포함)도 확인해보세요.")
                    .realties(new ArrayList<>())
                    .build();
        }

        // 상위 5개만
        List<Document> finalDocs = filtered.stream().limit(TARGET_RESULT_SIZE).collect(Collectors.toList());

        // [STEP 4] realty_id 추출 후 DB 조회
        List<Integer> realtyIds = finalDocs.stream()
                .map(d -> safeParseInt(metaStr(d.getMetadata(), "realty_id")))
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        List<Realty> recommendedRealties = realtyMapper.selectRealtyListByIds(realtyIds);

        // [STEP 5] AI 응답 생성
        String context = finalDocs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));

        String systemText = """
            당신은 '전문 부동산 컨설턴트'입니다.
            제공된 [매물 정보]를 바탕으로 사용자에게 매물을 추천해주세요.
            매물의 이름, 가격, 위치를 명확히 언급하세요.
            """;

        String aiResponse = chatClient.prompt()
                .system(systemText)
                .user(u -> u.text("""
                    [매물 정보]
                    {context}

                    [사용자 질문]
                    {query}
                    """)
                        .param("context", context)
                        .param("query", userQuery)
                )
                .call()
                .content();

        return RealtyRecommendationResponse.builder()
                .aiMessage(aiResponse)
                .realties(changeDto(recommendedRealties))
                .build();
    }

    // =========================
    // Vector Search
    // =========================
    private List<Document> similaritySearch(String query, int topK) {
        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .build();
        return vectorStore.similaritySearch(searchRequest);
    }

    private String buildEnrichedQuery(String userQuery, List<String> regionKeywords, String targetType) {
        // 지역 키워드를 앞에 강하게 넣으면 지역 문서가 더 올라오는 경우가 많음
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(" ", regionKeywords));
        if (!"ALL".equals(targetType)) sb.append(" ").append(targetType);
        sb.append(" ").append(userQuery);
        // 템플릿 문서에 자주 나오는 단어 추가(임베딩에 도움)
        sb.append(" 주소 키워드 매물");
        return sb.toString().trim();
    }

    private List<Document> mergeByRealtyId(List<Document> a, List<Document> b) {
        LinkedHashMap<String, Document> map = new LinkedHashMap<>();
        for (Document d : a) {
            String id = metaStr(d.getMetadata(), "realty_id");
            if (!id.isBlank()) map.put(id, d);
        }
        for (Document d : b) {
            String id = metaStr(d.getMetadata(), "realty_id");
            if (!id.isBlank()) map.putIfAbsent(id, d);
        }
        // realty_id가 없는 문서도 혹시 있을 수 있으니 뒤에 붙임
        for (Document d : a) {
            String id = metaStr(d.getMetadata(), "realty_id");
            if (id.isBlank()) map.put(UUID.randomUUID().toString(), d);
        }
        for (Document d : b) {
            String id = metaStr(d.getMetadata(), "realty_id");
            if (id.isBlank()) map.put(UUID.randomUUID().toString(), d);
        }
        return new ArrayList<>(map.values());
    }

    // =========================
    // Filtering
    // =========================
    private enum RegionMatchMode { STRICT, FUZZY }

    private static class FilterStats {
        int regionFail = 0;
        int typeFail = 0;
        int depositFail = 0;
        int monthlyFail = 0;
        int idMissing = 0;
    }

    private List<Document> filterDocs(
            List<Document> docs,
            List<String> regionKeywords,
            String targetType,
            int limitDeposit,
            int limitMonthly,
            RegionMatchMode mode,
            FilterStats stats
    ) {
        List<Document> out = new ArrayList<>();

        for (Document doc : docs) {
            String content = doc.getText();
            Map<String, Object> meta = doc.getMetadata();
            if (content == null) continue;

            // ✅ 여기서 “쓴 데이터만” 사용
            String regionDepth1 = metaStr(meta, "region_depth1"); // 예: 경기도/서울특별시/부산광역시 ...
            String haystack = normalize(content + " " + regionDepth1);

            // 1) 지역 체크
            if (!regionKeywords.isEmpty()) {
                boolean okRegion = (mode == RegionMatchMode.STRICT)
                        ? matchesRegionStrict(haystack, regionKeywords)
                        : matchesRegionFuzzy(content, regionDepth1, regionKeywords);

                if (!okRegion) {
                    stats.regionFail++;
                    continue;
                }
            }

            // 2) 거래유형 체크: DocumentConverter 기준과 동일하게 price_monthly로 판정
            int dbMonthly = getIntFromMeta(meta, "price_monthly");
            boolean docIsJeonse = (dbMonthly == 0);

            if (!"ALL".equals(targetType)) {
                boolean okType = ("전세".equals(targetType) && docIsJeonse)
                        || ("월세".equals(targetType) && !docIsJeonse);
                if (!okType) {
                    stats.typeFail++;
                    continue;
                }
            }

            // 3) 가격 체크 (메타 그대로)
            int dbDeposit = getIntFromMeta(meta, "price_deposit");
            if (limitDeposit > 0 && dbDeposit > limitDeposit) {
                stats.depositFail++;
                continue;
            }
            if (limitMonthly > 0 && dbMonthly > limitMonthly) {
                stats.monthlyFail++;
                continue;
            }

            // 4) id 체크
            String id = metaStr(meta, "realty_id");
            if (id.isBlank()) {
                stats.idMissing++;
                continue;
            }

            out.add(doc);
            if (out.size() >= TARGET_RESULT_SIZE) break;
        }

        return out;
    }

    // =========================
    // Region Match - STRICT
    // =========================
    private boolean matchesRegionStrict(String haystackNormalized, List<String> regionKeywords) {
        for (String raw : regionKeywords) {
            if (raw == null) continue;
            String kw = normalize(raw);
            if (kw.isBlank()) continue;

            // "서울" <-> "서울특별시" 같은 케이스 완화
            if (kw.equals("서울")) {
                if (haystackNormalized.contains("서울") || haystackNormalized.contains("서울특별시")) return true;
                continue;
            }
            if (kw.equals("부산")) {
                if (haystackNormalized.contains("부산") || haystackNormalized.contains("부산광역시")) return true;
                continue;
            }

            // 동 단위: 역삼동 -> 역삼1동/역삼2동 + stem(역삼) 완화
            if (kw.endsWith("동")) {
                String stem = kw.substring(0, kw.length() - 1);
                if (haystackNormalized.contains(kw)) return true;
                if (haystackNormalized.matches(".*" + stem + "\\d*동.*")) return true;
                if (haystackNormalized.contains(stem)) return true;
                continue;
            }

            // 구 단위: 강남구 -> 강남(완화)
            if (kw.endsWith("구")) {
                String stem = kw.substring(0, kw.length() - 1);
                if (haystackNormalized.contains(kw)) return true;
                if (haystackNormalized.contains(stem)) return true;
                continue;
            }

            // 도로명
            if (kw.endsWith("로") || kw.endsWith("길") || kw.endsWith("대로")) {
                String kwNoNum = kw.replaceAll("\\d+", "");
                if (haystackNormalized.contains(kw)) return true;
                if (!kwNoNum.isBlank() && haystackNormalized.contains(kwNoNum)) return true;
                continue;
            }

            if (haystackNormalized.contains(kw)) return true;
        }
        return false;
    }

    // =========================
    // Region Match - FUZZY (텍스트 유사도 기반)
    // =========================
    private boolean matchesRegionFuzzy(String content, String regionDepth1, List<String> regionKeywords) {
        // 비교 토큰: "키워드: ..." 라인 + region_depth1
        List<String> tokens = new ArrayList<>();
        tokens.addAll(extractKeywordTokensFromContent(content));
        if (regionDepth1 != null && !regionDepth1.isBlank()) tokens.add(regionDepth1.trim());

        // normalize
        List<String> normTokens = tokens.stream()
                .map(this::normalize)
                .filter(s -> !s.isBlank())
                .distinct()
                .collect(Collectors.toList());

        for (String raw : regionKeywords) {
            String kw = normalize(raw);
            if (kw.isBlank()) continue;

            // FUZZY 기준: 토큰과 키워드가 어느 정도 비슷하면 통과
            for (String t : normTokens) {
                if (t.contains(kw) || kw.contains(t)) return true;

                double sim = jaroWinkler(t, kw);
                if (sim >= 0.88) return true; // 여기 값(0.86~0.90)은 튜닝 포인트
            }

            // 동/구는 stem 완화도 같이
            if (kw.endsWith("동") || kw.endsWith("구")) {
                String stem = kw.substring(0, kw.length() - 1);
                for (String t : normTokens) {
                    if (t.contains(stem) || stem.contains(t)) return true;
                    if (jaroWinkler(t, stem) >= 0.88) return true;
                }
            }
        }

        return false;
    }

    private List<String> extractKeywordTokensFromContent(String content) {
        // content 끝에 "키워드: 경기도, 평택시, 서정동, 탄현로" 형태가 들어있음
        if (content == null) return Collections.emptyList();
        int idx = content.lastIndexOf("키워드:");
        if (idx < 0) return Collections.emptyList();

        String line = content.substring(idx).replace("키워드:", "").trim();
        if (line.isBlank()) return Collections.emptyList();

        String[] parts = line.split(",");
        List<String> out = new ArrayList<>();
        for (String p : parts) {
            String t = p.trim();
            if (!t.isEmpty()) out.add(t);
        }
        return out;
    }

    // =========================
    // Jaro-Winkler Similarity (순수 자바 구현)
    // =========================
    private double jaroWinkler(String s1, String s2) {
        if (s1 == null || s2 == null) return 0.0;
        if (s1.equals(s2)) return 1.0;
        if (s1.isEmpty() || s2.isEmpty()) return 0.0;

        int s1Len = s1.length();
        int s2Len = s2.length();
        int matchDistance = Math.max(s1Len, s2Len) / 2 - 1;

        boolean[] s1Matches = new boolean[s1Len];
        boolean[] s2Matches = new boolean[s2Len];

        int matches = 0;
        for (int i = 0; i < s1Len; i++) {
            int start = Math.max(0, i - matchDistance);
            int end = Math.min(i + matchDistance + 1, s2Len);

            for (int j = start; j < end; j++) {
                if (s2Matches[j]) continue;
                if (s1.charAt(i) != s2.charAt(j)) continue;
                s1Matches[i] = true;
                s2Matches[j] = true;
                matches++;
                break;
            }
        }

        if (matches == 0) return 0.0;

        double t = 0;
        int k = 0;
        for (int i = 0; i < s1Len; i++) {
            if (!s1Matches[i]) continue;
            while (!s2Matches[k]) k++;
            if (s1.charAt(i) != s2.charAt(k)) t++;
            k++;
        }
        t /= 2.0;

        double m = matches;
        double jaro = (m / s1Len + m / s2Len + (m - t) / m) / 3.0;

        // winkler boost
        int prefix = 0;
        int maxPrefix = 4;
        for (int i = 0; i < Math.min(Math.min(s1Len, s2Len), maxPrefix); i++) {
            if (s1.charAt(i) == s2.charAt(i)) prefix++;
            else break;
        }
        double p = 0.1;

        return jaro + prefix * p * (1 - jaro);
    }

    // =========================
    // Common Helpers
    // =========================
    private List<String> parseRegionKeywords(String regionPart) {
        if (regionPart == null) return new ArrayList<>();
        String rp = regionPart.trim();
        if (rp.isBlank() || "NONE".equalsIgnoreCase(rp)) return new ArrayList<>();

        List<String> out = new ArrayList<>();
        for (String kw : rp.split(",")) {
            String t = kw.trim();
            if (!t.isEmpty() && !"NONE".equalsIgnoreCase(t)) out.add(t);
        }
        return out;
    }

    private String metaStr(Map<String, Object> meta, String key) {
        if (meta == null) return "";
        Object v = meta.get(key);
        return v == null ? "" : v.toString();
    }

    private String normalize(String s) {
        if (s == null) return "";
        return s.replaceAll("\\s+", "")
                .replaceAll("[^0-9a-zA-Z가-힣]", "");
    }

    private int parseSafeInt(String value) {
        try {
            return Integer.parseInt(value.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    private Integer safeParseInt(String value) {
        try {
            if (value == null) return null;
            String v = value.replaceAll("[^0-9]", "");
            if (v.isBlank()) return null;
            return Integer.parseInt(v);
        } catch (Exception e) {
            return null;
        }
    }

    private int getIntFromMeta(Map<String, Object> meta, String key) {
        if (meta == null || !meta.containsKey(key)) return 0;
        try {
            return Integer.parseInt(meta.get(key).toString());
        } catch (Exception e) {
            return 0;
        }
    }

    protected List<RealtyResponseDto> changeDto(List<Realty> realties) {
        return realties.stream()
                .map(RealtyResponseDto::new)
                .collect(Collectors.toList());
    }
}
