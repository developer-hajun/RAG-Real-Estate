package ssafy.realty.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ssafy.realty.Entity.Comment;
import ssafy.realty.Mapper.CommentMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentMapper commentMapper;

    public List<Comment> findByUserId(Integer userId) {
        return commentMapper.selectCommentsByUserId(userId);
    }

    public List<Comment> findByPostId(Integer postId) {
        return commentMapper.selectPostByID(postId);
    }

    public int insertComment(Comment comment,int postId,int UserId ,int parentCommentId) {
        return commentMapper.insertComment(comment,postId,UserId,parentCommentId);
    }

    public int updateComment(Comment comment) {
        return commentMapper.updateComment(comment);
    }

    public
    int deleteComment(Integer commentId) {
        return commentMapper.deleteComment(commentId);
    }



}
