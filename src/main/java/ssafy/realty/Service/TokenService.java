package ssafy.realty.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import ssafy.realty.util.JwtUtil;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final StringRedisTemplate redisTemplate;
    private final JwtUtil jwtUtil;

    //refresh token
    private String refreshKey(Integer userId){
        return "RT:" + userId;
    }

    // BlackList
    private String blacklistKey(String rawAccessToken){
        return "BL:" + rawAccessToken;
    }

    //==refresh token 관리==

    public void saveRefreshToken(Integer userId, String refreshToken){
        String key = refreshKey(userId);
        long ttl = jwtUtil.getRemainRefreshExpMinutes();

        redisTemplate.opsForValue().set(key,refreshToken,ttl, TimeUnit.MINUTES);
    }

    public String getRefreshToken(Integer userId){
        return redisTemplate.opsForValue().get(refreshKey(userId));
    }

    public void deleteRefreshToken(Integer userId){
        redisTemplate.delete(refreshKey(userId));
    }

    //==Access token 블랙리스트 관리==
    public void blacklistAccessToken(String accessTokenHeader){
        String rawToken = jwtUtil.extractRawToken(accessTokenHeader);

        long remainMilliSeconds = jwtUtil.getRemainingExpMillis(accessTokenHeader);
        long ttlSeconds = Math.max(remainMilliSeconds / 1000L,0L);

        if(ttlSeconds > 0){
            redisTemplate.opsForValue().set(
                    blacklistKey(rawToken),
                    "logout",
                    ttlSeconds,
                    TimeUnit.SECONDS
            );
        }
    }

    public boolean isAccessTokenBlacklisted(String accessTokenHeader){
        String rawToken = jwtUtil.extractRawToken(accessTokenHeader);
        return Boolean.TRUE.equals(redisTemplate.hasKey(blacklistKey(rawToken)));
    }

}
