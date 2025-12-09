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

    private static final int BATCH_SIZE = 10;

    public void convertAndUploadAll() {
        List<Realty> realities = realtyMapper.selectAllRealty();

        log.info("총 {}개의 매물 데이터를 조회했습니다.", realities.size());

        List<Document> documentBatch = new ArrayList<>();

        for (int i = 0; i < realities.size(); i++) {
            documentBatch.add(toDocument(realities.get(i)));

            if (documentBatch.size() >= BATCH_SIZE || i == realities.size() - 1) {
                uploadBatch(documentBatch);
                documentBatch.clear();
                break;
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
        String content = String.format(
                "이 매물의 이름은 %s이고, 주소는 %s입니다. 보증금 %d만원에 월세 %d만원인 매물입니다.",
                realty.getName(),
                realty.getAddress(),
                realty.getE_price(),
                realty.getMonth_price()
        );

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("realty_id", String.valueOf(realty.getId()));
        metadata.put("price_deposit", realty.getE_price());
        metadata.put("price_monthly", realty.getMonth_price());
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