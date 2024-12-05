package com.BackEndTeam1.repository;

import com.BackEndTeam1.entity.Board;
import com.BackEndTeam1.entity.FavoriteBoard;
import com.BackEndTeam1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteBoardRepository extends JpaRepository<FavoriteBoard, Integer> {

    Optional<FavoriteBoard> findByUserAndBoard(User user, Board board);
    List<FavoriteBoard> findByUser_UserId(String userId);

}
