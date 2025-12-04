package ssafy.realty.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

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
    private List<ReviewDto> reviewList;


}
