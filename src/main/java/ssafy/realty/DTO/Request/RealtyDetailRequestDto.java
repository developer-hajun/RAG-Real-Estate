package ssafy.realty.DTO.Request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RealtyDetailRequestDto {
    private int id;          // realtyId
    private Integer userId;  // JWT
}
