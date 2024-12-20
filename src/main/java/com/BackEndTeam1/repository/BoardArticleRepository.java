package com.BackEndTeam1.repository;

import com.BackEndTeam1.entity.Board;
import com.BackEndTeam1.entity.BoardArticle;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BoardArticleRepository extends JpaRepository<BoardArticle, Integer> {

    List<BoardArticle> findByStatus(String status);

    @Query("SELECT b FROM BoardArticle b WHERE b.status = :status AND b.deletedBy.userId = :userId")
    Page<BoardArticle> findByStatusAndDeletedBy(String status, String userId, Pageable pageable);

    @Query("SELECT b FROM BoardArticle b WHERE b.status = 'trash' AND b.trashDate <= :cutoffDate")
    List<BoardArticle> findOldTrashArticles(@Param("cutoffDate") LocalDateTime cutoffDate);


    @Query("SELECT b FROM BoardArticle b WHERE b.id IN :ids AND b.status = :status")
    List<BoardArticle> findAllByIdAndStatus(@Param("ids") List<Long> ids, @Param("status") String status);

    void deleteAllByIdIn(List<Long> ids);

    List<BoardArticle> findByStatusAndTrashDateBefore(String status, LocalDateTime dateTime);

    List<BoardArticle> findByBoard(Board board);

    List<BoardArticle> findByBoard_BoardId(Long boardId);

    Page<BoardArticle> findByAuthor_UserIdAndStatus(String userId, String status, Pageable pageable);

    Page<BoardArticle> findByBoard_BoardIdAndStatus(Long boardId, String status, Pageable pageable);

    List<BoardArticle> findByMustReadTrue();

    Page<BoardArticle> findByMustReadTrue(Pageable pageable);

    @Query("SELECT b FROM BoardArticle b WHERE b.createdAt >= :startDate AND b.status = 'active' ORDER BY b.createdAt DESC")
    Page<BoardArticle> findRecentArticles(LocalDateTime startDate, Pageable pageable);

    @Query("SELECT b FROM BoardArticle b WHERE b.status = 'active' ORDER BY b.createdAt DESC")
    List<BoardArticle> findTop10ByStatusOrderByCreatedAtDesc(Pageable pageable);

    Page<BoardArticle> findByBoard_BoardIdAndTitleContainingIgnoreCase(Integer boardId, String title, Pageable pageable);

    @Query("SELECT a FROM BoardArticle a WHERE a.board.boardId = :boardId ORDER BY a.createdAt DESC")
    List<BoardArticle> findTop5ByBoardIdOrderByCreatedAtDesc(@Param("boardId") Long boardId);






}
