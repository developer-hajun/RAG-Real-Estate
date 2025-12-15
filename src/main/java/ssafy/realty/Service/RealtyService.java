package ssafy.realty.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.realty.DTO.Request.*;
import ssafy.realty.DTO.Response.RealtyResponseDto;
import ssafy.realty.DTO.Response.ReviewResponseDto;
import ssafy.realty.Mapper.RealtyMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RealtyService {

    private final RealtyMapper realtyMapper;

    public List<RealtyResponseDto> search(RealtySearchRequestDto cond, Integer userId) {
        int size = (cond.getSize() <= 0) ? 20 : cond.getSize();
        int page = Math.max(cond.getPage(), 0);
        int offset = page * size;

        cond.setSize(size);
        cond.setPage(page);
        cond.setOffset(offset);
        cond.setUserId(userId);

        return realtyMapper.search(cond);
    }

    // 찜 여부를 알기 위해 userId가 필요함
    public RealtyResponseDto detail(int id, Integer userId) {
        RealtyDetailRequestDto req = new RealtyDetailRequestDto(id, userId);
        RealtyResponseDto dto = realtyMapper.findById(req);
        if (dto == null) return null;

        // 리뷰 붙이기
        List<ReviewResponseDto> reviews = realtyMapper.findReviewsByRealtyId(id);
        dto.setReviewList(reviews);
        return dto;
    }

    public List<ReviewResponseDto> reviews(int realtyId) {
        return realtyMapper.findReviewsByRealtyId(realtyId);
    }

    @Transactional
    public void createReview(int realtyId, int userId, int rating, String text) {
        ReviewInsertDto dto = new ReviewInsertDto(userId, realtyId, rating, text);
        realtyMapper.insertReview(dto);
    }

    @Transactional
    public boolean toggleFavorite(int realtyId, int userId) {
        RealtyFavoriteKeyDto key = new RealtyFavoriteKeyDto(userId, realtyId);
        int exists = realtyMapper.countFavorite(key);
        if (exists > 0) {
            realtyMapper.deleteFavorite(key);
            return false;
        } else {
            realtyMapper.insertFavorite(key);
            return true;
        }
    }

    public List<RealtyResponseDto> myFavorites(int userId) {
        return realtyMapper.findFavoritesByUserId(userId);
    }

    public List<RealtyResponseDto> compare(RealtyCompareRequestDto req) {
        return realtyMapper.findByIds(req);
    }
}
