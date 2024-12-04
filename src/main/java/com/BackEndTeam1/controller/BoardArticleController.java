package com.BackEndTeam1.controller;

import com.BackEndTeam1.dto.BoardArticleDTO;
import com.BackEndTeam1.service.BoardArticleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/article")
public class BoardArticleController {

    private final BoardArticleService boardArticleService;

    @PostMapping("write")
    public ResponseEntity<?> write(@RequestBody BoardArticleDTO boardArticleDTO) {
        try {
            log.info(boardArticleDTO.toString());

            // 저장 로직 처리
            int id = boardArticleService.save(boardArticleDTO);

            // 응답으로 게시글 ID 반환
            return ResponseEntity.ok(id);  // id 반환
        } catch (Exception e) {
            log.error("게시글 등록 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 등록에 실패했습니다.");
        }
    }


}
