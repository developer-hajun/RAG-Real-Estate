package ssafy.realty.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ssafy.realty.Entity.Realty;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class RealtyResponseDto {
    private int id;
    private String address;
    private String name;
    private int e_price;
    private int month_price;

    // 좌표 정보
    private float y_coordinate;
    private float x_coordinate;

    // 리뷰 리스트
    private List<ReviewResponseDto> reviewList;

    // 부가 정보
    private double ratingAvg;
    private int reviewCount;
    private boolean isFavorite; // 찜 여부

    // 목록 조회용 Dto
    public RealtyResponseDto(int id, String address, String name, int e_price, int month_price, int reviewCount, float ratingAvg, boolean isFavorite) {
        this.id = id;
        this.address = address;
        this.name = name;
        this.e_price = e_price;
        this.month_price = month_price;
        this.reviewCount = reviewCount;
        this.ratingAvg = ratingAvg;
        this.isFavorite = isFavorite;
    }

    public RealtyResponseDto(Realty realty) {
        this.address = realty.getAddress();
        this.id = realty.getId();
        this.name = realty.getName();
        this.e_price = realty.getE_price();
        this.month_price = realty.getMonth_price();
        this.y_coordinate = realty.getY_coordinate();
        this.x_coordinate = realty.getX_coordinate();
    }
}
