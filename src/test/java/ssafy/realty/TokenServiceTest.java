package ssafy.realty;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import ssafy.realty.Service.TokenService;
import ssafy.realty.util.JwtUtil;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private TokenService tokenService;

    @BeforeEach
    void setup() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("saveRefreshToken: TTL(분)으로 값 저장")
    void saveRefreshToken_SetsValueWithTTL() {
        when(jwtUtil.getRemainRefreshExpMinutes()).thenReturn(30L);

        tokenService.saveRefreshToken(10, "refresh-token-val");

        verify(valueOperations).set(eq("RT:10"), eq("refresh-token-val"), eq(30L), eq(TimeUnit.MINUTES));
    }

    @Test
    @DisplayName("getRefreshToken: 저장된 값 반환")
    void getRefreshToken_ReturnsValue() {
        when(valueOperations.get("RT:11")).thenReturn("val11");

        String res = tokenService.getRefreshToken(11);

        assertEquals("val11", res);
    }

    @Test
    @DisplayName("deleteRefreshToken: 키 삭제 호출 확인")
    void deleteRefreshToken_DeletesKey() {
        tokenService.deleteRefreshToken(12);

        verify(redisTemplate).delete("RT:12");
    }

    @Test
    @DisplayName("blacklistAccessToken: TTL 양수일 때 블랙리스트 설정")
    void blacklistAccessToken_TTLPositive() {
        String header = "Bearer abc";
        when(jwtUtil.extractRawToken(header)).thenReturn("abc");
        when(jwtUtil.getRemainingExpMillis(header)).thenReturn(5000L);

        tokenService.blacklistAccessToken(header);

        verify(valueOperations).set(eq("BL:abc"), eq("logout"), eq(5L), eq(TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("blacklistAccessToken: TTL 0이면 설정하지 않음")
    void blacklistAccessToken_TTLZero() {
        String header = "Bearer abc";
        when(jwtUtil.extractRawToken(header)).thenReturn("abc");
        when(jwtUtil.getRemainingExpMillis(header)).thenReturn(0L);

        tokenService.blacklistAccessToken(header);

        verify(valueOperations, never()).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));
    }

    @Test
    @DisplayName("isAccessTokenBlacklisted: hasKey에 따라 true/false 반환")
    void isAccessTokenBlacklisted_Returns() {
        String header = "Bearer xyz";
        when(jwtUtil.extractRawToken(header)).thenReturn("xyz");
        when(redisTemplate.hasKey("BL:xyz")).thenReturn(true);

        assertTrue(tokenService.isAccessTokenBlacklisted(header));

        when(redisTemplate.hasKey("BL:xyz")).thenReturn(false);
        assertFalse(tokenService.isAccessTokenBlacklisted(header));
    }
}
