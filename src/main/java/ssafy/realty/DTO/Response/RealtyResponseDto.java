package ssafy.realty.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import ssafy.realty.Entity.Realty;

import java.util.List;

@Data
@AllArgsConstructor
public class RealtyResponseDto {
    private int id;
    private String address;
    private String name;
    private int e_price;
    private int month_price;
    private float y_coordinate;
    private float x_coordinate;
    private List<ReviewResponseDto> reviewList;

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
