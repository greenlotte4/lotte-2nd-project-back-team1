package com.BackEndTeam1.controller;


import com.BackEndTeam1.entity.Board;
import com.BackEndTeam1.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/board") // 경로를 명시적으로 설정
public class BoardController {

    private final BoardRepository boardRepository;

    @GetMapping("/type")
    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    @PutMapping("/favorite/{boardId}")
    public void updateFavorite(@PathVariable Long boardId, @RequestBody Map<String, Boolean> requestBody) {
        Boolean isFavorite = requestBody.get("isFavorite");  // isFavorite 값을 추출
        Board board = boardRepository.findById(Math.toIntExact(boardId))
                .orElseThrow(() -> new IllegalArgumentException("Invalid board ID"));
        board.setIsFavorite(isFavorite);  // isFavorite 필드를 업데이트
        boardRepository.save(board);  // 변경 사항 저장
    }

}
