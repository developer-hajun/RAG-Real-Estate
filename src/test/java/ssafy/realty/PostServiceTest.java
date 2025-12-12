package ssafy.realty;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import ssafy.realty.DTO.Request.PostRequestDto;
import ssafy.realty.DTO.Response.PostResponseDto;
import ssafy.realty.Service.PostService;
import ssafy.realty.util.JwtUtil;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired private PostService postService;
    @Autowired private JdbcTemplate jdbcTemplate;

    @MockBean private JwtUtil jwtUtil;

    private int testUserId;
    private final String TEST_TOKEN = "Bearer test_token";

    @BeforeEach
    void setUp() {
        // 1. 테스트용 유저 생성 (DB)
        String sql = "INSERT INTO User (email, password, name, age, createdDate, updatedDate) " +
                "VALUES ('postwriter@test.com', '1234', '김게시', 30, NOW(), NOW())";
        jdbcTemplate.update(sql);
        testUserId = jdbcTemplate.queryForObject("SELECT id FROM User WHERE email='postwriter@test.com' LIMIT 1", Integer.class);

        given(jwtUtil.extractUserId(anyString())).willReturn(testUserId);
    }

    @Test
    @DisplayName("1. 게시글 작성(Insert) 및 ID 자동 생성 확인")
    void insertPostTest() {
        // given
        PostRequestDto requestDto = new PostRequestDto();
        requestDto.setTitle("새로운 글");
        requestDto.setText("내용입니다.");
        requestDto.setBoardId(1);
        postService.insertPost(TEST_TOKEN, requestDto);

        assertThat(requestDto.getId()).isGreaterThan(0);
        System.out.println("Generated Post ID: " + requestDto.getId());
    }

    @Test
    @DisplayName("2. 게시글 상세 조회(Detail) - 제목, 내용 확인")
    void detailPostTest() {
        PostRequestDto requestDto = new PostRequestDto();
        requestDto.setTitle("상세조회용 제목");
        requestDto.setText("상세조회용 내용");
        requestDto.setBoardId(1);
        postService.insertPost(TEST_TOKEN, requestDto);
        int postId = requestDto.getId();

        PostResponseDto result = postService.detailPost(postId);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("상세조회용 제목");
        assertThat(result.getText()).isEqualTo("상세조회용 내용");
    }

    @Test
    @DisplayName("3. 게시글 수정(Update) 확인")
    void updatePostTest() {
        PostRequestDto originPost = new PostRequestDto();
        originPost.setTitle("원래 제목");
        originPost.setText("원래 내용");
        originPost.setBoardId(1);
        postService.insertPost(TEST_TOKEN, originPost);
        int postId = originPost.getId();

        PostRequestDto updatePost = new PostRequestDto();
        updatePost.setId(postId); // 수정할 게시글 ID 지정
        updatePost.setTitle("수정된 제목");
        updatePost.setText("수정된 내용");

        postService.updatePost(TEST_TOKEN, updatePost);

        PostResponseDto result = postService.detailPost(postId);
        assertThat(result.getTitle()).isEqualTo("수정된 제목");
        assertThat(result.getText()).isEqualTo("수정된 내용");
    }

    @Test
    @DisplayName("4. 게시글 삭제(Delete) 확인")
    void deletePostTest() {
        PostRequestDto post = new PostRequestDto();
        post.setTitle("삭제될 글");
        post.setText("내용");
        post.setBoardId(1);
        postService.insertPost(TEST_TOKEN, post);
        int postId = post.getId();

        postService.deletePost(TEST_TOKEN, postId);

        assertThrows(Exception.class, () -> {
            postService.detailPost(postId);
        });
    }

    @Test
    @DisplayName("5. 전체 게시글 조회(SelectAll) - 리스트 개수 확인")
    void selectAllTest() {
        PostRequestDto p1 = new PostRequestDto(); p1.setTitle("글1"); p1.setText("내용1"); p1.setBoardId(1);
        PostRequestDto p2 = new PostRequestDto(); p2.setTitle("글2"); p2.setText("내용2"); p2.setBoardId(1);

        postService.insertPost(TEST_TOKEN, p1);
        postService.insertPost(TEST_TOKEN, p2);

        List<PostResponseDto> allPosts = postService.selectAll();

        assertThat(allPosts).size().isGreaterThanOrEqualTo(2);
        assertThat(allPosts).extracting("title")
                .contains("글1", "글2");
    }

    @Test
    @DisplayName("🚨 예외 상황: 댓글이 있는 게시글 삭제 시 DB 에러 발생 확인")
    void deletePostWithCommentsTest() {
        // given
        // 1. 게시글 작성
        PostRequestDto post = new PostRequestDto();
        post.setTitle("삭제할 글");
        post.setText("내용");
        post.setBoardId(1);
        postService.insertPost(TEST_TOKEN, post);
        int postId = post.getId();

        // 2. 댓글 강제 삽입 (SQL 사용)
        jdbcTemplate.update("INSERT INTO Comment (post_id, user_id, content, createdDate, updatedDate) VALUES (?, ?, '못 지울걸?', NOW(), NOW())",
                postId, testUserId);

        try {
            postService.deletePost(TEST_TOKEN, postId);
        } catch (Exception e) {
            System.out.println("예상된 에러 발생: " + e.getMessage());
            assertThat(e).isInstanceOf(RuntimeException.class);
        }
    }
}