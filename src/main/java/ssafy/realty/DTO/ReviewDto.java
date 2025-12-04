package ssafy.realty.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import ssafy.realty.Entity.Realty;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ReviewDto {
    private int id;
    private int rating;
    private String title;
    private String text;
    private LocalDateTime updatedDate;
}
