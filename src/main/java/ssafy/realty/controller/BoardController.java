package ssafy.realty.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ssafy.realty.DTO.Response.BoardDetailResponseDto;
import ssafy.realty.DTO.Response.BoardListResponseDto;
// import ssafy.realty.Exception.global.DatabaseOperationException; // Controller에서 직접 사용할 필요 없음
import ssafy.realty.Service.BoardService;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping
    public ResponseEntity<List<BoardListResponseDto>> getBoardList() {
        List<BoardListResponseDto> response = boardService.getBoards();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDetailResponseDto> getBoardDetail(@PathVariable("boardId") int boardId) {
        BoardDetailResponseDto response = boardService.getBoardDetail(boardId);
        return ResponseEntity.ok(response);
    }
}