package ssafy.realty.Common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import ssafy.realty.util.JwtUtil;

import java.io.IOException;
import java.util.ArrayList;

/*
 1. 토큰 추출 : 모든 HTTP 요청마다 실행되어 요청 헤더의 Authorization : Bearer ... 에서 JWT를 추출함
 2 유효성 검증 : 추출한 토큰의 유효성 검증을 JWT에 위임함
 3 인증상태 부여 : 토큰이 유효하면 해당 토큰 정보로 UsernamePasswordAuthenticationToken 객체를 생성하여
    SecurityContextHolder에 저장한다. -> 이 과정을 통해 인증된 사용자 자격이 부여됨
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // http 요청 헤더에서 Authorization 값을 가져온다.
        String authorizationHeader = request.getHeader("Authorization");

        //JWT 토큰 문자열을 저장할 변수를 초기화 함
        String token = null;

        //Authorization 헤더가 존재하고 Bearer로 시작하는지 확인한다.
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }

        // 토큰이 존재하고 jwtUtil 이용하여 토큰이 유효한지를 확인한다. -> 유효성 검증
        if (token != null && jwtUtil.validateToken(token)) { // 유효성 검증을 jwt에 위임함
            // 유효한 토큰으로 부터 인증객체 (Authentication)를 생성한다 ( 여기서는 토큰 자체를 주체로 사용, 권한은 빈 리스트로 생성)
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(token, null,new ArrayList<>());
            //인증 객체에 요청 상세정보를 설정함
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // 현재 Security Context에 생성된 인증 객체를 설정하여 이 요청이 인증된 상태임을 알림
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 다음 필터 또는 대상 서블릿/컨트롤러로 요청과 응답을 전달하여 필터 체인의 처리를 계속함
        filterChain.doFilter(request, response);
    }



}
