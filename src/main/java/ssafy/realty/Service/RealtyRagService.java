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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RealtyRagService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final RealtyMapper realtyMapper;

    public RealtyRecommendationResponse getRealtyRecommendation(String userQuery) {

        // [STEP 1] 사용자 질문에서 '지역', '유형', '가격상한' 추출 (LLM 사용)
        // 응답 형식: "지역|유형|최대보증금|최대월세" (단위: 만원, 없으면 0)
        String keywordPrompt = """
            질문을 분석하여 "지역|거래유형|최대보증금|최대월세" 형식으로 답해.
            
            [규칙]
            1. 지역: 행정구역 명칭 (예: 부산, 강남구). 없으면 "NONE"
            2. 거래유형: "전세", "월세". 모르면 "ALL"
            3. 최대보증금: 만원 단위 숫자. (예: 3억 -> 30000). 조건 없으면 0
            4. 최대월세: 만원 단위 숫자. 조건 없으면 0
            
            [예시]
            "부산 전세 3억 이하" -> 부산|전세|30000|0
            "서울 월세 2000에 50" -> 서울|월세|2000|50
            "강남 오피스텔 추천" -> 강남|ALL|0|0
            """;

        String llmResponse = chatClient.prompt()
                .system(keywordPrompt)
                .user(userQuery)
                .call()
                .content()
                .trim();

        log.info("LLM 분석 결과: {}", llmResponse);

        // 파싱 및 변수 할당
        String[] parts = llmResponse.split("\\|");
        String targetRegion = parts.length > 0 ? parts[0].trim() : "NONE";
        String targetType = parts.length > 1 ? parts[1].trim() : "ALL";
        int limitDeposit = parts.length > 2 ? parseSafeInt(parts[2]) : 0; // 0이면 제한 없음
        int limitMonthly = parts.length > 3 ? parseSafeInt(parts[3]) : 0; // 0이면 제한 없음


        // [STEP 2] 유사도 검색 (필터 없이 아주 넓게 검색)
        // 가격 조건까지 맞추려면 많이 탈락할 수 있으므로 100개를 가져옵니다.
        SearchRequest searchRequest = SearchRequest.builder()
                .query(userQuery)
                .topK(100)
                .build();

        List<Document> allDocuments = vectorStore.similaritySearch(searchRequest);


        // [STEP 3] Java 코드레벨 필터링 (지역 & 유형 & 가격)
        List<Document> filteredDocuments = new ArrayList<>();

        for (Document doc : allDocuments) {
            String content = doc.getText();
            Map<String, Object> meta = doc.getMetadata();

            if (content == null) continue;

            // 1. 지역 체크 (포함 여부)
            if (!"NONE".equals(targetRegion) && !content.contains(targetRegion)) {
                continue; // 지역 불일치 -> 탈락
            }

            // 2. 거래유형 체크 (포함 여부)
            if (!"ALL".equals(targetType) && !content.contains(targetType)) {
                continue; // 유형 불일치 -> 탈락
            }

            // 3. 가격 체크 (메타데이터 활용 + 기존 데이터 버그 보정)
            // 기존 데이터: 전세일 경우 price_deposit이 0이고 price_monthly에 전세금이 들어있음
            int dbDeposit = getIntFromMeta(meta, "price_deposit");
            int dbMonthly = getIntFromMeta(meta, "price_monthly");

            int realDeposit = dbDeposit;
            int realMonthly = dbMonthly;

            // 데이터 보정 로직 (전세가 월세칸에 저장된 경우 처리)
            if (dbDeposit == 0 && dbMonthly > 0) {
                // 전세로 추정 -> 월세값을 보증금으로 인식
                realDeposit = dbMonthly;
                realMonthly = 0;
            }

            // 보증금 상한 체크 (0이 아니면 체크)
            if (limitDeposit > 0 && realDeposit > limitDeposit) {
                continue; // 예산 초과 -> 탈락
            }

            // 월세 상한 체크 (0이 아니면 체크)
            if (limitMonthly > 0 && realMonthly > limitMonthly) {
                continue; // 월세 초과 -> 탈락
            }

            // 모든 관문 통과
            filteredDocuments.add(doc);

            if (filteredDocuments.size() >= 5) break; // 5개 찾으면 끝
        }


        // 결과 없음 처리
        if (filteredDocuments.isEmpty()) {
            return RealtyRecommendationResponse.builder()
                    .aiMessage("죄송합니다. 조건에 맞는 매물을 찾을 수 없습니다. (조건: " + llmResponse + ")")
                    .realties(new ArrayList<>())
                    .build();
        }

        // [STEP 4] 매물 ID 추출 및 DB 조회
        List<Integer> realtyIds = filteredDocuments.stream()
                .map(doc -> {
                    Object idObj = doc.getMetadata().get("realty_id");
                    return Integer.parseInt(idObj.toString());
                })
                .collect(Collectors.toList());

        List<Realty> recommendedRealties = realtyMapper.selectRealtyListByIds(realtyIds);

        // [STEP 5] AI 응답 생성
        String context = filteredDocuments.stream()
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

    // 안전한 정수 파싱
    private int parseSafeInt(String value) {
        try {
            return Integer.parseInt(value.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    // 메타데이터에서 정수 추출 (형변환 안전장치)
    private int getIntFromMeta(Map<String, Object> meta, String key) {
        if (meta == null || !meta.containsKey(key)) return 0;
        try {
            return Integer.parseInt(meta.get(key).toString());
        } catch (Exception e) {
            return 0;
        }
    }

    protected List<RealtyResponseDto> changeDto(List<Realty> realties){
        return realties.stream()
                .map(RealtyResponseDto::new)
                .collect(Collectors.toList());
    }
}