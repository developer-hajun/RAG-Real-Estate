package ssafy.realty.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    private final String SECRET_KEY = "VlwEyVlxTm9YpZnLsKyRxPmnWqRzNvQmJkXgFbDcAHTuZfYmWqEsXrT";
    private final long EXPIRATION_TIME = 86400000;

    // 미리 정의된 SECRET_KEY를 java.security.Key 객체로 변환하여 반환하는 메서드
    private SecretKey getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //JWT를 생성
    public String generateToken(String email, int userId) {

        return Jwts.builder() // JWT 빌더 시작 Jwts 클래스는 JWT 관련 기능 제공, builder()는 JWT의 구성요소를 설정할 수 있는 '객체'를 반환함
                .setSubject(email) // JWT의 payload에 포함될 sub claim을 사용자의 이메일로 설정한다/ subject는 토큰이 누구/무엇에 대한 것인지 나타냄
                .claim("userId",userId) // JWT의 payload에 userId라는 이름의 커스텀 클레임을 추가하고, 인자로 받은 userId 값을 넣는다
                .setIssuedAt(new Date()) // JWT의 payload에 포함될 iat claim을 현재 시간의 설정한다. 이 토큰이 언제 발행되었는지를 나타냄
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // payload에 포함될 Expiration Time claim 설정, 현재 시간에 미리 정의된 EXPIRATION_TIME을 더하여 토큰의 만료 시점을 정함
                .signWith(getSigningKey(),SignatureAlgorithm.HS256) // 이 토큰을 HS256이라는 서명 알고리즘을 사용하여 서명한다, 서명키로는 미리 정의된 SECRET_KEY 사용
                .compact(); // 지금까지 빌더에 설정된 모든 정보를 바탕으로 JWT 문자열을 최종적으로 compact하고 이를 반환한다, 생성된 JWT는 Header.Payload.Signature 형식의 문자열 이다.
    }

    //토큰에서 정보 추출
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder() // JWT 문자열을 분석하기 위한 빌더 패턴 시작
                .setSigningKey(getSigningKey()) // 토큰이 변조되지 않았는지 검증하기 위해 사용할 서명 키를 설정 함
                .build() // 설정이 완료된 parserBuilder를 바탕으로 실제 파싱 작업을 수행할 JwtParser 객체를 생성
                .parseClaimsJws(token). // 인자로 받은 token을 파싱한다. 이때, 설정된 서명 키를 이용하여 토큰의 서명을 검증하고 만료시간(Expiration) 등 기본 유효성 검사도 함께 수행한다.
                getBody(); // 성공적으로 검증된 JWT 에서 payload 부분 , 즉 토큰에 담긴 실제 사용자 정보(claim)를 담고 있는 Claims 객체를 최종적으로 반환함
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            extractClaims(token); // 위에서 정의한 extractClaims 메서드를 호춯한다. 이 때 메서드를 호출하는 것 자체가 내부적으로 서명 검증과 만료 시간 검증을 수행하는 행위
            return true;
        } catch (JwtException | IllegalArgumentException e) { // JwtException은 서명 불일치, 토큰 만료등 JWT 관련 오류를 포괄하며, IllegalArgumentException은 토큰 문자열이 잘못된 형식일 경우 발생할 수 있음
            return false;
        }
    }


}
