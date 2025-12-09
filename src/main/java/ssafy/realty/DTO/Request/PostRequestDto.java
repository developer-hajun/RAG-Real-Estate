package ssafy.realty.DTO.Request;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {

    private int id;
    private int UserId;
    private String title;
    private String text;
    private int boardId;
}
