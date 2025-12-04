package ssafy.realty.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ssafy.realty.Common.ResponseDto;
import ssafy.realty.DTO.PostResponseDto;
import ssafy.realty.Entity.Comment;
import ssafy.realty.Entity.Post;
import ssafy.realty.Mapper.PostMapper;
import ssafy.realty.Service.PostService;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<ResponseDto<?>> createPost(@RequestBody Post post) {
        postService.insertPost(post);//추후 JWT안에 정보로 대체 예정
        return ResponseEntity.ok(ResponseDto.create(CREATED.value(), "게시글을 생성 했습니다."));
    }

    @GetMapping
    public ResponseEntity<ResponseDto<?>> allPost() {
        List<PostResponseDto> posts = postService.selectAll();
        return ResponseEntity.ok(ResponseDto.create(HttpStatus.OK.value(), "전체 게시글을 조회 했습니다.",posts));

    }
    @GetMapping("/{PostId}")
    public ResponseEntity<ResponseDto<?>> selectDetail(int PostId) {
        PostResponseDto post = postService.detailPost(PostId);
        return ResponseEntity.ok(ResponseDto.create(HttpStatus.OK.value(), "게시물의 상세정보를 조회했습니다.",post));
    }
    @PatchMapping
    public ResponseEntity<ResponseDto<?>> updatePost(Post post) {
        postService.updatePost(post);
        return ResponseEntity.ok(ResponseDto.create(HttpStatus.OK.value(), "게시물의 댓글을 조회했습니다."));
    }
    @DeleteMapping("/{postId}")
    public ResponseEntity<ResponseDto<?>> deltePost(int postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok(ResponseDto.create(HttpStatus.OK.value(), "게시글을 삭제 했습니다."));
    }



}
