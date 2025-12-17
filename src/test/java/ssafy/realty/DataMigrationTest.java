package ssafy.realty;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import ssafy.realty.Service.RealtyDocumentConverter;

@SpringBootTest
class DataMigrationTest {

    @Autowired
    private RealtyDocumentConverter converter;

    @Test
    @Rollback(false)
    void runMigrationOnce() {
        System.out.println("====== 데이터 업로드 시작 ======");
        converter.convertAndUploadAll();
        System.out.println("====== 데이터 업로드 종료 ======");
    }
}