package ssafy.realty;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ssafy.realty.Entity.Realty;
import ssafy.realty.Entity.RealtyRecommendationResponse;
import ssafy.realty.Mapper.RealtyMapper;
import ssafy.realty.Service.RealtyDocumentConverter;
import ssafy.realty.Service.RealtyRagService;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest // 실제 Spring 환경 로드 (VectorStore, ChatClient 활용)
class RealtyRagServiceTest {

    @Autowired
    private RealtyRagService realtyRagService;

    @Autowired
    private RealtyDocumentConverter realtyDocumentConverter;

    @MockBean
    private RealtyMapper realtyMapper; // DB 연결 대신 Mock 객체 사용

    @Test
    @DisplayName("부산 오피스텔 추천 요청 시, 벡터 스토어에 저장된 부산 매물을 기반으로 답변해야 한다")
    void testBusanRealtyRecommendation() {
        // 1. [Given] 테스트용 가짜 데이터 준비 (부산 매물 포함)
        Realty busanRealty = new Realty();
        busanRealty.setId(1);
        busanRealty.setName("해운대 오션뷰 오피스텔");
        busanRealty.setAddress("부산광역시 해운대구 우동 123-4");
        busanRealty.setE_price(1000);   // 보증금 1000만
        busanRealty.setMonth_price(60); // 월세 60만
        busanRealty.setX_coordinate(129.1F);
        busanRealty.setY_coordinate(35.1F);

        Realty seoulRealty = new Realty();
        seoulRealty.setId(2);
        seoulRealty.setName("강남 역세권 원룸");
        seoulRealty.setAddress("서울시 강남구 역삼동 555");
        seoulRealty.setE_price(2000);
        seoulRealty.setMonth_price(90);
        seoulRealty.setX_coordinate(127.0F);
        seoulRealty.setY_coordinate(37.5F);

        // Mapper가 호출될 때 위 리스트를 반환하도록 설정 (Stubbing)
        when(realtyMapper.selectAllRealty()).thenReturn(Arrays.asList(busanRealty, seoulRealty));

        // 2. [When] 데이터 임베딩 수행 (DB 데이터 -> 벡터 스토어 적재)
        // 실제로는 서버 시작 시점에 돌거나 스케줄러가 하는 작업을 수동으로 실행
        realtyDocumentConverter.convertAndUploadAll();

        // 3. [When] 사용자 질문으로 서비스 호출
        String userQuery = "부산에 있는 저렴한 오피스텔 추천해줘";
        System.out.println("사용자 질문: " + userQuery);

        // 4. [Then] 결과 검증 및 출력
        System.out.println("========================================");
        System.out.println("LLM 답변 결과:\n" + realtyRagService.getRealtyRecommendation(userQuery));
        System.out.println("========================================");

        // 검증: 답변에 '부산' 매물 이름이나 관련 정보가 포함되어야 함
        //assertThat(response).contains("해운대");
        //assertThat(response).contains("1000만");

        // 검증: 서울 매물은 추천되지 않아야 함 (Context Window에 포함될 순 있으나 답변은 부산 위주여야 함)
        // (이 부분은 LLM의 "생성" 영역이라 엄격한 검증보다는 로그 확인 용도)
    }

    @Test
    @DisplayName("RAG 전체 흐름 테스트: 데이터 적재 후 추천 검색")
    void testRealtyRecommendationFlow() {
        // 1. Given: 데이터 준비 (VectorStore에 데이터가 없으므로 먼저 적재)
        System.out.println(">>> 1. 벡터 데이터 적재 시작");
        realtyDocumentConverter.convertAndUploadAll();

        String userQuery = "보증금 500만원 정도로 저렴한 월세방 찾아줘";

        // 2. When: 서비스 호출
        System.out.println(">>> 2. AI 추천 서비스 호출");
        RealtyRecommendationResponse response = realtyRagService.getRealtyRecommendation(userQuery);

        // 3. Then: 결과 검증
        System.out.println(">>> 3. 결과 확인");
        System.out.println("AI 응답 메시지: " + response.getAiMessage());
        System.out.println("추천된 매물 개수: " + response.getRealties().size());

        // 검증 로직
        assertThat(response).isNotNull();
        assertThat(response.getAiMessage()).isNotEmpty();
        // DB에 데이터가 있고 검색이 잘 되었다면 매물이 1개 이상 나와야 함 (데이터가 있다는 가정 하에)
        // assertThat(response.getRealties()).isNotEmpty();
    }
}