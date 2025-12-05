package ssafy.realty.DTO.Response;

import lombok.*;
import ssafy.realty.Entity.Post;

import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
    private int id;
    private String title;
    private String text;
    private List<CommentResponseDto> commentDtos;

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.text = post.getText();
    }
}