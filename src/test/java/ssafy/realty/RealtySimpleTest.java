package ssafy.realty;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ssafy.realty.Service.RealtyDocumentConverter;
import ssafy.realty.Service.RealtyRagService;

@SpringBootTest // 실제 스프링 컨테이너를 띄워서 모든 빈(Service, Repository 등)을 연결
class RealtySimpleTest {

    @Autowired
    private RealtyDocumentConverter documentConverter;

    @Autowired
    private RealtyRagService ragService;

    @Test
    @DisplayName("데이터 업로드 후 부산 오피스텔 추천 결과 확인")
    void testUploadAndChat() {
        // 1. DB에 있는 모든 데이터를 읽어서 VectorStore(벡터DB)에 업로드
        System.out.println(">>> [Step 1] 데이터 업로드 시작...");
        documentConverter.convertAndUploadAll();
        System.out.println(">>> [Step 1] 데이터 업로드 완료!");

        // 2. 검색할 질문 설정
        String query = "부산에 있는 오피스텔 추천해줘";
        System.out.println(">>> [Step 2] 질문 요청: " + query);

        // 4. 결과 콘솔 출력
        System.out.println("\n========================================");
        System.out.println("            [AI 추천 결과]            ");
        System.out.println("========================================");
        System.out.println(ragService.getRealtyRecommendation(query));
        System.out.println("========================================\n");
    }
}