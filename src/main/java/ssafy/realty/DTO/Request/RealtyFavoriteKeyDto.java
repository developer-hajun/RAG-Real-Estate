package ssafy.realty.DTO.Request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RealtyFavoriteKeyDto {
    private int userId;
    private int realtyId;
}
