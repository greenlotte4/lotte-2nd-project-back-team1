package com.BackEndTeam1.service;

import com.BackEndTeam1.dto.CommentDTO;
import com.BackEndTeam1.entity.BoardArticle;
import com.BackEndTeam1.entity.Comment;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.repository.BoardArticleRepository;
import com.BackEndTeam1.repository.CommentRepository;
import com.BackEndTeam1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardArticleRepository boardArticleRepository;
    private final UserRepository userRepository;

    public void saveComment(CommentDTO requestDTO) {
        BoardArticle article = boardArticleRepository.findById(Math.toIntExact(requestDTO.getBoardArticleId()))
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        Comment comment = Comment.builder()
                .boardArticle(article)
                .createdUser(user)
                .content(requestDTO.getContent())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        commentRepository.save(comment);
    }

    public List<CommentDTO> getCommentsByArticleId(Long articleId) {
        return commentRepository.findByBoardArticle_Id(articleId)
                .stream()
                .map(comment -> new CommentDTO(
                        comment.getCommentId(),
                        comment.getCreatedUser().getUsername(),
                        comment.getContent(),
                        comment.getCreatedAt(),
                        Math.toIntExact(comment.getBoardArticle().getId())
                ))
                .collect(Collectors.toList());
    }
}
