package ssafy.realty.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssafy.realty.Common.ResponseDto;
import ssafy.realty.DTO.Request.RealtyCompareRequestDto;
import ssafy.realty.DTO.Request.RealtySearchRequestDto;
import ssafy.realty.DTO.Request.ReviewCreateRequestDto;
import ssafy.realty.DTO.Response.RealtyRecommendationResponse;
import ssafy.realty.DTO.Response.RealtyResponseDto;
import ssafy.realty.DTO.Response.ReviewResponseDto;
import ssafy.realty.Entity.Realty;
import ssafy.realty.Service.RealtyRagService;
import ssafy.realty.Service.RealtyService;
import ssafy.realty.Service.UserService;
import ssafy.realty.util.JwtUtil;

import java.util.List;

@RestController
@RequestMapping("/api/real-estate")
@RequiredArgsConstructor
public class RealtyController {

    private final RealtyService realtyService;
    private final RealtyRagService realtyRagService; // ✅ 그대로 주입해서 사용
    private final UserService userService;
    private final JwtUtil jwtUtil;

    private Integer userIdOrNull(String authorization) {
        if (authorization == null || authorization.isBlank()) return null;
        return jwtUtil.extractUserId(authorization);
    }

    // 목록 조회 (토큰 있으면 isFavorite 채워줌)
    @GetMapping
    public ResponseEntity<ResponseDto<?>> list(@ModelAttribute RealtySearchRequestDto request,
                                               @RequestHeader(value = "Authorization", required = false) String authorization) {
        List<RealtyResponseDto> list = realtyService.search(request, userIdOrNull(authorization));
        return ResponseEntity.ok(ResponseDto.create(200, "목록 조회 성공", list));
    }

    // 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<?>> detail(@PathVariable int id,
                                                 @RequestHeader(value = "Authorization", required = false) String authorization) {
        RealtyResponseDto detail = realtyService.detail(id, userIdOrNull(authorization));
        if (detail == null) return ResponseEntity.ok(ResponseDto.create(404, "매물을 찾을 수 없습니다."));
        return ResponseEntity.ok(ResponseDto.create(200, "상세 조회 성공", detail));
    }

    // 리뷰 조회
    @GetMapping("/{id}/reviews")
    public ResponseEntity<ResponseDto<?>> reviews(@PathVariable int id) {
        List<ReviewResponseDto> reviews = realtyService.reviews(id);
        return ResponseEntity.ok(ResponseDto.create(200, "리뷰 조회 성공", reviews));
    }

    // 평가 등록 (JWT에서 userId 꺼내고, body.userId는 무시) // 여기 좀 이상한듯 리팩토링 필요 Dto를 고치거나 해야 될듯
    @PostMapping("/{id}/rate")
    public ResponseEntity<ResponseDto<?>> rate(@PathVariable int id,
                                               @RequestHeader("Authorization") String authorization,
                                               @RequestBody ReviewCreateRequestDto request) {
        Integer userId = jwtUtil.extractUserId(authorization);
        if (userId == null) return ResponseEntity.ok(ResponseDto.create(401, "유효하지 않은 토큰입니다."));
        realtyService.createReview(id, userId, request.getRating(),request.getText());
        return ResponseEntity.ok(ResponseDto.create(200, "평가 등록 성공"));
    }

    // 찜 토글 (body 필요 없음)
    @PostMapping("/{id}/favorite")
    public ResponseEntity<ResponseDto<?>> toggleFavorite(@PathVariable int id,
                                                         @RequestHeader("Authorization") String authorization) {
        Integer userId = jwtUtil.extractUserId(authorization);
        if (userId == null) return ResponseEntity.ok(ResponseDto.create(401, "유효하지 않은 토큰입니다."));
        boolean isFavorite = realtyService.toggleFavorite(id, userId);
        return ResponseEntity.ok(ResponseDto.create(200, "찜 목록 변경 성공", isFavorite));
    }

    // 비교
    @PostMapping("/compare")
    public ResponseEntity<ResponseDto<?>> compare(@RequestBody RealtyCompareRequestDto request,
                                                  @RequestHeader(value = "Authorization", required = false) String authorization) {
        if(userIdOrNull(authorization) == null){
            return ResponseEntity.ok(ResponseDto.create(401, "유효하지 않은 토큰입니다."));
        }
        List<RealtyResponseDto> compareList = realtyService.compare(request);
        return ResponseEntity.ok(ResponseDto.create(200, "비교 결과 조회 성공", compareList));
    }

    // ✅ RAG 질문(페이지용) - RealtyRagService 그대로 호출 // Dto를 쓸지 String을 쓸지 고민 일단 주석 처리
    @PostMapping("/rag")
    public ResponseEntity<ResponseDto<?>> rag(@RequestHeader(value = "Authorization", required = false) String authorization, @RequestBody String query) {
        // 1. 유저 ID 추출 (로그인 한 경우에만 기록 저장)
        Integer userId = userIdOrNull(authorization);

        // 2. 검색 기록 저장 (비로그인 유저도 검색은 되지만 기록은 안 남김) <- 나중에 수정 지금은 다 열어놔서 상관없지만
        if (userId != null) {
            userService.saveSearchHistory(userId, query);
        }

        // 3. RAG 서비스 호출 및 결과 반환
        RealtyRecommendationResponse res = realtyRagService.getRealtyRecommendation(query);
        return ResponseEntity.ok(ResponseDto.create(200, "RAG 추천 성공", res));
    }
}
