package ssafy.realty.Common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ssafy.realty.Common.ResponseDto;
import ssafy.realty.DTO.CustomUserDetails;
import ssafy.realty.DTO.Request.UserRequestDto;
import ssafy.realty.DTO.Response.JwtLoginResponseDto;
import ssafy.realty.DTO.Response.UserResponseDto;
import ssafy.realty.Entity.User;
import ssafy.realty.Service.TokenService;
import ssafy.realty.util.JwtUtil;

import java.io.IOException;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;
    private final ObjectMapper objectMapper ; // 스프링이 관리하는 ObjectMapper 사용


    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                   JwtUtil jwtUtil,
                                   TokenService tokenService,
                                   ObjectMapper objectMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.tokenService = tokenService;
        this.objectMapper = objectMapper;

        // 로그인 URL을 /api/auth/login 으로 변경
        setFilterProcessesUrl("/api/auth/login");
    }

    // 로그인 시도
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {

        try {
            UserRequestDto loginRequest =
                    objectMapper.readValue(request.getInputStream(), UserRequestDto.class);

            String email = loginRequest.getEmail();
            String password = loginRequest.getPassword();

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(email, password);

            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException("로그인 요청 파싱 실패", e);
        }
    }

    // 로그인 성공 시: JWT 발급 + Redis 저장 + ResponseDto<LoginResponseDto> 반환
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        CustomUserDetails principal = (CustomUserDetails) authResult.getPrincipal();
        User user = principal.getUser();

        // JwtUtil(email, id) 시그니처에 맞게 호출
        String accessToken = jwtUtil.createAccessToken(user.getEmail(), user.getId());
        String refreshToken = jwtUtil.createRefreshToken(user.getEmail(), user.getId());

        // Redis에 refresh 저장
        tokenService.saveRefreshToken(user.getId(), refreshToken);

        UserResponseDto userResponseDto = new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getAge(),
                user.getEmail(),
                user.getBirthDate()
        );

        JwtLoginResponseDto loginResponseDto = new JwtLoginResponseDto(
                accessToken,
                refreshToken,
                userResponseDto
        );

        ResponseDto<JwtLoginResponseDto> body =
                ResponseDto.create(200, "로그인 성공", loginResponseDto);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json; charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), body);
    }

    // 로그인 실패 시
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {

        ResponseDto<Object> body =
                ResponseDto.create(401, "이메일 또는 비밀번호가 올바르지 않습니다.", null);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), body);
    }
}
