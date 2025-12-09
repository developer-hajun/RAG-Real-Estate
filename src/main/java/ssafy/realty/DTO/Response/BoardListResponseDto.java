package ssafy.realty.DTO.Response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardListResponseDto {
    private int id;
    private String title;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;


}