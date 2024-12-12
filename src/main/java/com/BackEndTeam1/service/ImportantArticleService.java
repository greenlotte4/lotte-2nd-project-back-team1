package com.BackEndTeam1.service;

import com.BackEndTeam1.entity.BoardArticle;
import com.BackEndTeam1.entity.ImportantArticle;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.repository.ImportantArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@Service
public class ImportantArticleService {

    private final ImportantArticleRepository importantArticleRepository;

    public void toggleImportantArticle(String userId, Long articleId) {
        Optional<ImportantArticle> existingRecord = importantArticleRepository.findByUser_UserIdAndArticleId(userId, articleId);

        if (existingRecord.isPresent()) {
            ImportantArticle importantArticle = existingRecord.get();
            importantArticle.setIsImportant(!importantArticle.getIsImportant()); // 중요 여부 토글
            importantArticleRepository.save(importantArticle);
        } else {
            ImportantArticle newImportantArticle = ImportantArticle.builder()
                    .user(new User(userId)) // 유저 객체 생성 또는 설정
                    .article(new BoardArticle(articleId)) // 게시글 객체 생성 또는 설정
                    .isImportant(true) // 새로 생성 시 기본값: true
                    .build();
            importantArticleRepository.save(newImportantArticle);
        }
    }
}
