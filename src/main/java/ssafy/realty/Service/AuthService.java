package ssafy.realty.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.realty.Common.ResponseDto;
import ssafy.realty.DTO.Request.UserRequestDto;
import ssafy.realty.DTO.Response.JwtLoginResponseDto;
import ssafy.realty.DTO.Response.UserResponseDto;
import ssafy.realty.Entity.User;
import ssafy.realty.Mapper.UserMapper;
import ssafy.realty.util.JwtUtil;

@Service
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class AuthService {

    private final UserMapper userMapper;
    //    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    // 로그인:JWT 발급 + Refresh를 redis에 저장
//    @Transactional
//    public ResponseDto<?> login(UserRequestDto userRequestDto) {
//        User user = userMapper.findByEmail(userRequestDto.getEmail());
//        if (user == null||!passwordEncoder.matches(userRequestDto.getPassword(), user.getPassword())) {
//            return ResponseDto.create(401,"이메일 또는 비밀번호가 올바르지 않습니다.");
//        }
//
//        JwtLoginResponseDto loginResponse =getResponseDto(user.getEmail(),user.getId(),user);
//
//        return ResponseDto.create(200,"로그인 성공",loginResponse);
//    }

    // refresh 토큰으로 access, refresh 재발급
    @Transactional
    public ResponseDto<JwtLoginResponseDto> reissue(String refreshTokenHeader) {
        if (!jwtUtil.validateToken(refreshTokenHeader)) {
            return ResponseDto.create(401, "유효하지 않은 리프레시 토큰입니다.");
        }

        String tokenType = jwtUtil.extractTokenType(refreshTokenHeader);
        if (!"refresh".equals(tokenType)) {
            return ResponseDto.create(400, "리프레시 토큰이 아닙니다.");
        }

        Integer userId = jwtUtil.extractUserId(refreshTokenHeader);
        String email = jwtUtil.extractEmail(refreshTokenHeader);
        if (userId == null) {
            return ResponseDto.create(401, "리프레시 토큰에서 사용자 정보를 찾을 수 없습니다.");
        }

        String storedRefreshToken = tokenService.getRefreshToken(userId);
        String incomingRawToken = jwtUtil.extractRawToken(refreshTokenHeader);

        if (storedRefreshToken == null || !storedRefreshToken.equals(incomingRawToken)) {
            return ResponseDto.create(401, "리프레시 토큰이 저장된 정보와 일치하지 않습니다.");
        }

        User user = userMapper.findById(userId);
        if (user == null) {
            return ResponseDto.create(404, "사용자를 찾을 수 없습니다");
        }
        JwtLoginResponseDto loginResponse = getResponseDto(email, userId, user);

        return ResponseDto.create(200, "토큰 재발급 성공", loginResponse);
    }

    // 로그아웃 : refresh 삭제, Access 블랙리스트 등록
    @Transactional
    public ResponseDto<?> logout(String accessTokenHeader, String refreshTokenHeader) {
        if (refreshTokenHeader != null && jwtUtil.validateToken(refreshTokenHeader)) {
            String tokenType = jwtUtil.extractTokenType(refreshTokenHeader);
            if ("refresh".equals(tokenType)) {
                Integer userId = jwtUtil.extractUserId(refreshTokenHeader);
                if (userId != null) {
                    tokenService.deleteRefreshToken(userId);
                }
            }
        }

        if (accessTokenHeader != null && jwtUtil.validateToken(accessTokenHeader)) {
            tokenService.blacklistAccessToken(accessTokenHeader);
        }

        return ResponseDto.create(200, "로그아웃 성공");
    }

    private JwtLoginResponseDto getResponseDto(String email, Integer userId, User user) {
        String accessToken = jwtUtil.createAccessToken(email, userId);
        String refreshToken = jwtUtil.createRefreshToken(email, userId);
        tokenService.saveRefreshToken(userId, refreshToken);

        UserResponseDto userResponse = new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getAge(),
                user.getEmail(),
                user.getBirthDate()
        );

        JwtLoginResponseDto loginResponse = new JwtLoginResponseDto(
                accessToken,
                refreshToken,
                userResponse
        );

        return loginResponse;
    }


}
