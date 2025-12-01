package ssafy.realty.Mapper;

import org.apache.ibatis.annotations.Mapper;
import ssafy.realty.Entity.Post;
import java.util.List;

@Mapper
public interface PostMapper {

    Post detailPost(int id); // 오타 수정됨
    List<Post> selectAll();
    List<Post> findByUserId(int userId);

    int insertPost(Post post);
    int updatePost(Post post);
    int deletePost(int id);
}