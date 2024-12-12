package com.BackEndTeam1.controller;

import com.BackEndTeam1.service.ImportantArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/important-articles")
@RequiredArgsConstructor
public class ImportantArticleController {

    private final ImportantArticleService importantArticleService;

    @PostMapping("/{articleId}")
    public ResponseEntity<String> toggleImportantArticle(
            @RequestParam String userId,
            @PathVariable Long articleId) {
        try {
            importantArticleService.toggleImportantArticle(userId, articleId);
            return ResponseEntity.ok("중요 여부가 업데이트되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("중요 여부 업데이트 중 오류 발생: " + e.getMessage());
        }
    }
}
