package ssafy.realty;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ssafy.realty.Entity.Realty;
import ssafy.realty.Entity.SearchHistory;
import ssafy.realty.Mapper.RealtyMapper;
import ssafy.realty.Mapper.SearchHistoryMapper;
import ssafy.realty.Service.RealtyDocumentConverter;
import ssafy.realty.Service.RealtyRagService;

import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class RealtyMockTest {

    @Autowired
    private RealtyDocumentConverter documentConverter;

    @Autowired
    private RealtyRagService ragService;

    // 실제 DB 매퍼 대신 가짜 역할을 할 Mock 객체 정의
    @MockBean
    private RealtyMapper realtyMapper;

    @MockBean
    private SearchHistoryMapper searchHistoryMapper;

    @Test
    @DisplayName("임의의 데이터로 업로드부터 질문 답변까지 전체 테스트")
    void testFullFlowWithDummyData() {
        // ====================================================
        // 1단계: 임의의 매물 데이터 생성 (가짜 DB 데이터)
        // ====================================================
        List<Realty> dummyRealties = createDummyRealties();

        // "DB에서 조회하면 이 가짜 리스트를 줘라"라고 설정
        given(realtyMapper.selectAllRealty()).willReturn(dummyRealties);

        System.out.println(">>> 1. 가짜 데이터 생성 및 벡터 업로드 시작");
        documentConverter.convertAndUploadAll(); // 내부적으로 realtyMapper.selectAllRealty()가 호출됨
        System.out.println(">>> ✅ 데이터 업로드 완료 (실제 DB 아님)");


        // ====================================================
        // 2단계: 임의의 검색 기록 데이터 생성
        // ====================================================
        int userId = 1;
        List<SearchHistory> dummyHistory = new ArrayList<>();
        dummyHistory.add(new SearchHistory(1, "서울 원룸",now(),now()));
        
        // "검색 기록을 조회하면 이 가짜 기록을 줘라"라고 설정
        given(searchHistoryMapper.searchHistoryTop3(userId)).willReturn(dummyHistory);


        // ====================================================
        // 3단계: 질문 던지기 (RAG 실행)
        // ====================================================
        String userQuery = "강남역 근처에 저렴한 월세 있어?";
        System.out.println(">>> 2. 질문 시작: " + userQuery);



        // ====================================================
        // 4단계: 결과 확인
        // ====================================================
        System.out.println("\n===========================================");
        System.out.println(" [🤖 AI 답변 결과] ");
        System.out.println("===========================================");
        System.out.println(ragService.getRealtyRecommendation(userQuery));
        System.out.println("===========================================\n");
    }

    // 테스트용 임의 매물 데이터 만드는 헬퍼 메소드
    private List<Realty> createDummyRealties() {
        List<Realty> list = new ArrayList<>();

        // 매물 1: 강남역 비싼 오피스텔
        Realty r1 = new Realty();
        r1.setId(100);
        r1.setName("강남역 센트럴 푸르지오");
        r1.setAddress("서울시 강남구 역삼동 825-1");
        r1.setE_price(1000); // 보증금
        r1.setMonth_price(150); // 월세
        // r1.setX_coordinate(...); 좌표 필요시 추가
        list.add(r1);

        // 매물 2: 강남역 저렴한 빌라 (정답 후보)
        Realty r2 = new Realty();
        r2.setId(101);
        r2.setName("역삼동 해피하우스");
        r2.setAddress("서울시 강남구 역삼동 123-45");
        r2.setE_price(500);
        r2.setMonth_price(45);
        list.add(r2);

        // 매물 3: 엉뚱한 지역 (홍대)
        Realty r3 = new Realty();
        r3.setId(102);
        r3.setName("홍대입구 원룸");
        r3.setAddress("서울시 마포구 동교동 111");
        r3.setE_price(1000);
        r3.setMonth_price(60);
        list.add(r3);

        return list;
    }
}