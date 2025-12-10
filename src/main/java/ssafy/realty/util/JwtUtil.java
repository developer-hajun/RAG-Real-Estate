package ssafy.realty.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ssafy.realty.DTO.Request.UserRequestDto;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
public class JwtUtil {
    private final String SECRET_KEY = "VlwEyVlxTm9YpZnLsKyRxPmnWqRzNvQmJkXgFbDcAHTuZfYmWqEsXrT";
    //private final long EXPIRATION_TIME = 86400000;

    private final long accessExpMinutes = 30; // 30분
    private final long refreshExpMinutes = 60L*24*7;

    private final SecretKey key;

    public JwtUtil() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        key = Keys.hmacShaKeyFor(keyBytes);
        log.debug("Key created: {}", Arrays.toString(key.getEncoded()));
    }

    private String createToken(String subject, long expireMin, Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expireMin * 1000L * 60L);

        String token = Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
        log.debug("Token created: {}", token);
        return token;
    }

    public String createAccessToken(String email, int userId) {
        return createToken("accessToken",accessExpMinutes,
                Map.of(
                        "userId", userId,
                        "email", email,
                        "tokenType","access"
                )); // 이 자리에 UserRequestDto 받아서 map 형식으로 넣어야 함
    }

    public String createRefreshToken(String email, int userId) {
        return createToken("refreshToken",refreshExpMinutes,
                Map.of(
                        "userId", userId,
                        "email", email,
                        "tokenType","refresh"
                )); // 이 자리에 UserRequestDto 받아서 map 형식으로 넣어야 함
    }

    private String resolveToken(String token) { // 받은 Bearer 토큰에서 7자리 제외 Bearer 포함 공백 포함
        if(token == null) {
            return null;
        }
        if(token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }

    public Claims extractClaims(String token){
        String realToken = resolveToken(token);
        if(realToken == null||realToken.isBlank()) {
            throw new IllegalArgumentException("Invalid token");
        }
        JwtParser parser = Jwts.parser()
                .verifyWith(key)
                .build();

        Jws<Claims> jws = parser.parseSignedClaims(realToken);
        log.debug("Claims received: {}", jws);
        return jws.getPayload();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token){
        try{
            extractClaims(token); // 제대로 실행 됐으면 밑에 return 실행 가능
            return true;
        }catch(JwtException | IllegalArgumentException e){
            log.warn("잘못된 JWT 토큰입니다. token={}, message={}", token, e.getMessage());
            return false;
        }
    }

    public Integer extractUserId(String token){
        try{
            Claims claims = extractClaims(token);
            return claims.get("userId",Integer.class);
        }catch(JwtException | IllegalArgumentException e){
            log.warn("userId 추출 실패 : {}", e.getMessage());
            return null;
        }
    }

    public String extractEmail(String token){
        try{
            Claims claims = extractClaims(token);
            return claims.get("email",String.class);
        }catch(JwtException | IllegalArgumentException e){
            log.warn("email 추출 실패 : {}",e.getMessage());
            return null;
        }
    }

    public String extractTokenType(String token){
        try{
            Claims claims = extractClaims(token);
            return claims.get("tokenType",String.class);
        }catch(JwtException | IllegalArgumentException e){
            log.warn("tokenType 추출 실패 : {}",e.getMessage());
            return null;
        }
    }

    // == Redis 연동용 헬퍼들 ==

    // access/refresh 만료시간 (분)
    public long getRemainAccessExpMinutes() {
        return accessExpMinutes;
    }

    // refresh 만료시간(분)
    public long getRemainRefreshExpMinutes() {
        return refreshExpMinutes;
    }

    // 토큰 남은 만료 시간(ms) - 블랙리스트 TTL에 사용
    public long getRemainingExpMillis(String token) {
        try{
            Claims claims = extractClaims(token);
            Date exp = claims.getExpiration();
            long remain = exp.getTime() - System.currentTimeMillis();
            return Math.max(remain,0L);
        }catch(JwtException | IllegalArgumentException e){
            return 0L;
        }
    }

    // Redis key 등에 쓸 때 "순수 토큰 문자열" 만 필요할 때
    public String extractRawToken(String token) {
        return resolveToken(token);
    }


    // 미리 정의된 SECRET_KEY를 java.security.Key 객체로 변환하여 반환하는 메서드
//    private SecretKey getSigningKey() {
//        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }

    //JWT를 생성
    //우린 userId가 PK인데 setSubject를 email로 하는게 옳은가 ?
//    public String generateToken(String email, int userId) {
//
//        return Jwts.builder() // JWT 빌더 시작 Jwts 클래스는 JWT 관련 기능 제공, builder()는 JWT의 구성요소를 설정할 수 있는 '객체'를 반환함
//                .setSubject(email) // JWT의 payload에 포함될 sub claim을 사용자의 이메일로 설정한다/ subject는 토큰이 누구/무엇에 대한 것인지 나타냄
//                .claim("userId",userId) // JWT의 payload에 userId라는 이름의 커스텀 클레임을 추가하고, 인자로 받은 userId 값을 넣는다
//                .setIssuedAt(new Date()) // JWT의 payload에 포함될 iat claim을 현재 시간의 설정한다. 이 토큰이 언제 발행되었는지를 나타냄
//                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // payload에 포함될 Expiration Time claim 설정, 현재 시간에 미리 정의된 EXPIRATION_TIME을 더하여 토큰의 만료 시점을 정함
//                .signWith(getSigningKey(),SignatureAlgorithm.HS256) // 이 토큰을 HS256이라는 서명 알고리즘을 사용하여 서명한다, 서명키로는 미리 정의된 SECRET_KEY 사용
//                .compact(); // 지금까지 빌더에 설정된 모든 정보를 바탕으로 JWT 문자열을 최종적으로 compact(직렬화)하고 이를 반환한다, 생성된 JWT는 Header.Payload.Signature 형식의 문자열 이다.
//    }

//    //토큰에서 정보 추출
//    public Claims extractClaims(String token) {
//        token = token.substring(7);
//        return Jwts.parserBuilder() // JWT 문자열을 분석하기 위한 빌더 패턴 시작
//                .setSigningKey(getSigningKey()) // 토큰이 변조되지 않았는지 검증하기 위해 사용할 서명 키를 설정 함
//                .build() // 설정이 완료된 parserBuilder를 바탕으로 실제 파싱 작업을 수행할 JwtParser 객체를 생성
//                .parseClaimsJws(token). // 인자로 받은 token을 파싱한다. 이때, 설정된 서명 키를 이용하여 토큰의 서명을 검증하고 만료시간(Expiration) 등 기본 유효성 검사도 함께 수행한다.
//                getBody(); // 성공적으로 검증된 JWT 에서 payload 부분 , 즉 토큰에 담긴 실제 사용자 정보(claim)를 담고 있는 Claims 객체를 최종적으로 반환함
//    }

//    // 토큰 유효성 검사
//    public boolean validateToken(String token) {
//        try {
//            extractClaims(token); // 위에서 정의한 extractClaims 메서드를 호춯한다. 이 때 메서드를 호출하는 것 자체가 내부적으로 서명 검증과 만료 시간 검증을 수행하는 행위
//            return true;
//        } catch (JwtException | IllegalArgumentException e) { // JwtException은 서명 불일치, 토큰 만료등 JWT 관련 오류를 포괄하며, IllegalArgumentException은 토큰 문자열이 잘못된 형식일 경우 발생할 수 있음
//            return false;
//        }
//    }

//    public Integer extractUserId(String token) {
//        try {
//            Claims claims = extractClaims(token);
//            return claims.get("userId", Integer.class);
//
//        } catch (JwtException | IllegalArgumentException e) {
//            return null;
//        }
//    }


}
