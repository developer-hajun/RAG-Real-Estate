package ssafy.realty.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    private int id;
    private String title;
    private String text;
    private User user;
    private List<Comment> commentList;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public Post(int id, String title, String text, LocalDateTime createdDate, LocalDateTime updatedDate) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }
}
