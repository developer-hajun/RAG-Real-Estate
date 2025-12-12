package ssafy.realty.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssafy.realty.Common.ResponseDto;
import ssafy.realty.DTO.Request.UserRequestDto;
import ssafy.realty.DTO.Response.UserResponseDto;
import ssafy.realty.Service.AuthService;
import ssafy.realty.Service.UserService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    // 회원가입
    // 나중에 볼거 회원가입 실패는 왜 없지 ?
    @PostMapping("/register")
    public ResponseEntity<ResponseDto<?>> register(@RequestBody UserRequestDto userRequestDto) {
        UserResponseDto userResponseDto = userService.register(userRequestDto);
        if(userResponseDto==null){
            return ResponseEntity.ok(ResponseDto.create(400,"이미 존재하는 이메일입니다."));
        }
        return ResponseEntity.ok(ResponseDto.create(201, "회원가입 성공", userResponseDto));
    }

    // 로그인이 없는 이유 는 JwtAuthenticationFilter가 /api/auth/login 요청을 가로채서 처리하기 때문입니다.
    // 필터에서 Resp응onseDto<jwtLoginResponseDto>를 생성하여 답을 반환합니다.

    //토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity<ResponseDto<?>> reissue(@RequestHeader("Refresh-Token") String refreshTokenHeader) {
        ResponseDto<?> responseDto = authService.reissue(refreshTokenHeader);
        return ResponseEntity.ok(responseDto);
    }

    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ResponseDto<?>> logout(@RequestHeader("Authorization") String accessTokenHeader,
                                                 @RequestHeader("Refresh-Token") String refreshTokenHeader) {
        ResponseDto<?> responseDto = authService.logout(accessTokenHeader, refreshTokenHeader);
        return ResponseEntity.ok(responseDto);
    }


}
