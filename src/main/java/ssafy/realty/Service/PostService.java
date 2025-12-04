package ssafy.realty.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.realty.DTO.CommentResponseDto;
import ssafy.realty.DTO.PostResponseDto;
import ssafy.realty.Entity.Post;
import ssafy.realty.Exception.global.DatabaseOperationException;
import ssafy.realty.Exception.post.PostNotFoundException;
import ssafy.realty.Mapper.PostMapper;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) 
public class PostService {
    private final PostMapper postMapper;
    private final CommentService commentService;

    @Transactional // 쓰기 작업이므로 트랜잭션 적용
    public void insertPost(Post post)  {
        int result = postMapper.insertPost(post);
        if (result == 0) throw new DatabaseOperationException();
    }

    public List<PostResponseDto> selectAll() {
        try{
            return allPostDto(postMapper.selectAll());
        }
        catch(Exception e){
            // 상세한 예외 로깅 필요
            throw new DatabaseOperationException();
        }
    }

    public PostResponseDto detailPost(int postId){
        Post post = postMapper.detailPost(postId);
        if (post == null) {
            throw new PostNotFoundException("ID가 " + postId + "인 게시글을 찾을 수 없습니다.");
        }

        // 1. DTO 변환 (게시글 기본 정보)
        PostResponseDto postResponseDto = new PostResponseDto(post);

        try {
            List<CommentResponseDto> comments = post.getCommentList().stream()
                    .map(commentService::convertToDto)
                    .collect(Collectors.toList());

            postResponseDto.setCommentDtos(comments);

        } catch (Exception e) {
            System.err.println("댓글 조회 중 오류 발생: " + e.getMessage());
            postResponseDto.setCommentDtos(List.of());
        }

        return postResponseDto;
    }

    @Transactional
    public void updatePost(Post post) {
        int result = postMapper.updatePost(post);
        if (result == 0) {
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

    protected List<PostResponseDto> allPostDto(List<Post> posts) {
        return posts.stream()
                .map(this::convertToSimpleDto)
                .collect(Collectors.toList());
    }


    private PostResponseDto convertToSimpleDto(Post post) {
        return new PostResponseDto(post.getId(), post.getTitle(), post.getText(), List.of());
    }

}