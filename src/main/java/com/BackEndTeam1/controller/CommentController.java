package com.BackEndTeam1.controller;

import com.BackEndTeam1.dto.CommentDTO;
import com.BackEndTeam1.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/add")
    public ResponseEntity<?> addComment(@RequestBody CommentDTO commentDTO) {
        // ID 확인
        if (commentDTO.getArticleId() == null || commentDTO.getUserId() == null) {
            return ResponseEntity.badRequest().body("Article ID or User ID must not be null");
        }
        // 댓글 저장 로직

        commentService.saveComment(commentDTO);
        return ResponseEntity.ok("댓글이 성공적으로 저장되었습니다.");
    }

    @GetMapping("/{articleId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByArticle(@PathVariable Long articleId) {
        List<CommentDTO> comments = commentService.getCommentsByArticleId(articleId);
        return ResponseEntity.ok(comments);
    }
}
