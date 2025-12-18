package ssafy.realty.Entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchHistory {
    private int id;
    private int userId;
    private String text;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

}
