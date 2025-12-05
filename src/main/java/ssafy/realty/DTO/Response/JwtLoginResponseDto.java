package ssafy.realty.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtLoginResponseDto {
    private String accessToken;
    private String refreshToken;
    private UserResponseDto user;


}
