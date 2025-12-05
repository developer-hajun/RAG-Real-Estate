package ssafy.realty.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.realty.DTO.Request.PostRequestDto;
import ssafy.realty.DTO.Response.CommentResponseDto;
import ssafy.realty.DTO.Response.PostResponseDto;
import ssafy.realty.Entity.Post;
import ssafy.realty.Exception.global.DatabaseOperationException;
import ssafy.realty.Exception.post.PostNotFoundException;
import ssafy.realty.Mapper.PostMapper;
import ssafy.realty.util.JwtUtil;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostMapper postMapper;
    private final CommentService commentService;
    private final JwtUtil jwtUtil;

    @Transactional
    public void insertPost(String token, PostRequestDto post) {
        Integer userId = jwtUtil.extractUserId(token);
        if (userId == null) throw new AccessDeniedException("유효하지 않은 인증 토큰입니다.");

        post.setUserId(userId);
        if (postMapper.insertPost(post) == 0) {
            throw new DatabaseOperationException("게시글 삽입 중 오류가 발생했습니다.");
        }
    }

    public List<PostResponseDto> selectAll() {
        return postMapper.selectAll().stream()
                .map(post -> new PostResponseDto(post.getId(), post.getTitle(), post.getText(), List.of()))
                .collect(Collectors.toList());
    }

    public PostResponseDto detailPost(int postId) {
        Post post = postMapper.detailPost(postId);
        if (post == null) throw new PostNotFoundException("게시글을 찾을 수 없습니다.");

        PostResponseDto responseDto = new PostResponseDto(post);

        // 댓글 매핑
        List<CommentResponseDto> comments = Optional.ofNullable(post.getCommentList())
                .orElse(Collections.emptyList())
                .stream()
                .map(commentService::convertToDto)
                .collect(Collectors.toList());

        responseDto.setCommentDtos(comments);
        return responseDto;
    }

    @Transactional
    public void updatePost(String token, PostRequestDto postDto) {
        Integer userId = jwtUtil.extractUserId(token);
        Post find = postMapper.findById(postDto.getId());

        if (find == null) throw new PostNotFoundException("게시글이 존재하지 않습니다.");
        if (!userId.equals(find.getUser().getId())) throw new AccessDeniedException("수정 권한이 없습니다.");

        if (postMapper.updatePost(postDto) == 0) {
            throw new DatabaseOperationException("게시글 수정 실패");
        }
    }

    @Transactional
    public void deletePost(String token, int postId) {
        Integer userId = jwtUtil.extractUserId(token);
        Post find = postMapper.findById(postId);

        if (find == null) throw new PostNotFoundException("게시글이 존재하지 않습니다.");
        if (!userId.equals(find.getUser().getId())) throw new AccessDeniedException("삭제 권한이 없습니다.");

        if (postMapper.deletePost(postId) == 0) {
            throw new DatabaseOperationException("게시글 삭제 실패");
        }
    }
}