package ssafy.realty;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import ssafy.realty.Entity.Post;
import ssafy.realty.Entity.User;
import ssafy.realty.Service.PostService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional // 테스트 종료 후 데이터 롤백 (초기화)
class PostServiceTest {

    @Autowired private PostService postService;
    @Autowired private JdbcTemplate jdbcTemplate;

    private int testUserId;

    @BeforeEach
    void setUp() {
        // 모든 테스트 전에 "작성자(User)"를 미리 생성해둠
        String sql = "INSERT INTO User (email, password, name, age, createdDate, updatedDate) " +
                "VALUES ('postwriter@test.com', '1234', '김게시', 30, NOW(), NOW())";
        jdbcTemplate.update(sql);
        testUserId = jdbcTemplate.queryForObject("SELECT id FROM User WHERE email='postwriter@test.com' LIMIT 1", Integer.class);
    }

    @Test
    @DisplayName("1. 게시글 작성(Insert) 및 ID 자동 생성 확인")
    void insertPostTest() {
        // given
        User user = new User();
        user.setId(testUserId);
        Post post = new Post();
        post.setTitle("새로운 글");
        post.setText("내용입니다.");
        post.setUser(user);

        // when
        postService.insertPost(post);

        // then
        assertThat(post.getId()).isGreaterThan(0); // ID가 생성되었는지
        System.out.println("Generated Post ID: " + post.getId());
    }

    @Test
    @DisplayName("2. 게시글 상세 조회(Detail) - 제목, 내용, 작성자 정보 확인")
    void detailPostTest() {
        // given
        User user = new User();
        user.setId(testUserId);
        Post post = new Post();
        post.setTitle("상세조회용 제목");
        post.setText("상세조회용 내용");
        post.setUser(user);
        postService.insertPost(post);

        // when
        // 서비스 메서드명이 DeatilPost(오타)로 되어 있어 그대로 호출함
        Post result = postService.DeatilPost(post.getId());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("상세조회용 제목");
        assertThat(result.getText()).isEqualTo("상세조회용 내용");
        // 작성자 정보가 잘 매핑되었는지 (MyBatis Association)
        assertThat(result.getUser()).isNotNull();
        assertThat(result.getUser().getId()).isEqualTo(testUserId);
    }

    @Test
    @DisplayName("3. 게시글 수정(Update) 및 날짜 업데이트 확인")
    void updatePostTest() {
        // given
        User user = new User();
        user.setId(testUserId);
        Post post = new Post(0, "원래 제목", "원래 내용", user, null, null, null);
        postService.insertPost(post);

        // when
        post.setTitle("수정된 제목");
        post.setText("수정된 내용");
        postService.updatePost(post);

        // then
        Post updatedPost = postService.DeatilPost(post.getId());
        assertThat(updatedPost.getTitle()).isEqualTo("수정된 제목");
        assertThat(updatedPost.getText()).isEqualTo("수정된 내용");
        // DB 트리거가 없다면 Java 레벨에서 NOW()가 잘 들어갔는지 확인
        assertThat(updatedPost.getUpdatedDate()).isNotNull();
    }

    @Test
    @DisplayName("4. 게시글 삭제(Delete) 확인")
    void deletePostTest() {
        // given
        User user = new User();
        user.setId(testUserId);
        Post post = new Post(0, "삭제될 글", "내용", user, null, null, null);
        postService.insertPost(post);
        int postId = post.getId();

        // when
        postService.deletePost(postId);

        // then
        Post deletedPost = postService.DeatilPost(postId);
        assertThat(deletedPost).isNull(); // 조회 결과가 없어야 함
    }

    @Test
    @DisplayName("5. 전체 게시글 조회(SelectAll) - 리스트 개수 확인")
    void selectAllTest() {
        // given
        User user = new User();
        user.setId(testUserId);
        postService.insertPost(new Post(0, "글1", "내용1", user, null, null, null));
        postService.insertPost(new Post(0, "글2", "내용2", user, null, null, null));

        // when
        List<Post> allPosts = postService.selectAll();

        // then
        assertThat(allPosts).size().isGreaterThanOrEqualTo(2);
        assertThat(allPosts).extracting("title")
                .contains("글1", "글2");
    }
}