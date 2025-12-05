package ssafy.realty.DTO.Request;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {
    private int id;
    private String content;
    private int parentsId;
    private int postId;
}