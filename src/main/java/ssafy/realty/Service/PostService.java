package ssafy.realty.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ssafy.realty.Entity.Post;
import ssafy.realty.Mapper.PostMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostMapper postMapper;
    public void insertPost(Post post) {
        postMapper.insertPost(post);
    }

    public List<Post> selectAll() {
        return postMapper.selectAll();
    }

    public Post DeatilPost(int postId){
        return postMapper.detailPost(postId);
    }

    public void updatePost(Post post) {
        postMapper.updatePost(post);
    }

    public void deletePost(int postId) {
        postMapper.deletePost(postId);
    }
}
