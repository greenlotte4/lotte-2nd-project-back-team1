package com.BackEndTeam1.controller;


import com.BackEndTeam1.dto.BoardArticleDTO;
import com.BackEndTeam1.dto.BoardDTO;
import com.BackEndTeam1.entity.Board;
import com.BackEndTeam1.repository.BoardRepository;
import com.BackEndTeam1.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/board") // 경로를 명시적으로 설정
public class BoardController {

    private final BoardRepository boardRepository;
    private final BoardService boardService;

    @PostMapping("/create")
    public ResponseEntity<BoardDTO> createBoard(
            @RequestBody BoardDTO boardDTO,
            @RequestParam("userId") String userId) {
        log.info("Received BoardDTO: {}", boardDTO);
        log.info("Received UserID: {}", userId);
        BoardDTO createdBoard = boardService.createBoard(boardDTO, userId);
        return ResponseEntity.ok(createdBoard);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BoardDTO> updateBoard(
            @PathVariable("id") int id,
            @RequestBody BoardDTO boardDTO,
            @RequestParam("userId") String userId) {
        boardDTO.setBoard_id(id); // DTO에 PathVariable ID를 설정
        BoardDTO updatedBoard = boardService.updateBoard(boardDTO, userId); // 수정 로직 호출
        return ResponseEntity.ok(updatedBoard);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteBoard(@PathVariable long id, @RequestParam("userId") String userId) {
        try {
            log.info("삭제 요청 ID: {}, 요청 User ID: {}", id, userId);

            // 서비스 호출
            boardService.deleteBoard(id, userId);

            log.info("삭제 성공한 ID: {}", id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.warn("삭제 실패 ID: {}, 이유: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body("게시글 삭제에 실패하였습니다.");
        } catch (Exception e) {
            log.error("Unexpected error during deletion:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("내부 서버 오류가 발생했습니다.");
        }
    }

    @GetMapping("/type")
    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    @GetMapping("/all")
    public ResponseEntity<List<BoardDTO>> getBoardList() {
        List<BoardDTO> boardList = boardService.getAllBoards();
        return ResponseEntity.ok(boardList);
    }



}
