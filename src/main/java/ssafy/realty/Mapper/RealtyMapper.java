package ssafy.realty.Mapper;

import org.apache.ibatis.annotations.Mapper;
import ssafy.realty.DTO.Request.*;
import ssafy.realty.DTO.Response.RealtyResponseDto;
import ssafy.realty.DTO.Response.ReviewResponseDto;
import ssafy.realty.Entity.Realty;

import java.util.List;

@Mapper
public interface RealtyMapper {

    List<Realty> selectAllRealty();

    List<Realty> selectRealtyListByIds(List<Integer> ids);


    // --- 건빈 ---

    // 목록 검색
    List<RealtyResponseDto> search(RealtySearchRequestDto cond);

    // 상세
    RealtyResponseDto findById(RealtyDetailRequestDto req);

    // 리뷰 목록
    List<ReviewResponseDto> findReviewsByRealtyId(int realtyId);

    // 리뷰 등록 (userId는 JWT로)
    int insertReview(ReviewInsertDto dto);

    // 찜 여부 확인/추가/삭제
    int countFavorite(RealtyFavoriteKeyDto key);
    int insertFavorite(RealtyFavoriteKeyDto key);
    int deleteFavorite(RealtyFavoriteKeyDto key);


    // 내 찜 목록
    List<RealtyResponseDto> findFavoritesByUserId(int userId);

    // 비교
    List<RealtyResponseDto> findByIds(RealtyCompareRequestDto req);

}
