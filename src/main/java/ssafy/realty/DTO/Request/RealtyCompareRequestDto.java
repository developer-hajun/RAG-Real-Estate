package ssafy.realty.DTO.Request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RealtyCompareRequestDto {
    private List<Integer> ids;
    private Integer userId; // JWT (선택)
}
