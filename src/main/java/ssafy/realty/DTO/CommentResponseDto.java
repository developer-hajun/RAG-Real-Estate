package ssafy.realty.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import ssafy.realty.Entity.Comment;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentResponseDto {
    private int id;
    private String content;
    private CommentResponseDto parentComment;
    private LocalDateTime updatedDate;
}
