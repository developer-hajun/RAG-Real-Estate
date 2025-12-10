package ssafy.realty;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ssafy.realty.Common.ResponseDto;
import ssafy.realty.DTO.Response.JwtLoginResponseDto;
import ssafy.realty.DTO.Response.UserResponseDto;
import ssafy.realty.Entity.User;
import ssafy.realty.Mapper.UserMapper;
import ssafy.realty.Service.AuthService;
import ssafy.realty.Service.TokenService;
import ssafy.realty.util.JwtUtil;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthService authService;

    private final String refreshHeader = "Bearer refreshTokenHeader";

    @BeforeEach
    void setup(){
    }

    @Test
    @DisplayName("reissue: 유효하지 않은 토큰이면 401 반환")
    void reissue_InvalidToken() {
        when(jwtUtil.validateToken(refreshHeader)).thenReturn(false);

        ResponseDto<JwtLoginResponseDto> res = authService.reissue(refreshHeader);

        assertNotNull(res);
        assertEquals(401, res.getStatusCode());
        assertEquals("유효하지 않은 리프레시 토큰입니다.", res.getMessage());

        verify(jwtUtil).validateToken(refreshHeader);
        verifyNoMoreInteractions(userMapper, tokenService, jwtUtil);
    }

    @Test
    @DisplayName("reissue: 리프레시 토큰이 아니면 400 반환")
    void reissue_NotRefreshToken() {
        when(jwtUtil.validateToken(refreshHeader)).thenReturn(true);
        when(jwtUtil.extractTokenType(refreshHeader)).thenReturn("access");

        ResponseDto<JwtLoginResponseDto> res = authService.reissue(refreshHeader);

        assertEquals(400, res.getStatusCode());
        assertEquals("리프레시 토큰이 아닙니다.", res.getMessage());

        verify(jwtUtil).validateToken(refreshHeader);
        verify(jwtUtil).extractTokenType(refreshHeader);
        verifyNoMoreInteractions(userMapper, tokenService, jwtUtil);
    }

    @Test
    @DisplayName("reissue: 저장된 토큰과 불일치하면 401 반환")
    void reissue_StoredTokenMismatch() {
        when(jwtUtil.validateToken(refreshHeader)).thenReturn(true);
        when(jwtUtil.extractTokenType(refreshHeader)).thenReturn("refresh");
        when(jwtUtil.extractUserId(refreshHeader)).thenReturn(1);
        when(jwtUtil.extractEmail(refreshHeader)).thenReturn("a@b.com");

        when(tokenService.getRefreshToken(1)).thenReturn("storedRaw");
        when(jwtUtil.extractRawToken(refreshHeader)).thenReturn("incomingRaw");

        ResponseDto<JwtLoginResponseDto> res = authService.reissue(refreshHeader);

        assertEquals(401, res.getStatusCode());
        assertEquals("리프레시 토큰이 저장된 정보와 일치하지 않습니다.", res.getMessage());

        verify(tokenService).getRefreshToken(1);
    }

    @Test
    @DisplayName("reissue: 사용자를 찾을 수 없으면 404 반환")
    void reissue_UserNotFound() {
        when(jwtUtil.validateToken(refreshHeader)).thenReturn(true);
        when(jwtUtil.extractTokenType(refreshHeader)).thenReturn("refresh");
        when(jwtUtil.extractUserId(refreshHeader)).thenReturn(2);
        when(jwtUtil.extractEmail(refreshHeader)).thenReturn("a@b.com");
        when(tokenService.getRefreshToken(2)).thenReturn("raw");
        when(jwtUtil.extractRawToken(refreshHeader)).thenReturn("raw");

        when(userMapper.findById(2)).thenReturn(null);

        ResponseDto<JwtLoginResponseDto> res = authService.reissue(refreshHeader);

        assertEquals(404, res.getStatusCode());
        assertEquals("사용자를 찾을 수 없습니다", res.getMessage());

        verify(userMapper).findById(2);
    }

    @Test
    @DisplayName("reissue: 성공 시 200 반환 및 새로운 리프레시 토큰 저장")
    void reissue_Success() {
        when(jwtUtil.validateToken(refreshHeader)).thenReturn(true);
        when(jwtUtil.extractTokenType(refreshHeader)).thenReturn("refresh");
        when(jwtUtil.extractUserId(refreshHeader)).thenReturn(3);
        when(jwtUtil.extractEmail(refreshHeader)).thenReturn("u@ex.com");
        when(tokenService.getRefreshToken(3)).thenReturn("raw");
        when(jwtUtil.extractRawToken(refreshHeader)).thenReturn("raw");

        User user = new User();
        user.setId(3);
        user.setEmail("u@ex.com");
        user.setName("User");
        user.setAge(30);

        when(userMapper.findById(3)).thenReturn(user);

        when(jwtUtil.createAccessToken("u@ex.com", 3)).thenReturn("newAccess");
        when(jwtUtil.createRefreshToken("u@ex.com", 3)).thenReturn("newRefresh");

        ResponseDto<JwtLoginResponseDto> res = authService.reissue(refreshHeader);

        assertEquals(200, res.getStatusCode());
        assertEquals("토큰 재발급 성공", res.getMessage());
        assertNotNull(res.getData());
        JwtLoginResponseDto data = res.getData();
        assertEquals("newAccess", data.getAccessToken());
        assertEquals("newRefresh", data.getRefreshToken());
        UserResponseDto userRes = data.getUser();
        assertEquals(3, userRes.getId());

        verify(tokenService).saveRefreshToken(3, "newRefresh");
    }

    @Test
    @DisplayName("logout: 유효한 refresh/access가 있으면 삭제 및 블랙리스트 등록")
    void logout_WithTokens() {
        String access = "Bearer access";
        String refresh = refreshHeader;

        when(jwtUtil.validateToken(refresh)).thenReturn(true);
        when(jwtUtil.extractTokenType(refresh)).thenReturn("refresh");
        when(jwtUtil.extractUserId(refresh)).thenReturn(5);

        when(jwtUtil.validateToken(access)).thenReturn(true);

        authService.logout(access, refresh);

        verify(tokenService).deleteRefreshToken(5);
        verify(tokenService).blacklistAccessToken(access);
    }

    @Test
    @DisplayName("logout: 토큰이 유효하지 않아도 200 반환, 토큰 서비스 호출 없음")
    void logout_InvalidTokens() {
        String access = "Bearer access";
        String refresh = "Bearer refresh";

        when(jwtUtil.validateToken(refresh)).thenReturn(false);
        when(jwtUtil.validateToken(access)).thenReturn(false);

        ResponseDto<?> res = authService.logout(access, refresh);

        assertEquals(200, res.getStatusCode());
        assertEquals("로그아웃 성공", res.getMessage());

        verify(tokenService, never()).deleteRefreshToken(any());
        verify(tokenService, never()).blacklistAccessToken(anyString());
    }
}
