package com.BackEndTeam1.service;

import com.BackEndTeam1.dto.BoardArticleDTO;
import com.BackEndTeam1.entity.BoardArticle;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.repository.BoardArticleRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


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
}
