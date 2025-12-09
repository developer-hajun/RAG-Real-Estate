package ssafy.realty.DTO.Response;

import lombok.*;
import ssafy.realty.Entity.Post;

import java.util.List;
import java.util.stream.Collectors;

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

    public static PostResponseDto fromEntity(Post post) {

        List<CommentResponseDto> commentDtos = null;

        return new PostResponseDto(
                post.getId(),
                post.getTitle(),
                post.getText(),
                commentDtos
        );
    }
}