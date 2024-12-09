package com.BackEndTeam1.service;

import com.BackEndTeam1.dto.BoardArticleDTO;
import com.BackEndTeam1.entity.Board;
import com.BackEndTeam1.entity.BoardArticle;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.repository.BoardArticleRepository;
import com.BackEndTeam1.repository.BoardRepository;
import com.BackEndTeam1.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
public class BoardArticleService {

    private final ModelMapper modelMapper;
    private final BoardArticleRepository boardArticleRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    public int save(BoardArticleDTO boardArticleDTO) {
        log.info("Saving BoardArticle: {}", boardArticleDTO);

        // 사용자 검증
        if (boardArticleDTO.getUserId() == null) {
            throw new IllegalArgumentException("userId가 null입니다.");
        }

        User user = userRepository.findByUserId(String.valueOf(boardArticleDTO.getUserId()))
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 사용자가 존재하지 않습니다."));

        // 게시판 검증 및 검색
        if (boardArticleDTO.getBoardName() == null) {
            throw new IllegalArgumentException("boardName이 null입니다.");
        }

        Board board = boardRepository.findByboardName(boardArticleDTO.getBoardName())
                .orElseThrow(() -> new IllegalArgumentException("해당 이름의 게시판이 존재하지 않습니다."));

        // BoardArticle 엔티티 생성
        BoardArticle boardArticle = modelMapper.map(boardArticleDTO, BoardArticle.class);
        boardArticle.setAuthor(user); // 작성자 설정
        boardArticle.setBoard(board); // 게시판 설정
        boardArticle.setCreatedAt(LocalDateTime.now());
        boardArticle.setUpdatedAt(LocalDateTime.now());

        // 저장
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
                        article.getCreatedAt() != null ? article.getCreatedAt().toString() : "Unknown", // 작성일
                        article.getUpdatedAt() != null ? article.getUpdatedAt().toString() : "Unknown", // 수정일
                        article.getAuthor() != null ? article.getAuthor().getUsername() : "Unknown",// 작성자 이름
                        article.getAuthor() != null ? article.getAuthor().getUserId() : "Unknown",
                        String.valueOf(article.getTrashDate() != null ? article.getTrashDate() : "Unknown"),
                        article.getDeletedBy()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteArticles(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("삭제할 게시글 ID가 없습니다.");
        }
        // 휴지통에 있는 해당 ID들의 게시글 삭제
        boardArticleRepository.deleteAllByIdIn(ids);
    }

    public List<BoardArticleDTO> getArticlesByBoard(Long boardId) {
        List<BoardArticle> articles = boardArticleRepository.findByBoard_BoardId(boardId);

        // 엔티티 -> DTO 변환
        return articles.stream()
                .map(article -> new BoardArticleDTO(
                        article.getId(),
                        article.getTitle(),
                        article.getContent(),
                        article.getBoard().getBoardName(),
                        article.getCreatedAt() != null ? article.getCreatedAt().toString() : null,
                        article.getUpdatedAt() != null ? article.getUpdatedAt().toString() : null,
                        article.getAuthor() != null ? article.getAuthor().getUsername() : "Unknown",
                        article.getAuthor() != null ? article.getAuthor().getUserId().toString() : null,
                        article.getTrashDate() != null ? article.getTrashDate().toString() : null,
                        article.getDeletedBy()
                ))
                .collect(Collectors.toList());
    }


}
