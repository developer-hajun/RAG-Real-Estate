package ssafy.realty.DTO.Request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UserRequestDto {
    private String email;
    private String password;
    private String name;
    private Integer age;
    private LocalDateTime birthDate;

    // 로그인 요청 생성자
    public UserRequestDto(String email, String password){
        this.email = email;
        this.password = password;
    }
    // 회원가입 요청 생성자
    public UserRequestDto(String email, String password, String name,Integer age, LocalDateTime birthDate){
        this.email = email;
        this.password = password;
        this.name = name;
        this.age = age;
        this.birthDate = birthDate;
    }
    // 프로필 수정 요청 생성자
    public UserRequestDto(String name, Integer age, String email, LocalDateTime birthDate){
        this.name = name;
        this.age = age;
        this.email = email;
        this.birthDate = birthDate;
    }
}
