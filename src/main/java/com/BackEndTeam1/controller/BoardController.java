package com.BackEndTeam1.controller;


import com.BackEndTeam1.dto.BoardDTO;
import com.BackEndTeam1.entity.Board;
import com.BackEndTeam1.repository.BoardRepository;
import com.BackEndTeam1.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/board") // 경로를 명시적으로 설정
public class BoardController {

    private final BoardRepository boardRepository;
    private final BoardService boardService;

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
