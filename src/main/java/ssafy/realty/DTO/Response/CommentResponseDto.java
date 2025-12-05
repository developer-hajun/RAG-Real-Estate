package ssafy.realty.DTO.Response;

import lombok.*;

import java.time.LocalDateTime;

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
}
