package com.BackEndTeam1.service;

import com.BackEndTeam1.dto.BoardArticleDTO;
import com.BackEndTeam1.entity.BoardArticle;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.repository.BoardArticleRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class BoardArticleService {

    private final ModelMapper modelMapper;
    private final BoardArticleRepository boardArticleRepository;

    public int save(BoardArticleDTO boardArticleDTO) {

        BoardArticle boardArticle = modelMapper.map(boardArticleDTO, BoardArticle.class);

        boardArticle.setCreated_At(LocalDateTime.now());
        boardArticle.setUpdated_At(LocalDateTime.now());


        BoardArticle savedBoardArticle = boardArticleRepository.save(boardArticle);


        return Math.toIntExact(savedBoardArticle.getId());

    }

    public List<BoardArticleDTO> getAllBoardArticles() {
        return boardArticleRepository.findAll().stream()
                .map(article -> new BoardArticleDTO(
                        article.getId(),
                        article.getTitle(),
                        article.getContent(),
                        article.getBoard() != null ? article.getBoard().getBoardName() : "Unknown", // 게시판 이름
                        article.getCreated_At() != null ? article.getCreated_At().toString() : "Unknown", // 작성일
                        article.getUpdated_At() != null ? article.getUpdated_At().toString() : "Unknown", // 수정일
                        article.getAuthor() != null ? article.getAuthor().getUsername() : "Unknown" // 작성자 이름
                ))
                .collect(Collectors.toList());
    }
}
