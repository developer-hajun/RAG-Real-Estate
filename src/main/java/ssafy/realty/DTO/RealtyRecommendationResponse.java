package ssafy.realty.DTO;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import ssafy.realty.Entity.Realty;
import java.util.List;

@Data
@Builder
@ToString
public class RealtyRecommendationResponse {
    private String aiMessage;       // AI가 생성한 추천 멘트
    private List<RealtyResponseDto> realties;  // 추천된 실제 매물 데이터 리스트
}