package com.BackEndTeam1.controller;

import com.BackEndTeam1.dto.CommentDTO;
import com.BackEndTeam1.dto.ReplyDTO;
import com.BackEndTeam1.service.CommentService;
import com.BackEndTeam1.service.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;
    private final ReplyService replyService;

    @PostMapping("/add")
    public ResponseEntity<?> addComment(@RequestBody CommentDTO commentDTO) {
        log.info("Received CommentDTO: {}", commentDTO); // 요청 데이터 로깅
        if (commentDTO.getUserId() == null || commentDTO.getBoardArticleId() == null) {
            return ResponseEntity.badRequest().body("User ID or Article ID must not be null");
        }

        commentService.saveComment(commentDTO);
        return ResponseEntity.ok("댓글이 성공적으로 저장되었습니다.");
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByArticle(@PathVariable Integer commentId) {
        List<CommentDTO> comments = commentService.getCommentsByArticleId(Long.valueOf(commentId));
        log.info("여기임" +comments);
        return ResponseEntity.ok(comments);
    }

    // 답글 추가
    @PostMapping("/{commentId}/reply")
    public ResponseEntity<?> addReply(@PathVariable Integer commentId, @RequestBody ReplyDTO replyDTO) {
        log.info("Received commentId: " + commentId);
        // 기본 검증
        if (commentId == null) {
            return ResponseEntity.badRequest().body("Comment ID must not be null.");
        }

        if (replyDTO == null || replyDTO.getUserId() == null || replyDTO.getContent() == null) {
            return ResponseEntity.badRequest().body("User ID and content must not be null.");
        }

        try {
            // 서비스 호출
            replyService.saveReply(commentId, replyDTO.getUserId(), replyDTO.getContent());
            return ResponseEntity.ok("Reply has been successfully saved.");
        } catch (IllegalArgumentException e) {
            // 잘못된 데이터로 인한 에러 처리
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // 기타 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while saving the reply.");
        }
    }

    // 특정 댓글의 답글 조회
    @GetMapping("/{commentId}/reply")
    public ResponseEntity<?> getRepliesByComment(@PathVariable Long commentId) {
        try {
            return ResponseEntity.ok(replyService.getRepliesByCommentId(commentId));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("답글 조회 중 오류가 발생했습니다.");
        }
    }
}
