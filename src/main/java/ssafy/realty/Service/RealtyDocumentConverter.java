package ssafy.realty.Service;

import org.springframework.ai.document.Document;
import ssafy.realty.Entity.Realty;

import java.util.HashMap;
import java.util.Map;

public class RealtyDocumentConverter {

    public Document toDocument(Realty realty) {

        String content = String.format(
            "이 매물의 이름은 %s이고, 주소는 %s입니다. 보증금 %d만원에 월세 %d만원인 매물입니다.",
            realty.getName(),
            realty.getAddress(),
            realty.getE_price(),
            realty.getMonth_price()
        );

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("realty_id", realty.getId());
        metadata.put("price_deposit", realty.getE_price());
        metadata.put("price_monthly", realty.getMonth_price());
        metadata.put("location_lat", realty.getY_coordinate());
        metadata.put("location_lon", realty.getX_coordinate());
        metadata.put("region_depth1", extractRegion(realty.getAddress()));

        return new Document(content, metadata);
    }

    private String extractRegion(String address) {
        return address.split(" ")[0]; //아직 어떻게 필터링 할지 고민중임 -> 추후 수정 가능
    }
}