package ssafy.realty.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ssafy.realty.Common.ResponseDto;
import ssafy.realty.DTO.Request.RealtySearchRequestDto;
import ssafy.realty.DTO.Request.ReviewCreateRequestDto;
import ssafy.realty.DTO.Response.RealtyListResponseDto;
import ssafy.realty.DTO.Response.RealtyResponseDto;
import ssafy.realty.DTO.Response.ReviewResponseDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/real-estate")
public class RealtyController {

    // 목록 조회
    @GetMapping
    public ResponseEntity<ResponseDto<?>> list(@ModelAttribute RealtySearchRequestDto request){
        List<RealtyListResponseDto> list = new ArrayList<>();

        // 더미 1: 전세 (month_price = 0)
        list.add(RealtyListResponseDto.builder()
                .id(1)
                .name("싸피 아파트 101동")
                .address("서울 강남구 역삼동")
                .e_price(500000000) // 전세 5억
                .month_price(0)     // 월세 0
                .priceInfo("전세 5억")
                .ratingAvg(4.5)
                .build());

        // 더미 2: 월세 (month_price > 0)
        list.add(RealtyListResponseDto.builder()
                .id(2)
                .name("멀티캠퍼스 오피스텔")
                .address("서울 강남구 테헤란로")
                .e_price(20000000)  // 보증금 2천
                .month_price(800000) // 월세 80

                .priceInfo("2000 / 80")
                .ratingAvg(3.8)
                .build());

        return ResponseEntity.ok(ResponseDto.create(200,"목록 조회 성공",list));
    }

    // 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<?>> detail(@PathVariable int id,
                                                 @RequestParam(required = false, defaultValue = "0") int user_id){
        // ID가 1이면 전세, 아니면 월세 데이터라고 가정
        boolean isJeonse = (id == 1);

        RealtyResponseDto detail = RealtyResponseDto.builder()
                .id(id)
                .name(isJeonse ? "싸피 아파트 101동" : "멀티캠퍼스 오피스텔")
                .address(isJeonse ? "서울 강남구 역삼동" : "서울 강남구 테헤란로")
                .e_price(isJeonse ? 500000000 : 20000000)
                .month_price(isJeonse ? 0 : 800000)
                // 좌표 데이터 (DB Schema, Entity 이름 그대로)
                .y_coordinate(37.5013f) // 위도
                .x_coordinate(127.039f) // 경도
                .ratingAvg(4.2)
                .reviewCount(10)
                .isFavorite(user_id == 1) // 1번 유저면 찜 되어있음 처리
                .reviewList(new ArrayList<>())
                .build();
        return ResponseEntity.ok(ResponseDto.create(200,"상세 조회 성공",detail));
    }

    // 리뷰 조회
    @GetMapping("/{id}/reviews")
    public ResponseEntity<ResponseDto<?>> reviews(@PathVariable Integer id){
        List<ReviewResponseDto> reviews = new ArrayList<>();
        reviews.add(ReviewResponseDto.builder()
                .id(1)
                .rating(5)
                .text("채광이 좋고 조용해요.")
                .updatedDate(LocalDateTime.now())
                .build());
        return ResponseEntity.ok(ResponseDto.create(200,"리뷰 조회 성공",reviews));
    }

    // 평가 등록
    @PostMapping("/{id}/rate")
    public ResponseEntity<ResponseDto<?>> rate(@PathVariable Integer id, @RequestBody ReviewCreateRequestDto request){
        return ResponseEntity.ok(ResponseDto.create(200,"평가 등록 성공"));
    }

    // 부동산 찜
    @PostMapping("/{id}/favorite")
    public ResponseEntity<ResponseDto<?>> toggleFavorite(@PathVariable int id, @RequestBody ReviewCreateRequestDto request) {
        // 찜 로직 수행
        return ResponseEntity.ok(ResponseDto.create(200,"찜 목록 변경 성공"));
    }

    // 비교 결과 조회
    @PostMapping("/compare")
    public ResponseEntity<ResponseDto<?>> compare(){
        List<RealtyResponseDto> compareList = new ArrayList<>();

        // 예시 데이터 채워넣기
        compareList.add(RealtyResponseDto.builder()
                .id(1)
                .name("비교 매물 1")
                .e_price(300000000)
                .month_price(0)
                .build());
        return ResponseEntity.ok(ResponseDto.create(200,"비교 결과 조회 성공",compareList));
    }




}
