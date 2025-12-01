package ssafy.realty;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import ssafy.realty.Entity.Comment;
import ssafy.realty.Service.CommentService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CommentServiceTest {

    @Autowired private CommentService commentService;
    @Autowired private JdbcTemplate jdbcTemplate;

    private int testUserId;
    private int testPostId;

    @BeforeEach
    void setUp() {
        // 1. 유저 생성
        jdbcTemplate.update("INSERT INTO User (email, password, name, age, createdDate) VALUES ('commenter@test.com', 'pw', '댓글러', 20, NOW())");
        testUserId = jdbcTemplate.queryForObject("SELECT id FROM User WHERE email='commenter@test.com' LIMIT 1", Integer.class);

        // 2. 게시글 생성 (댓글의 부모)
        jdbcTemplate.update("INSERT INTO Post (title, text, user_id, createdDate, updatedDate) VALUES ('댓글테스트용 게시글', '본문', ?, NOW(), NOW())", testUserId);
        testPostId = jdbcTemplate.queryForObject("SELECT id FROM Post WHERE user_id=? ORDER BY id DESC LIMIT 1", Integer.class, testUserId);
    }

    @Test
    @DisplayName("1. 댓글 작성(Insert) 및 게시글별 조회(SelectByPostId)")
    void insertAndSelectByPostTest() {
        // given
        Comment comment = new Comment();
        comment.setContent("1등 댓글");

        // when: parentId가 0이면 최상위 댓글
        commentService.insertComment(comment, testPostId, testUserId, 0);

        // then
        List<Comment> comments = commentService.findByPostId(testPostId);

        assertThat(comments).isNotEmpty();
        assertThat(comments.get(0).getContent()).isEqualTo("1등 댓글");
        assertThat(comments.get(0).getCreatedDate()).isNotNull(); // 날짜 매핑 확인
    }

    @Test
    @DisplayName("2. 사용자별 댓글 조회(FindByUserId) 확인")
    void selectByUserIdTest() {
        // given
        Comment c1 = new Comment(); c1.setContent("내가 쓴 글 1");
        Comment c2 = new Comment(); c2.setContent("내가 쓴 글 2");

        commentService.insertComment(c1, testPostId, testUserId, 0);
        commentService.insertComment(c2, testPostId, testUserId, 0);

        // when
        List<Comment> myComments = commentService.findByUserId(testUserId);

        // then
        assertThat(myComments).hasSizeGreaterThanOrEqualTo(2);
        assertThat(myComments).extracting("content").contains("내가 쓴 글 1", "내가 쓴 글 2");
    }

    @Test
    @DisplayName("3. ★대댓글(Reply) 계층 구조 매핑 테스트★")
    void replyHierarchyTest() {
        // Step 1: 부모 댓글 생성
        Comment parent = new Comment();
        parent.setContent("부모 댓글");
        commentService.insertComment(parent, testPostId, testUserId, 0);

        // 부모 ID 조회 (DB에서 생성된 ID 확인)
        int parentId = commentService.findByPostId(testPostId).get(0).getId();

        // Step 2: 자식 댓글(대댓글) 생성
        Comment child = new Comment();
        child.setContent("자식 댓글입니다");
        // parentId를 명시하여 insert
        commentService.insertComment(child, testPostId, testUserId, parentId);

        // Step 3: 조회 및 구조 검증
        List<Comment> result = commentService.findByPostId(testPostId);

        // 최상위 댓글 리스트는 1개여야 함 (자식은 부모 안에 들어가 있으므로)
        // 만약 Mapper 쿼리가 틀려서 '부모1, 자식1' 이렇게 2개가 나오면 실패
        Comment parentComment = result.get(0);

        assertThat(parentComment.getContent()).isEqualTo("부모 댓글");

        // 핵심: getReplies() 안에 자식 댓글이 들어있는가?
        List<Comment> replies = parentComment.getReplies();
        assertThat(replies).isNotNull();
        assertThat(replies).hasSize(1);
        assertThat(replies.get(0).getContent()).isEqualTo("자식 댓글입니다");
        assertThat(replies.get(0).getId()).isNotEqualTo(parentComment.getId());
    }

    @Test
    @DisplayName("4. 댓글 수정(Update) 테스트")
    void updateCommentTest() {
        // given
        Comment comment = new Comment();
        comment.setContent("수정 전");
        commentService.insertComment(comment, testPostId, testUserId, 0);
        int commentId = commentService.findByPostId(testPostId).get(0).getId();

        // when
        Comment updateParams = new Comment();
        updateParams.setId(commentId);
        updateParams.setContent("수정 후");
        commentService.updateComment(updateParams);

        // then
        Comment updated = commentService.findByPostId(testPostId).get(0);
        assertThat(updated.getContent()).isEqualTo("수정 후");
        assertThat(updated.getUpdatedDate()).isAfterOrEqualTo(updated.getCreatedDate());
    }

    @Test
    @DisplayName("5. 댓글 삭제(Delete) 테스트")
    void deleteCommentTest() {
        // given
        Comment comment = new Comment();
        comment.setContent("삭제할 댓글");
        commentService.insertComment(comment, testPostId, testUserId, 0);
        int commentId = commentService.findByPostId(testPostId).get(0).getId();

        // when
        commentService.deleteComment(commentId);

        // then
        List<Comment> comments = commentService.findByPostId(testPostId);
        // post_id로 조회했을 때 해당 댓글이 없어야 함
        assertThat(comments).isEmpty();
    }
}