package ssafy.realty.DTO.Request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewInsertDto {
    private int userId;
    private int realtyId;
    private int rating;
    private String text;
}
