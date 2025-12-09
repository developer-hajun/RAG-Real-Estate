package ssafy.realty;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ssafy.realty.DTO.Response.BoardDetailResponseDto;
import ssafy.realty.DTO.Response.BoardListResponseDto;
import ssafy.realty.Entity.Board;
import ssafy.realty.Entity.Post;
import ssafy.realty.Mapper.BoardMapper;
import ssafy.realty.Service.BoardService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.time.LocalTime.now;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// JUnit 5와 Mockito 사용 환경 설정
@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    // 외부 의존성인 BoardMapper를 Mock 객체로 대체
    @Mock
    private BoardMapper boardMapper;

    // 테스트 대상인 BoardService에 Mock 객체를 주입
    @InjectMocks
    private BoardService boardService;

    // 테스트 데이터 초기화
    private final LocalDateTime now = LocalDateTime.of(2025, 12, 9, 11, 0);
    // Post 엔티티에는 id, title, text, comments(null 처리) 필드가 있다고 가정
    private final Post mockPost = new Post(101, "테스트 포스트 제목", "내용", LocalDateTime.now(), LocalDateTime.now());
    // Board 엔티티에는 id, title, posts, createdDate, updatedDate 필드가 있다고 가정
    private final Board mockBoard = new Board(1, "테스트 게시판", List.of(mockPost), now, now);

    // --------------------------------------------------------------------------------
    // 1. 게시판 목록 조회 테스트 (getBoards)
    // --------------------------------------------------------------------------------

    @Test
    @DisplayName("✅ 성공: 게시판 목록 조회 시 BoardListResponseDto 리스트를 반환")
    void getBoards_Success() {
        // Given
        Board board1 = new Board(1, "공지사항", Collections.emptyList(), now, now);
        List<Board> mockBoards = Arrays.asList(board1);

        when(boardMapper.selectAllBoardsWithoutPosts()).thenReturn(mockBoards);

        // When
        List<BoardListResponseDto> result = boardService.getBoards();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("공지사항", result.get(0).getTitle());
        verify(boardMapper, times(1)).selectAllBoardsWithoutPosts(); // Mapper 호출 검증
    }
    
    // --------------------------------------------------------------------------------
    // 2. 특정 게시판 상세 조회 테스트 (getBoardDetail)
    // --------------------------------------------------------------------------------

    @Test
    @DisplayName("✅ 성공: 유효한 ID로 상세 조회 시 Post를 포함한 DTO를 반환")
    void getBoardDetail_Success() {
        // Given
        when(boardMapper.selectBoardWithPostsById(1)).thenReturn(mockBoard);

        // When
        BoardDetailResponseDto result = boardService.getBoardDetail(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getId());
        // Posts 리스트가 제대로 변환되었는지 검증
        assertNotNull(result.getPosts());
        assertEquals(1, result.getPosts().size());
        assertEquals("테스트 포스트 제목", result.getPosts().get(0).getTitle());
        verify(boardMapper, times(1)).selectBoardWithPostsById(1); // Mapper 호출 검증
    }

    // BoardServiceTest.java (수정된 getBoardDetail_NotFound_ThrowsException)

    @Test
    @DisplayName("❌ 실패: 존재하지 않는 ID 조회 시 RuntimeException을 발생시킨다")
    void getBoardDetail_NotFound_ThrowsException() {
        // Given
        int notFoundId = 999;
        when(boardMapper.selectBoardWithPostsById(notFoundId)).thenReturn(null);

        // 1. assertThrows를 사용하여 예외 발생 여부 검증
        RuntimeException thrown = assertThrows(
                // 서비스 코드는 DatabaseOperationException을 던지지만,
                // DatabaseOperationException이 RuntimeException을 상속하므로 상위 타입으로 검증 가능
                RuntimeException.class,
                () -> boardService.getBoardDetail(notFoundId),
                "게시판을 찾을 수 없을 때 RuntimeException이 발생해야 합니다."
        );

        // 2. 예외 메시지 검증 (전체 문자열 일치 확인)
        final String expectedMessage = "게시판 ID " + notFoundId + "를 찾을 수 없습니다.";
        assertEquals(expectedMessage, thrown.getMessage(),
                "예외 메시지 내용이 '게시판 ID [ID]를 찾을 수 없습니다.'와 정확히 일치해야 합니다.");

        verify(boardMapper, times(1)).selectBoardWithPostsById(notFoundId);
    }
}