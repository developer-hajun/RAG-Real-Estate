package ssafy.realty.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import ssafy.realty.Entity.Post;

import java.util.List;

@Data
@AllArgsConstructor
public class PostResponseDto {
    private int id;
    private String title;
    private String text;
    private List<CommentResponseDto> commentDtos;

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.text = post.getTitle();
    }

}
