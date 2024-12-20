package com.BackEndTeam1.service;

import com.BackEndTeam1.entity.BoardArticle;
import com.BackEndTeam1.repository.BoardArticleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Service
public class TrashCleanupService {

    private final BoardArticleRepository boardArticleRepository;

    @Scheduled(cron = "0 * * * * ?") // 매일 새벽 3시에 실행
    @Transactional
    public void cleanOldTrashArticles() {
        LocalDateTime thresholdDate = LocalDateTime.now().minusMinutes(1);
        List<BoardArticle> articlesToDelete = boardArticleRepository.findByStatusAndTrashDateBefore("trash", thresholdDate);

        if (!articlesToDelete.isEmpty()) {
            boardArticleRepository.deleteAll(articlesToDelete);
            log.info("{}개의 오래된 휴지통 게시글이 삭제되었습니다.", articlesToDelete.size());
        }
    }
}
