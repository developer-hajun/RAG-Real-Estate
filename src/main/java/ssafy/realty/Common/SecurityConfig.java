package ssafy.realty.Common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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
import ssafy.realty.Service.CustomUserDetailsService;
import ssafy.realty.Service.TokenService;
import ssafy.realty.util.JwtUtil;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final TokenService tokenService;
    private final CustomUserDetailsService customUserDetailsService;
    private final ObjectMapper objectMapper;

    public SecurityConfig(JwtUtil jwtUtil,
                          TokenService tokenService,
                          CustomUserDetailsService customUserDetailsService,
                          ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.tokenService = tokenService;
        this.customUserDetailsService = customUserDetailsService;
        this.objectMapper = objectMapper;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager (UserDetailsService + PasswordEncoder)
    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationManager authenticationManager) throws Exception {

        JwtAuthenticationFilter jwtAuthenticationFilter =
                new JwtAuthenticationFilter(authenticationManager, jwtUtil, tokenService,objectMapper);

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(
//                                "/api/auth/login",     // 로그인 (필터가 처리)
//                                "/api/auth/register",  // 회원가입
//                                "/api/auth/reissue",   // 재발급
//                                "/api/auth/logout"     // 로그아웃
//                        ).permitAll()
//                        .anyRequest().authenticated()
                        .anyRequest().permitAll() // 테스트를 위해 잠시 전체 허용
                )
                // JWT 검증 + 블랙리스트 + SecurityContext 세팅 필터
                .addFilterBefore(new JwtVerificationFilter(jwtUtil, tokenService),
                        UsernamePasswordAuthenticationFilter.class)
                // 로그인 처리 필터
                .addFilterAt(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:8080", "http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS","PATCH"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
