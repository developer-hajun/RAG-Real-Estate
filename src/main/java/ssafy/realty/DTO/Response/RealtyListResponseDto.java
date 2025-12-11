package ssafy.realty.DTO.Response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RealtyListResponseDto {
    private int id;
    private String name;
    private String address;

    private int e_price;
    private int month_price;

    // 풇ㄴ트엔드 표시 편의를 위한 추가 필드 <- 나쁘지 않아보여서 추가함
    private String priceInfo; // ex) "전세 2억", "1000/50"
    private double ratingAvg; // 평균 평점
}
