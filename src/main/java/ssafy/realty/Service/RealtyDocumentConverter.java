package ssafy.realty.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 로깅을 위해 추가 추천
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import ssafy.realty.DTO.Response.RealtyResponseDto;
import ssafy.realty.Entity.Realty;
import ssafy.realty.Mapper.RealtyMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            log.error("배치 저장 중 오류 발생: {}", e.getMessage());
        }
    }

    public Document toDocument(Realty realty) {
        String content;

        // [수정 포인트 1] 전세 판단 기준 변경
        // 월세(month_price)가 0원이면 '전세', 아니면 '월세'로 판단
        boolean isJeonse = (realty.getMonth_price() == 0);

        if (isJeonse) {
            // 전세인 경우: 보증금은 e_price입니다.
            content = String.format(
                    "이 매물의 이름은 %s이고, 주소는 %s입니다. 보증금 %d만원인 전세 매물입니다.",
                    realty.getName(),
                    realty.getAddress(),
                    realty.getE_price() // 전세금(보증금)
            );
        } else {
            // 월세인 경우
            content = String.format(
                    "이 매물의 이름은 %s이고, 주소는 %s입니다. 보증금 %d만원에 월세 %d만원인 월세 매물입니다.", // '월세 매물' 명시
                    realty.getName(),
                    realty.getAddress(),
                    realty.getE_price(),     // 보증금
                    realty.getMonth_price()  // 월세
            );
        }

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("realty_id", String.valueOf(realty.getId()));

        // [수정 포인트 2] 메타데이터 저장 시 헷갈리지 않게 저장
        metadata.put("price_deposit", realty.getE_price());     // 보증금
        metadata.put("price_monthly", realty.getMonth_price()); // 월세
        metadata.put("location_lat", realty.getY_coordinate());
        metadata.put("location_lon", realty.getX_coordinate());
        metadata.put("region_depth1", extractRegion(realty.getAddress()));

        return new Document(content, metadata);
    }

    private String extractRegion(String address) {
        if (address == null || address.isEmpty()) return "Unknown";
        return address.split(" ")[0];
    }

    protected List<RealtyResponseDto> changeDto(List<Realty> realties){
        return realties.stream()
                .map(RealtyResponseDto::new)
                .collect(Collectors.toList());
    }


}