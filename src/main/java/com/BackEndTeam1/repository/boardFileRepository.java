package com.BackEndTeam1.repository;

import com.BackEndTeam1.entity.BoardFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface boardFileRepository extends JpaRepository<BoardFile, Integer> {

    List<BoardFile> findByBoardArticleId(Long articleId);

    Optional<BoardFile> findByFileOriginalName(String fileOriginalName);

    Optional<BoardFile> findByFileStoredName(String fileStoredName);
}
