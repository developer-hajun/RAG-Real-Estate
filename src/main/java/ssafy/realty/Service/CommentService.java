package ssafy.realty.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ssafy.realty.Entity.Comment;
import ssafy.realty.Exception.comment.CommentNotFoundException;
import ssafy.realty.Exception.global.DatabaseOperationException;
import ssafy.realty.Mapper.CommentMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentMapper commentMapper;

    public List<Comment> findByUserId(Integer userId) {
        try{
            return commentMapper.selectCommentsByUserId(userId);
        }
        catch (Exception e){
            throw new DatabaseOperationException();
        }

    }

    public int insertComment(Comment comment,int postId,int UserId ,int parentCommentId) {

        int val =  commentMapper.insertComment(comment,postId,UserId,parentCommentId);
        if(val==0){
            throw new DatabaseOperationException();
        }
        return val;
    }

    public int updateComment(Comment comment) {
        int val = commentMapper.updateComment(comment);
        if(val==0){
            throw new CommentNotFoundException();
        }
        return val;
    }

    public
    int deleteComment(Integer commentId) {
        int val = commentMapper.deleteComment(commentId);
        if(val==0){
            throw new CommentNotFoundException();
        }
        return val;
    }



}
