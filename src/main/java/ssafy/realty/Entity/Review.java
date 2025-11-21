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
public class Review {
    private int id;

    private int rating;
    private Realty realty;
    private User user;

    private String title;
    private String text;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

}
