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
public class Board {
    private int id;
    private String title;
    private List<Post> posts;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
