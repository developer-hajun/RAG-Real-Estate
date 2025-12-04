package ssafy.realty.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentRequestDto {
    private String content;
    private int  parentsId;
    private int postId;

}
