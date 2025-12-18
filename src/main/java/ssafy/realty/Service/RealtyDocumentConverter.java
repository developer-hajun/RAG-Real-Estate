package ssafy.realty.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import ssafy.realty.DTO.Response.RealtyResponseDto;
import ssafy.realty.Entity.Realty;
import ssafy.realty.Mapper.RealtyMapper;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RealtyDocumentConverter {

    private final RealtyMapper realtyMapper;
    private final VectorStore vectorStore;

    private static final int BATCH_SIZE = 1000;

    public void convertAndUploadAll() {
        List<Realty> realities = realtyMapper.selectAllRealty();
        log.info("총 {}개의 매물 데이터를 조회했습니다.", realities.size());

        List<Document> documentBatch = new ArrayList<>();

        for (int i = 0; i < realities.size(); i++) {
            documentBatch.add(toDocument(realities.get(i)));

            if (documentBatch.size() >= BATCH_SIZE || i == realities.size() - 1) {
                uploadBatch(documentBatch);
                documentBatch.clear();
            }
        }

        log.info("모든 데이터 업로드 완료");
    }

    private void uploadBatch(List<Document> batch) {
        if (batch.isEmpty()) return;
        try {
            vectorStore.add(batch);
            log.info("{}개 데이터 배치 저장 완료", batch.size());
        } catch (Exception e) {
            log.error("배치 저장 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    public Document toDocument(Realty realty) {
        // 전세 판단: 월세가 0이면 전세
        boolean isJeonse = (realty.getMonth_price() == 0);

        String base;
        if (isJeonse) {
            base = String.format(
                    "이 매물의 이름은 %s이고, 주소는 %s입니다. 보증금 %d만원인 전세 매물입니다.",
                    realty.getName(),
                    realty.getAddress(),
                    realty.getE_price()
            );
        } else {
            base = String.format(
                    "이 매물의 이름은 %s이고, 주소는 %s입니다. 보증금 %d만원에 월세 %d만원인 월세 매물입니다.",
                    realty.getName(),
                    realty.getAddress(),
                    realty.getE_price(),
                    realty.getMonth_price()
            );
        }

        // ✅ 핵심: 주소에서 동/구/도로명(~~로/~~길/~~대로) 키워드를 뽑아 content에 같이 넣음
        // => 이후 RagService에서 content.contains(...)가 훨씬 잘 걸림
        String keywordLine = buildAddressKeywordLine(realty.getAddress());

        String content = base + "\n" + keywordLine;

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("realty_id", String.valueOf(realty.getId()));

        // 너가 쓰는 메타데이터 그대로 유지
        metadata.put("price_deposit", realty.getE_price());        // 보증금
        metadata.put("price_monthly", realty.getMonth_price());    // 월세
        metadata.put("location_lat", realty.getY_coordinate());
        metadata.put("location_lon", realty.getX_coordinate());
        metadata.put("region_depth1", extractRegion(realty.getAddress()));

        return new Document(content, metadata);
    }

    private String extractRegion(String address) {
        if (address == null || address.isEmpty()) return "Unknown";
        String[] tokens = address.trim().split("\\s+");
        return tokens.length > 0 ? tokens[0] : "Unknown";
    }

    /**
     * 주소에서 검색에 도움되는 키워드들을 뽑아 "키워드: ..." 형태로 반환
     * - 동/구/시/군 같은 행정단위
     * - 도로명: ~~로/~~길/~~대로 (숫자 붙어도 제거해서 같이 넣음)
     *
     * ※ metadata는 늘리지 않고 content만 강화 (요구사항: 여기서 쓴 데이터만 사용)
     */
    private String buildAddressKeywordLine(String address) {
        if (address == null) return "키워드: ";
        String a = address.trim();
        if (a.isEmpty()) return "키워드: ";

        String[] tokens = a.split("\\s+");
        LinkedHashSet<String> keywords = new LinkedHashSet<>();

        for (String t : tokens) {
            if (t == null) continue;
            String token = t.trim();
            if (token.isEmpty()) continue;

            // 괄호/쉼표 등 간단 제거
            token = token.replaceAll("[(),]", "");

            // 행정단위 키워드
            if (token.endsWith("시") || token.endsWith("도") || token.endsWith("군") || token.endsWith("구")
                    || token.endsWith("동") || token.endsWith("읍") || token.endsWith("면")) {
                keywords.add(token);
            }

            // 도로명 키워드
            if (token.endsWith("로") || token.endsWith("길") || token.endsWith("대로")) {
                keywords.add(token);

                // "테헤란로152" 같이 숫자 붙은 케이스 대비: 숫자 제거 버전도 같이
                String noNum = token.replaceAll("\\d+", "");
                if (!noNum.isBlank()) keywords.add(noNum);
            }
        }

        // 상위 토큰들도(시/구 같은 경우) 있으면 도움이 되니까 앞에서 몇 개는 무조건 넣어둠
        for (int i = 0; i < Math.min(3, tokens.length); i++) {
            String t = tokens[i].replaceAll("[(),]", "").trim();
            if (!t.isEmpty()) keywords.add(t);
        }

        return "키워드: " + String.join(", ", keywords);
    }

    protected List<RealtyResponseDto> changeDto(List<Realty> realties) {
        return realties.stream()
                .map(RealtyResponseDto::new)
                .collect(Collectors.toList());
    }
}
