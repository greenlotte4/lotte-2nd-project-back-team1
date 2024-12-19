package com.BackEndTeam1.repository;


import com.BackEndTeam1.entity.Comment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findByBoardArticle_Id(Long articleId);


    @Transactional
    void deleteByBoardArticle_Id(Long id);

}
