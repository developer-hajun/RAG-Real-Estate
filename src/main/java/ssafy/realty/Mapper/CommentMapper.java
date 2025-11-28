package ssafy.realty.Mapper;

import org.apache.ibatis.annotations.Mapper;
import ssafy.realty.Entity.Comment;

import java.util.List;

@Mapper
public interface CommentMapper {

    List<Comment> selectCommentsByUserId(Integer userId);

    List<Comment> selectPostByID(Integer postId);

    int updateComment(Comment comment);

    int insertComment(Comment comment,int postId,int UserId);

    int deleteComment(Integer commentId);


}
