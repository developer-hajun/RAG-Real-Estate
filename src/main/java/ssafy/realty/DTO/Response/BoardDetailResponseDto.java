package ssafy.realty.DTO.Response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections; // Collections.emptyList() 사용을 위해 추가

import lombok.*;

import ssafy.realty.Entity.Board; // Board 엔티티 경로를 가정합니다.

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDetailResponseDto {
    private int id;
    private String title;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private List<PostResponseDto> posts;

    public BoardDetailResponseDto(String title, int id, LocalDateTime createdDate, LocalDateTime updatedDate) {
        this.title = title;
        this.id = id;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }
}