package com.BackEndTeam1.repository;

import com.BackEndTeam1.entity.FavoriteBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteBoardRepository extends JpaRepository<FavoriteBoard, Integer> {

    List<FavoriteBoard> findByUser_UserId(String userId);
    boolean existsByUser_UserIdAndBoard_BoardId(String userId, Long boardId);
    void deleteByUser_UserIdAndBoard_BoardId(String userId, Long boardId); // 특정 즐겨찾기 삭제
}
