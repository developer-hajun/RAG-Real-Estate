package ssafy.realty.DTO.Response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UserResponseDto {
    private Integer id;
    private String name;
    private Integer age;
    private String email;
    private LocalDateTime birthDate;

    // 프로필 조회 응답
    public UserResponseDto(Integer id, String name, Integer age, String email, LocalDateTime birthDate) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
        this.birthDate = birthDate;
    }


}
