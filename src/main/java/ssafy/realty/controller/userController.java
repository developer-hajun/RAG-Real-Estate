package ssafy.realty.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssafy.realty.Common.ResponseDto;
import ssafy.realty.DTO.Response.SearchHistoryResponseDto;
import ssafy.realty.DTO.Request.UserRequestDto;
import ssafy.realty.DTO.Response.UserResponseDto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/*
 ** 지금 작성하는건 임시로 만든 것 프론트 구현하기 위해 나중에 실제로 바꿀 때 이 주석도 함께 삭제
 */
@RestController
@RequestMapping("/api/users")
public class userController {

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ResponseDto<?>> login(@RequestBody UserRequestDto request) {
        if ("test@example.com".equals(request.getEmail())
                && "password123".equals(request.getPassword())) {
            return ResponseEntity.ok(ResponseDto.create(200, "로그인 성공"));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseDto.create(401, "인증 실패"));
    }

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<ResponseDto<?>> register(@RequestBody UserRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.create(201, "회원가입 성공"));
    }

    //프로필 조회
    @GetMapping("/profile")
    public ResponseEntity<ResponseDto<?>> getProfile() {
        UserResponseDto profile = new UserResponseDto(
                1,
                "홍길동",
                29,
                "test@example.com",
                LocalDateTime.of(1996, 1, 1, 0, 0));
        return ResponseEntity.ok(ResponseDto.create(200, "프로필 조회 성공", profile));
    }

    // 프로필 수정
    @PutMapping("/profile")
    public ResponseEntity<ResponseDto<?>> updateProfile(@RequestBody UserRequestDto request) {
        UserResponseDto updated = new UserResponseDto(
                1,
                request.getName(),
                request.getAge(),
                request.getEmail(),
                request.getBirthDate()
        );

        return ResponseEntity.ok(ResponseDto.create(200, "프로필 수정 성공", updated));
    }

    // 검색 기록 조회니깐 리스트로 한 사람이 여러 검색 기록 가질 수 있음
    @GetMapping("/searchHistory")
    public ResponseEntity<ResponseDto<?>> getSearchHistory() {
        List<SearchHistoryResponseDto> searchHistories = Arrays.asList(
                new SearchHistoryResponseDto(1, "강남 전세", LocalDateTime.now().minusDays(1)),
                new SearchHistoryResponseDto(2, "송파 월세", LocalDateTime.now().minusDays(2)));

        return ResponseEntity.ok(ResponseDto.create(200, "검색 기록 조회 성공", searchHistories));
    }

}
