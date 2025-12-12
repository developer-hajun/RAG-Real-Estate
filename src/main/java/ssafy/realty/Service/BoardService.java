package ssafy.realty.Service;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ssafy.realty.Entity.Board;
import ssafy.realty.Exception.global.DatabaseOperationException;
import ssafy.realty.Mapper.BoardMapper;

import ssafy.realty.DTO.Response.BoardDetailResponseDto;
import ssafy.realty.DTO.Response.PostResponseDto; // PostResponseDto 임포트

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardService {

    private final BoardMapper boardMapper;

    public List<BoardDetailResponseDto> getBoards() {
        List<Board> boards = boardMapper.selectAllBoardsWithoutPosts();

        return boards.stream()
                .map(BoardService::convertToBoardListDto)
                .collect(Collectors.toList());
    }

    public BoardDetailResponseDto getBoardDetail(int boardId) {
        Board board = boardMapper.selectBoardWithPostsById(boardId);

        if (board == null) {
            throw new DatabaseOperationException("게시판 ID " + boardId + "를 찾을 수 없습니다.");
        }

        return convertToBoardDetailDto(board);
    }

    private static BoardDetailResponseDto convertToBoardListDto(Board board) {
        return BoardDetailResponseDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .createdDate(board.getCreatedDate())
                .updatedDate(board.getUpdatedDate())
                .build();
    }

    private BoardDetailResponseDto convertToBoardDetailDto(Board board) {

        List<PostResponseDto> postDtos = board.getPosts().stream()
                .map(PostResponseDto::fromEntity)
                .collect(Collectors.toList());

        return new BoardDetailResponseDto(
                board.getId(),
                board.getTitle(),
                board.getCreatedDate(),
                board.getUpdatedDate(),
                postDtos
        );
    }
}