package ssafy.realty.DTO.Response;

import lombok.*;
import ssafy.realty.Entity.Comment;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
    private int id;
    private String content;
    private CommentResponseDto parentComment;
    private LocalDateTime updatedDate;
    private List<CommentResponseDto> replies;
    private String userName;

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.updatedDate = comment.getUpdatedDate();
    }
}
