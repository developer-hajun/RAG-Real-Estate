package ssafy.realty.DTO.Response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections; // Collections.emptyList() 사용을 위해 추가

import lombok.AllArgsConstructor; // ✨ 모든 필드를 받는 생성자 추가
import lombok.Getter;
import lombok.NoArgsConstructor; // 기본 생성자 필요 시 추가
import lombok.Setter; // 필요하다면 추가 (Response DTO에서는 보통 제외 권장)

import ssafy.realty.Entity.Board; // Board 엔티티 경로를 가정합니다.

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardDetailResponseDto {
    private int id;
    private String title;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private List<PostResponseDto> posts;

}