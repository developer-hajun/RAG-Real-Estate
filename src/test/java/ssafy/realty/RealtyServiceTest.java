package ssafy.realty;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mockito;

import ssafy.realty.DTO.Request.RealtyCompareRequestDto;
import ssafy.realty.DTO.Request.RealtySearchRequestDto;
import ssafy.realty.DTO.Response.RealtyResponseDto;
import ssafy.realty.DTO.Response.ReviewResponseDto;
import ssafy.realty.Mapper.RealtyMapper;
import ssafy.realty.Service.RealtyService;
import ssafy.realty.DTO.Request.ReviewInsertDto;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RealtyServiceTest {

    @Mock
    private RealtyMapper realtyMapper;

    @InjectMocks
    private RealtyService realtyService;

    private final int TEST_USER_ID = 9;

    private RealtyResponseDto r1;
    private RealtyResponseDto r2;

    @BeforeEach
    void setUp() {
        // ...테스트용 DTO 초기화 (DB 대신 Mock으로 반환하도록 설정)
        r1 = new RealtyResponseDto();
        r1.setId(1);
        r1.setName("강남역 센트럴");
        r1.setAddress("서울 강남구 역삼동");
        r1.setReviewList(Collections.emptyList());

        r2 = new RealtyResponseDto();
        r2.setId(2);
        r2.setName("홍대 스위트");
        r2.setAddress("서울 마포구 동교동");
        r2.setReviewList(Collections.emptyList());

        // lenient stub: 일부 테스트에서 사용되지 않을 수 있는 스텁들을 lenient로 등록
        Mockito.lenient().when(realtyMapper.search(any(RealtySearchRequestDto.class))).thenReturn(Arrays.asList(r1, r2));

        // 상세 조회
        Mockito.lenient().when(realtyMapper.findById(any())).thenReturn(r1);

        // 리뷰 목록 (기본 빈 리스트)
        Mockito.lenient().when(realtyMapper.findReviewsByRealtyId(1)).thenReturn(Collections.emptyList());

        // 찜 관련: 첫 호출은 없음(0) -> 두번째 호출은 있음(1)
        Mockito.lenient().when(realtyMapper.countFavorite(any())).thenReturn(0, 1);

        // 내 찜 목록: 첫 호출에서는 r2 포함, 두번째 호출에는 빈 리스트로 변경해서 시퀀스 테스트 가능
        Mockito.lenient().when(realtyMapper.findFavoritesByUserId(anyInt())).thenReturn(Arrays.asList(r2), Collections.emptyList());

        // 비교 조회
        Mockito.lenient().when(realtyMapper.findByIds(any())).thenReturn(Arrays.asList(r1, r2));
    }

    @Test
    @DisplayName("1. 매물 검색(search) - 주소로 검색 시 결과 반환 확인 (Mock)")
    void searchTest() {
        RealtySearchRequestDto cond = new RealtySearchRequestDto();
        cond.setAddress("강남");
        cond.setPage(0);
        cond.setSize(10);

        List<RealtyResponseDto> results = realtyService.search(cond, TEST_USER_ID);

        assertThat(results).isNotNull();
        assertThat(results.size()).isGreaterThanOrEqualTo(2);
        assertThat(results).extracting("name").contains("강남역 센트럴");
    }

    @Test
    @DisplayName("2. 매물 상세(detail) - 필드와 리뷰 리스트 확인 (Mock)")
    void detailTest() {
        RealtyResponseDto dto = realtyService.detail(1, TEST_USER_ID);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("강남역 센트럴");
        assertThat(dto.getReviewList()).isNotNull();
        assertThat(dto.getReviewList()).isEmpty();

        // mapper의 findById, findReviewsByRealtyId가 호출되었는지 확인
        verify(realtyMapper).findById(any());
        verify(realtyMapper).findReviewsByRealtyId(1);
    }

    @Test
    @DisplayName("3. 찜 토글(toggleFavorite) 및 내 찜 목록(myFavorites) 확인 (Mock)")
    void toggleFavoriteAndMyFavoritesTest() {
        // 첫 호출: 없음 -> 추가되어 true 반환
        boolean added = realtyService.toggleFavorite(2, TEST_USER_ID);
        assertThat(added).isTrue();
        verify(realtyMapper).insertFavorite(any());

        // 내 찜 목록에 포함되어야 함 (첫 시나리오)
        List<RealtyResponseDto> favs = realtyService.myFavorites(TEST_USER_ID);
        assertThat(favs).isNotEmpty();
        assertThat(favs).extracting("id").contains(2);

        // 두번째 toggle: 존재하므로 삭제되어 false 반환
        boolean removed = realtyService.toggleFavorite(2, TEST_USER_ID);
        assertThat(removed).isFalse();
        verify(realtyMapper).deleteFavorite(any());

        // 내 찜 리스트는 두번째 호출에서는 빈 리스트로 설정됨
        List<RealtyResponseDto> favsAfter = realtyService.myFavorites(TEST_USER_ID);
        assertThat(favsAfter).isEmpty();
    }

    @Test
    @DisplayName("4. 리뷰 작성(createReview) 및 리뷰 목록(reviews) 확인 (Mock)")
    void createReviewAndListTest() {
        // createReview는 Mapper.insertReview 호출을 수행
        realtyService.createReview(1, TEST_USER_ID, 5, "좋은 매물입니다");
        verify(realtyMapper).insertReview(any(ReviewInsertDto.class));

        // 이후 리뷰 목록을 반환하도록 Mock 설정
        ReviewResponseDto r = ReviewResponseDto.builder().id(1).rating(5).text("좋은 매물입니다").build();
        given(realtyMapper.findReviewsByRealtyId(1)).willReturn(Arrays.asList(r));

        List<ReviewResponseDto> reviews = realtyService.reviews(1);
        assertThat(reviews).isNotNull();
        assertThat(reviews.size()).isGreaterThanOrEqualTo(1);
        assertThat(reviews.get(0).getRating()).isEqualTo(5);
        assertThat(reviews.get(0).getText()).isEqualTo("좋은 매물입니다");
    }

    @Test
    @DisplayName("5. 비교(compare) - 여러 ID로 조회 (Mock)")
    void compareTest() {
        RealtyCompareRequestDto req = new RealtyCompareRequestDto(Arrays.asList(1, 2), TEST_USER_ID);

        List<RealtyResponseDto> list = realtyService.compare(req);

        assertThat(list).isNotNull();
        assertThat(list.size()).isGreaterThanOrEqualTo(2);
    }
}
