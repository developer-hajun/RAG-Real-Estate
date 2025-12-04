package ssafy.realty.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ssafy.realty.DTO.CommentResponseDto;
import ssafy.realty.Entity.Comment;
import ssafy.realty.Exception.comment.CommentNotFoundException;
import ssafy.realty.Exception.global.DatabaseOperationException;
import ssafy.realty.Mapper.CommentMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentMapper commentMapper;

    /**
     * 특정 사용자 ID의 댓글 목록을 조회하고 CommentResponseDto로 변환하여 반환합니다.
     */
    public List<CommentResponseDto> findByUserId(Integer userId) {
        try {
            List<Comment> comments = commentMapper.selectCommentsByUserId(userId);

            // Stream API를 사용하여 엔티티 리스트를 DTO 리스트로 변환
            return comments.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new DatabaseOperationException();
        }
    }

    public int insertComment(Comment comment, int postId, int UserId, int parentCommentId) {
        int val = commentMapper.insertComment(comment, postId, UserId, parentCommentId);
        if (val == 0) {
            throw new DatabaseOperationException();
        }
        return val;
    }

    public int updateComment(Comment comment) {
        int val = commentMapper.updateComment(comment);
        if (val == 0) {
            throw new CommentNotFoundException();
        }
        return val;
    }


    public int deleteComment(Integer commentId) {
        int val = commentMapper.deleteComment(commentId);
        if (val == 0) {
            throw new CommentNotFoundException();
        }
        return val;
    }


    CommentResponseDto convertToDto(Comment comment) {
        CommentResponseDto parentDto = null;

        Comment parentEntity = comment.getParentComment();
        if (parentEntity != null) {
            parentDto = convertToDto(parentEntity);
        }

        return new CommentResponseDto(
                comment.getId(),
                comment.getContent(),
                parentDto, // 변환된 DTO를 사용
                comment.getUpdatedDate()
        );
    }
}