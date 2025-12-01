package ssafy.realty.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import ssafy.realty.Entity.Comment;
import java.util.List;

@Mapper
public interface CommentMapper {

    List<Comment> selectCommentsByUserId(Integer userId);

    // XML에서 #{postId}로 받기 위해 @Param 추가
    List<Comment> selectPostByID(@Param("postId") Integer postId);

    int updateComment(Comment comment);

    int insertComment(@Param("comment") Comment comment,
                      @Param("postId") int postId,
                      @Param("userId") int userId,
                      @Param("parentCommentId") int parentCommentId);

    int deleteComment(Integer commentId);
}