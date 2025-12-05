package ssafy.realty.Mapper;

import org.apache.ibatis.annotations.Mapper;
import ssafy.realty.DTO.Request.PostRequestDto;
import ssafy.realty.Entity.Post;
import java.util.List;

@Mapper
public interface PostMapper {

    Post findById(int id);
    Post detailPost(int id); 
    List<Post> selectAll();
    List<Post> findByUserId(int userId);

    int insertPost(PostRequestDto post);
    int updatePost(PostRequestDto post);
    int deletePost(int id);
}