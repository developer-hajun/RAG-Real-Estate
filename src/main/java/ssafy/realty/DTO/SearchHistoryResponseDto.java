package ssafy.realty.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchHistoryResponseDto {
    private Integer id;
    private String text;
    private LocalDateTime createdDate;
}
