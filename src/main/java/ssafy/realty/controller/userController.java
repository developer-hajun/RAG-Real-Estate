package ssafy.realty.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssafy.realty.Common.ResponseDto;
import ssafy.realty.DTO.Response.SearchHistoryResponseDto;
import ssafy.realty.DTO.Request.UserRequestDto;
import ssafy.realty.DTO.Response.UserResponseDto;
import ssafy.realty.Service.RealtyService;
import ssafy.realty.Service.UserService;
import ssafy.realty.util.JwtUtil;

import java.util.List;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class userController {

    private final UserService userService;
    private final RealtyService realtyService;
    private final JwtUtil jwtUtil;

    //프로필 조회
    @GetMapping("/profile")
    public ResponseEntity<ResponseDto<?>> getMyProfile(@RequestHeader("Authorization") String authorization) {
        Integer userId = jwtUtil.extractUserId(authorization);
        if(userId == null){
            return ResponseEntity.ok(ResponseDto.create(401, "유효하지 않은 토큰입니다."));
        }

        UserResponseDto profile = userService.getProfile(userId);
        if(profile == null){
            return ResponseEntity.ok(ResponseDto.create(404, "사용자를 찾을 수 없습니다."));
        }

        return ResponseEntity.ok(ResponseDto.create(200, "프로필 조회 성공", profile));
    }

    // 프로필 수정
    @PutMapping("/profile")
    public ResponseEntity<ResponseDto<?>> updateMyProfile(@RequestHeader("Authorization") String authorization,
                                                          @RequestBody UserRequestDto userRequestDto) {
        Integer userId = jwtUtil.extractUserId(authorization);
        if(userId == null){
            return ResponseEntity.ok(ResponseDto.create(401, "유효하지 않은 토큰입니다."));
        }
        UserResponseDto updatedProfile = userService.updateProfile(userId, userRequestDto);
        if(updatedProfile == null) {
            return ResponseEntity.ok(ResponseDto.create(404, "사용자를 찾을 수 없습니다. or 이메일은 변경할 수 없습니다.."));
        }
        return ResponseEntity.ok(ResponseDto.create(200, "프로필 수정 성공", updatedProfile));
    }

    // 검색 기록 조회
    @GetMapping("/search-history")
    public ResponseEntity<ResponseDto<?>> getSearchHistory(@RequestHeader("Authorization") String authorization) {
        Integer userId = jwtUtil.extractUserId(authorization);
        if (userId == null) {
            return ResponseEntity.ok(ResponseDto.create(401, "유효하지 않은 토큰입니다."));
        }
        List<SearchHistoryResponseDto> histories = userService.getMySearchHistory(userId);
        // 검색 기록이 없을 때 빈 리스트 반환 굳이 체크할 필요 없음
        return ResponseEntity.ok(ResponseDto.create(200, "검색 기록 조회 성공", histories));
    }

    // 찜 목록 조회
    @GetMapping("/favorites")
    public ResponseEntity<ResponseDto<?>> myFavorites(@RequestHeader("Authorization") String authorization) {
        Integer userId = jwtUtil.extractUserId(authorization);
        if (userId == null) {
            return ResponseEntity.ok(ResponseDto.create(401, "유효하지 않은 토큰입니다."));
        }
        return ResponseEntity.ok(ResponseDto.create(200, "찜 목록 조회 성공", realtyService.myFavorites(userId)));
    }



}
