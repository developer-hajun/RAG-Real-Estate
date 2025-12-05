package ssafy.realty.DTO.Response;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@ToString
public class RealtyRecommendationResponse {
    private String aiMessage;
    private List<RealtyResponseDto> realties;
}