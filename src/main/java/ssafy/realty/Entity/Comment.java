package ssafy.realty.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private int id;
    private Post post;
    private User user;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private Comment parentComment;
    private List<Comment> replies;

}
