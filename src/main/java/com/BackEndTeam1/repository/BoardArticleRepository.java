package com.BackEndTeam1.repository;

import com.BackEndTeam1.entity.BoardArticle;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BoardArticleRepository extends JpaRepository<BoardArticle, Integer> {

    List<BoardArticle> findByStatus(String status);

    @Query("SELECT b FROM BoardArticle b WHERE b.status = 'trash' AND b.trashDate <= :cutoffDate")
    List<BoardArticle> findOldTrashArticles(@Param("cutoffDate") LocalDateTime cutoffDate);


    @Query("SELECT b FROM BoardArticle b WHERE b.id IN :ids AND b.status = :status")
    List<BoardArticle> findAllByIdAndStatus(@Param("ids") List<Long> ids, @Param("status") String status);

    void deleteAllByIdIn(List<Long> ids);

    List<BoardArticle> findByStatusAndTrashDateBefore(String status, LocalDateTime dateTime);

}
