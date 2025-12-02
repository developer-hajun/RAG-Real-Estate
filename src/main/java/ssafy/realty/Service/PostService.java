package ssafy.realty.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.realty.Entity.Post;
import ssafy.realty.Exception.global.DatabaseOperationException;
import ssafy.realty.Exception.post.PostNotFoundException;
import ssafy.realty.Mapper.PostMapper;


import java.util.List;



@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 읽기 전용 트랜잭션 기본 설정
public class PostService {
    private final PostMapper postMapper;


    @Transactional // 쓰기 작업이므로 트랜잭션 적용
    public void insertPost(Post post)  {
        int result = postMapper.insertPost(post);
        if (result == 0) throw new DatabaseOperationException();

    }


    public List<Post> selectAll() {
        try{
            return postMapper.selectAll();
        }
        catch(Exception e){
            throw new DatabaseOperationException();
        }
    }


    public Post detailPost(int postId){
        Post post = postMapper.detailPost(postId);
        if (post == null) {
            // 존재하지 않는 리소스에 대한 접근이므로 ResourceNotFoundException 발생
            throw new PostNotFoundException("ID가 " + postId + "인 게시글을 찾을 수 없습니다.");
        }
        return post;
    }

    @Transactional // 쓰기 작업이므로 트랜잭션 적용
    public void updatePost(Post post) {
        int result = postMapper.updatePost(post);
        if (result == 0) {
            // 수정 대상 게시글이 없거나, 다른 이유로 수정이 실패한 경우
            throw new PostNotFoundException("수정할 게시글(ID: " + post.getId() + ")을 찾을 수 없습니다.");
        }
    }

    @Transactional
    public void deletePost(int postId) {
        int result = postMapper.deletePost(postId);
        if (result == 0) {
            throw new PostNotFoundException("삭제할 게시글(ID: " + postId + ")을 찾을 수 없습니다.");
        }
    }
}