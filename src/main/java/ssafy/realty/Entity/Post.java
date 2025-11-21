package ssafy.realty.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    private int id;
    private String title;
    private String text;
    private User user;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
