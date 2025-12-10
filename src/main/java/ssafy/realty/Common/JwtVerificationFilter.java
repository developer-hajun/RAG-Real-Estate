package ssafy.realty.Common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import ssafy.realty.Service.TokenService;
import ssafy.realty.util.JwtUtil;

import java.io.IOException;
import java.util.Collections;

public class JwtVerificationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    public JwtVerificationFilter(JwtUtil jwtUtil, TokenService tokenService) {
        this.jwtUtil = jwtUtil;
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        // 헤더에 토큰이 없다면 그냥 통과
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 블랙리스트 체크 (로그아웃 된 토큰인지)
        if (tokenService.isAccessTokenBlacklisted(authorizationHeader)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json; charset=UTF-8");
            String body = """
                    {"statusCode":401,"message":"로그아웃된 토큰입니다.","data":null}
                    """;
            response.getWriter().write(body);
            return;
        }

        // 기본 유효성 검증
        if (!jwtUtil.validateToken(authorizationHeader)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json; charset=UTF-8");
            String body = """
                    {"statusCode":401,"message":"유효하지 않거나 만료된 토큰입니다.","data":null}
                    """;
            response.getWriter().write(body);
            return;
        }

        // 토큰에서 정보 꺼내서 SecurityContext 에 인증 정보 세팅
        String email = jwtUtil.extractEmail(authorizationHeader);
        Integer userId = jwtUtil.extractUserId(authorizationHeader);

        if (email != null && userId != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            Collections.emptyList()
                    );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
