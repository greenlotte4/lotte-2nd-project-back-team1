package com.BackEndTeam1.repository;

import com.BackEndTeam1.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    Optional<Board> findByboardName(String name);

    Optional<Object> findByBoardId(Integer boardId);

}
