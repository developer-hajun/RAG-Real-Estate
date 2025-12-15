package ssafy.realty.DTO.Request;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RealtySearchRequestDto {
    private String address; // 주소 검색
    private String name; // 매물명 검색

    // 보증금
    private Integer min_e_price;
    private Integer max_e_price;

    // 월세
    private Integer min_monthly_price;
    private Integer max_monthly_price;

    // 페이징
    private int page = 0;
    private int size = 0;

    // ===== 서버 내부용(클라가 안 보내도 됨) =====
    private Integer userId; // JWT에서 세팅
    private Integer offset; // service에서 page*size 계산해서 세팅

}
