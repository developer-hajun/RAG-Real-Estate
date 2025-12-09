package ssafy.realty.Common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ssafy.realty.util.JwtUtil;

import java.util.List;

/*
SecurityConfig, JwtAuthenticationFilter, JwtUtil 세 클래스가 유기적으로 작동하며
SecurityConfig의 규칙에 따라 JwtAuthenticationFilter가 JwtUtil의 도움을 받아 요청의 유효성을 검증하고 보호된 리소스에 대한 접근을 결정하게 됨

1 정책설계 : 애플리케이션의 보안 정책 및 환경을 정의함
2 필터연결 : 사용자 정의 필터인 JwtAuthenticationFilter를 스프링 보안 필터 체인에 등록하고 순서를 지정한다
3 구성요소제공 : 비밀번호 암호화를 위한 PasswordEncoder Bean 과 CORS 처리를 위한 CorsConfigurationSource Bean을 제공한다.
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    // 비밀번호 인코딩을 위한 빈을 정의하는 메서드임
    public PasswordEncoder passwordEncoder() {
        // BCrypt 알고리즘을 사용하여 비밀번호를 해시하는 PasswordEncoder 구현체를 반환
        return new BCryptPasswordEncoder();
    }

    // 보안 필터 체인을 구성하는 메서드이며 HttpSecurity를 주입 받는다 .
    @Bean
    public SecurityFilterChain SecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // CSRF(Cross-site Request Forgery) 보호 기능을 비활성화 한다(일반적으로 REST API에서 JWT를 사용할 떄).
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS(Cross-Origin Resource Sharing) 설정을 적용하고 corsConfigurationSource()메서드를 사용하도록 지정
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 관리를 설정하며 STATELESS(무상태)로 설정하여 세션을 사용하지 않음을 명시함
                .authorizeHttpRequests(auth -> auth // 요청에 대한 권한 확인 규칙을 정의하기 시작

                        /*.requestMatchers("api/users/login", "api/users/register").permitAll() // 여기서 명시한 경로에 대한 요청은 모두 허용함
                        .anyRequest().authenticated() // 위의 경로들을 제외한 모든 요청은 인증이 필요하도록 설정*/

                        .requestMatchers("/**").permitAll() // 지금은 테스트를 위해 모든 경로에 대해 허용하겠음
                )
                // 정의된 JwtAuthenticationFilter를 기본 UsernamePasswordAuthenticationFilter '이전에' 추가하여 JWT 인증을 먼저 수행하도록 한다.
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
        // 구성이 완료된 SecurityFilterChain 객체를 빌드하고 반환한다.
        return http.build();
    }

    // CORS 설정을 정의하는 CorsConfigurationSource 빈을 정의한다.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration(); // 새로운 CORS 설정 객체 생성

        configuration.setAllowedOrigins(List.of("http://localhost:8080","http://localhost:5173")); // 허용할 출처 목록 설정
        configuration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS")); // 허용할 HTTP 메서드 목록 설정
        configuration.setAllowCredentials(true); // 자격 증명(쿠키, 인증 헤더 등)을 요청에 포함할 수 있도록 허용함
        configuration.setAllowedHeaders(List.of("*")); // 모든 HTTP 헤더를 요청에 포함할 수 있도록 허용한다

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); // URL 경로를 기반으로 CORS 구성을 매핑할 소스 객체를 생성함
        source.registerCorsConfiguration("/**", configuration); // 모든 URL 경로에 대해 위에서 CORS 설정을 적용하도록 등록한다.
        return source; // 설정이 완료된 CorsConfigurationSource 객체를 반환 한다
    }


}
