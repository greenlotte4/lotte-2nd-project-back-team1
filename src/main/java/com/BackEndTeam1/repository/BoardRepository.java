package com.BackEndTeam1.repository;

import com.BackEndTeam1.entity.Board;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    Optional<Board> findByboardName(String name);

    Optional<Object> findByBoardId(Integer boardId);

    @Query("SELECT b FROM Board b WHERE b.boardName = :name")
    Board findByName(@Param("name") String name);

}
