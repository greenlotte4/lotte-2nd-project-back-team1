package com.BackEndTeam1.service;

import com.BackEndTeam1.dto.BoardArticleDTO;
import com.BackEndTeam1.dto.BoardDTO;
import com.BackEndTeam1.dto.PageRequestDTO;
import com.BackEndTeam1.dto.PageResponseDTO;
import com.BackEndTeam1.entity.*;
import com.BackEndTeam1.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BoardArticleRepository boardArticleRepository;
    private final com.BackEndTeam1.repository.boardFileRepository boardFileRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;

    public List<BoardDTO> getAllBoards() {
        return boardRepository.findAll().stream()
                .map(board -> BoardDTO.builder()
                        .board_id(board.getBoardId())
                        .board_name(board.getBoardName())
                        .max_collaborators(board.getMaxCollaborators() != null ? board.getMaxCollaborators() : 3)
                        .created_at(board.getCreatedAt() != null ? board.getCreatedAt().toString() : null)
                        .updated_at(board.getUpdatedAt() != null ? board.getUpdatedAt().toString() : null)
                        .user_id(board.getUser() != null ? board.getUser().getUserId() : null)
                        .build())
                .collect(Collectors.toList());
    }

    public String getBoardNameById(Long boardId) {
        return boardRepository.findById(boardId)
                .map(Board::getBoardName)
                .orElse("Unknown Board");
    }

    public BoardDTO createBoard(BoardDTO boardDTO, String userId) {
        log.info("생성한 아이디는 : {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        if (!"ADMIN".equals(user.getRole())) {
            throw new AccessDeniedException("관리자 권한이 아닙니다.");
        }

        Board board = new Board();
        board.setBoardName(boardDTO.getBoard_name());
        board.setMaxCollaborators(boardDTO.getMax_collaborators());
        board.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        board.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        board.setUser(user);

        board = boardRepository.save(board);

        return modelMapper.map(board, BoardDTO.class);
    }

    public BoardDTO updateBoard(BoardDTO boardDTO, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        if (!"ADMIN".equals(user.getRole())) {
            throw new AccessDeniedException("관리자 권한이 아닙니다.");
        }
        Board board = new Board();
        board.setBoardId(boardDTO.getBoard_id());
        board.setBoardName(boardDTO.getBoard_name());
        board.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        board.setUser(user);

        board = boardRepository.save(board);

        return modelMapper.map(board, BoardDTO.class);
    }

    @Transactional
    public void deleteBoard(Long boardId, String userId) {
        // 1. 사용자 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // 2. 관리자 권한 확인
        if (!"ADMIN".equals(user.getRole())) {
            throw new AccessDeniedException("관리자 권한이 아닙니다.");
        }

        // 3. 게시판 존재 여부 확인
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found with ID: " + boardId));

        // 4. 게시판에 속한 글(BoardArticle) 삭제
        List<BoardArticle> articles = boardArticleRepository.findByBoard_BoardId(boardId);
        for (BoardArticle article : articles) {
            // 4-1. 글에 속한 파일 삭제
            List<BoardFile> files = boardFileRepository.findByBoardArticleId(article.getId());
            for (BoardFile file : files) {
                try {
                    Path filePath = Paths.get("uploads", file.getFileStoredName());
                    Files.deleteIfExists(filePath);
                    boardFileRepository.delete(file);
                } catch (Exception e) {
                    throw new RuntimeException("파일 삭제 실패", e);
                }
            }

            // 4-2. 글에 속한 댓글 삭제
            List<Comment> comments = commentRepository.findByBoardArticle_Id(article.getId());
            for (Comment comment : comments) {
                // 댓글에 연결된 답글 삭제
                replyRepository.deleteByComment_CommentId(comment.getCommentId());
                // 댓글 삭제
                commentRepository.delete(comment);
            }

            // 4-3. 글(BoardArticle) 삭제
            boardArticleRepository.deleteById(Math.toIntExact(article.getId()));
        }

        // 5. 게시판 삭제
        boardRepository.deleteById(boardId);
    }




}
