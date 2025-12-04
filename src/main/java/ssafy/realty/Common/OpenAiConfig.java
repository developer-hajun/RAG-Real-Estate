package ssafy.realty.Common;

import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;

@Configuration
public class OpenAiConfig {

    @Bean
    public RestClientCustomizer restClientCustomizer() {
        return restClientBuilder -> {
            restClientBuilder.requestInterceptor((request, body, execution) -> {
                // 1. Cloudflare를 통과하기 위해 브라우저인 척 User-Agent 변경
                request.getHeaders().add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
                
                // 2. 명시적으로 Content-Type 지정
                request.getHeaders().add("Content-Type", "application/json");
                
                return execution.execute(request, body);
            });
        };
    }
}