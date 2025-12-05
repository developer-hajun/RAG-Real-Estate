package ssafy.realty.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssafy.realty.Common.ResponseDto;
import ssafy.realty.DTO.Request.PostRequestDto;
import ssafy.realty.DTO.Response.PostResponseDto;
import ssafy.realty.Service.PostService;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;


@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<ResponseDto<?>> createPost(@RequestHeader("Authorization") String token,
                                                     @RequestBody PostRequestDto post) {
        postService.insertPost(token, post);
        return ResponseEntity.ok(ResponseDto.create(HttpStatus.CREATED.value(), "게시글을 생성 했습니다."));
    }

    @GetMapping
    public ResponseEntity<ResponseDto<?>> allPost() {
        List<PostResponseDto> posts = postService.selectAll();
        return ResponseEntity.ok(ResponseDto.create(HttpStatus.OK.value(), "전체 게시글을 조회 했습니다.", posts));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ResponseDto<?>> selectDetail(@PathVariable int postId) {
        PostResponseDto post = postService.detailPost(postId);
        return ResponseEntity.ok(ResponseDto.create(HttpStatus.OK.value(), "게시물의 상세정보를 조회했습니다.", post));
    }

    @PatchMapping
    public ResponseEntity<ResponseDto<?>> updatePost(@RequestHeader("Authorization") String token,
                                                     @RequestBody PostRequestDto post) {
        postService.updatePost(token, post);
        return ResponseEntity.ok(ResponseDto.create(HttpStatus.OK.value(), "게시글을 수정했습니다."));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ResponseDto<?>> deletePost(@RequestHeader("Authorization") String token,
                                                     @PathVariable int postId) {
        postService.deletePost(token, postId);
        return ResponseEntity.ok(ResponseDto.create(HttpStatus.OK.value(), "게시글을 삭제 했습니다."));
    }
}
