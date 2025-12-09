package ssafy.realty.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException; // 인가 실패 예외
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 트랜잭션 관리
import ssafy.realty.DTO.Request.CommentRequestDto;
import ssafy.realty.DTO.Response.CommentResponseDto;
import ssafy.realty.Entity.Comment;
import ssafy.realty.Exception.comment.CommentNotFoundException;
import ssafy.realty.Exception.global.DatabaseOperationException;
import ssafy.realty.Mapper.CommentMapper;
import ssafy.realty.util.JwtUtil; // JWT 유틸리티 추가

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentMapper commentMapper;
    private final JwtUtil jwtUtil;

    public List<CommentResponseDto> findByUserId(String token) {
        Integer userId = jwtUtil.extractUserId(token);
        if (userId == null) throw new AccessDeniedException("유효하지 않은 토큰입니다.");

        List<Comment> comments = commentMapper.selectCommentsByUserId(userId);
        if (comments == null) return Collections.emptyList();

        return comments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public int insertComment(String token, CommentRequestDto requestDto) {
        Integer userId = jwtUtil.extractUserId(token);
        if (userId == null) throw new AccessDeniedException("유효하지 않은 토큰입니다.");

        Comment comment = new Comment();
        comment.setContent(requestDto.getContent());

        int result = commentMapper.insertComment(comment, requestDto.getPostId(), userId, requestDto.getParentsId());

        if (result == 0) throw new DatabaseOperationException("댓글 저장 실패");
        return result;
    }

    @Transactional
    public int updateComment(String token, CommentRequestDto requestDto) {
        Integer userId = jwtUtil.extractUserId(token);

        Comment findComment = commentMapper.findById(requestDto.getId());
        if (findComment == null) throw new CommentNotFoundException("댓글을 찾을 수 없습니다.");

        // 권한 체크
        if (!userId.equals(findComment.getUser().getId())) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

        Comment updateTarget = new Comment();
        updateTarget.setId(requestDto.getId());
        updateTarget.setContent(requestDto.getContent());

        int result = commentMapper.updateComment(updateTarget);
        if (result == 0) throw new CommentNotFoundException("댓글 수정 실패");
        return result;
    }

    @Transactional
    public int deleteComment(String token, Integer commentId) {
        Integer userId = jwtUtil.extractUserId(token);

        Comment findComment = commentMapper.findById(commentId);
        if (findComment == null) throw new CommentNotFoundException("댓글을 찾을 수 없습니다.");

        if (!userId.equals(findComment.getUser().getId())) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        int result = commentMapper.deleteComment(commentId);
        if (result == 0) throw new CommentNotFoundException("댓글 삭제 실패");
        return result;
    }

    public CommentResponseDto convertToDto(Comment comment) {
        // 1. 기본 댓글 정보 매핑 (id, content, updatedDate 등)
        CommentResponseDto dto = new CommentResponseDto(comment);

        // 2. 대댓글(Replies) 처리
        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            List<CommentResponseDto> replyDtos = comment.getReplies().stream()
                    .map(this::convertToDto) // 재귀 호출로 계층 구조 생성
                    .collect(Collectors.toList());
            dto.setReplies(replyDtos); // ⭐ 새로 추가된 replies 필드에 설정 ⭐
        }

        return dto;
    }
}