package ssafy.realty.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ssafy.realty.Common.ResponseDto;
import ssafy.realty.Entity.Comment;
import ssafy.realty.Mapper.CommentMapper;
import ssafy.realty.Service.CommentService;

import javax.validation.Valid;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ResponseDto<?>> createComment(Comment comment,int postId,int UserId,int parentCommentId) {
        commentService.insertComment(comment,postId,UserId,parentCommentId);
        return ResponseEntity.ok(ResponseDto.create(CREATED.value(), "댓글 객체를 생성 했습니다."));
    }
    @GetMapping("/user/{id}")
    public ResponseEntity<ResponseDto<?>> userComment(int UserId) {
        List<Comment> byUserId = commentService.findByUserId(UserId);
        return ResponseEntity.ok(ResponseDto.create(HttpStatus.OK.value(), "게시물의 댓글을 조회했습니다.",byUserId));
    }
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ResponseDto<?>> deleteComment(int commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(ResponseDto.create(HttpStatus.OK.value(), "댓글 객체를 삭제 했습니다."));
    }

    @PatchMapping()
    public ResponseEntity<ResponseDto<?>> updateComment(Comment comment) {
        commentService.updateComment(comment);
        return ResponseEntity.ok(ResponseDto.create(HttpStatus.OK.value(), "댓글 객체를 수정 했습니다."));
    }

}
