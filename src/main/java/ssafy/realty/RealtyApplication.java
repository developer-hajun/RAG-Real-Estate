package ssafy.realty;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 특정 자동 설정 클래스를 제외(exclude) 시킵니다.
@SpringBootApplication
@MapperScan(basePackages = "ssafy.realty.Mapper")
public class RealtyApplication {

    public static void main(String[] args) {
        SpringApplication.run(RealtyApplication.class, args);
    }
}