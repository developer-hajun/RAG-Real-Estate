package ssafy.realty.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssafy.realty.Common.ResponseDto;
import ssafy.realty.DTO.Request.CommentRequestDto;
import ssafy.realty.DTO.Response.CommentResponseDto;
import ssafy.realty.Service.CommentService;

import java.util.List;


@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ResponseDto<?>> createComment(@RequestHeader("Authorization") String token,
                                                        @RequestBody CommentRequestDto requestDto) {
        commentService.insertComment(token, requestDto);
        return ResponseEntity.ok(ResponseDto.create(HttpStatus.CREATED.value(), "댓글을 생성 했습니다."));
    }

    @GetMapping("/my-comments")
    public ResponseEntity<ResponseDto<?>> userComment(@RequestHeader("Authorization") String token) {
        List<CommentResponseDto> byUserId = commentService.findByUserId(token);
        return ResponseEntity.ok(ResponseDto.create(HttpStatus.OK.value(), "내 댓글 목록을 조회했습니다.", byUserId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ResponseDto<?>> deleteComment(@RequestHeader("Authorization") String token,
                                                        @PathVariable int commentId) {
        commentService.deleteComment(token, commentId);
        return ResponseEntity.ok(ResponseDto.create(HttpStatus.OK.value(), "댓글을 삭제 했습니다."));
    }

    @PatchMapping
    public ResponseEntity<ResponseDto<?>> updateComment(@RequestHeader("Authorization") String token,
                                                        @RequestBody CommentRequestDto requestDto) {
            commentService.updateComment(token, requestDto);
        return ResponseEntity.ok(ResponseDto.create(HttpStatus.OK.value(), "댓글을 수정 했습니다."));
    }
}
