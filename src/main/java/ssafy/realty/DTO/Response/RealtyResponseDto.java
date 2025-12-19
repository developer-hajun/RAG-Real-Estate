package ssafy.realty.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ssafy.realty.Entity.Realty;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RealtyResponseDto {
    private int id;
    private String address;
    private String name;
    private int ePrice; // 카멜케이스로 수정
    private int monthPrice;


    // 프론트 표시용(RealtyListResponseDto에 있던 거 통합)
    private String priceInfo;

    // 좌표 정보
    private double yCoordinate; // 카멜케이스로 수정
    private double xCoordinate;

    // 리뷰 리스트
    private List<ReviewResponseDto> reviewList;

    // 부가 정보
    private BigDecimal ratingAvg;
    private long reviewCount;
    private long isFavorite; // 찜 여부

    // 전용 면적
    private float exclusiveArea;

//    // 목록 조회용 Dto
//    public RealtyResponseDto(int id, String address, String name, int e_price, int month_price, int reviewCount, float ratingAvg, boolean isFavorite) {
//        this.id = id;
//        this.address = address;
//        this.name = name;
//        this.e_price = e_price;
//        this.month_price = month_price;
//        this.reviewCount = reviewCount;
//        this.ratingAvg = ratingAvg;
//        this.isFavorite = isFavorite;
//    }

    public RealtyResponseDto(Realty realty) {
        this.address = realty.getAddress();
        this.id = realty.getId();
        this.name = realty.getName();
        this.ePrice = realty.getE_price();
        this.monthPrice = realty.getMonth_price();
        this.yCoordinate = realty.getY_coordinate();
        this.xCoordinate = realty.getX_coordinate();
        this.exclusiveArea = realty.getExclusiveArea();

        // priceInfo -> 프론트에서 가격 표시 편의를 위한 변수
        this.priceInfo = (this.monthPrice == 0)
                ? ("전세 " + this.ePrice)
                : (this.ePrice + " / " + this.monthPrice);
    }
}
