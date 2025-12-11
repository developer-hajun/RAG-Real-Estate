package ssafy.realty.DTO.Request;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateRequestDto {
    private int userId;
    private int rating; // 별점
    private String text; // 리뷰 내용
}
