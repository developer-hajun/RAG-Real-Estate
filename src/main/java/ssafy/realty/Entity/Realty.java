package ssafy.realty.Entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Realty {
    private int id;

    private String address;
    private String name;
    private int e_price;
    private int month_price;
    private float y_coordinate;
    private float x_coordinate;
    private List<Review> reviewList;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    private float exclusiveArea;

}
