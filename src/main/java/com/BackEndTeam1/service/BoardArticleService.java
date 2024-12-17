package com.BackEndTeam1.service;

import com.BackEndTeam1.dto.BoardArticleDTO;
import com.BackEndTeam1.entity.*;
import com.BackEndTeam1.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
public class BoardArticleService {

    private final ModelMapper modelMapper;
    private final BoardArticleRepository boardArticleRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final ImportantArticleRepository importantArticleRepository;
    private final com.BackEndTeam1.repository.boardFileRepository boardFileRepository;

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

        // 필독 여부 설정
        boardArticle.setMustRead(boardArticleDTO.getMustRead() != null ? boardArticleDTO.getMustRead() : false);
        boardArticle.setNotification(boardArticleDTO.getNotification() != null ? boardArticleDTO.getNotification() : false);

        // 저장
        BoardArticle savedBoardArticle = boardArticleRepository.save(boardArticle);

        return Math.toIntExact(savedBoardArticle.getId());
    }

    public void saveFiles(int articleId, List<MultipartFile> files) {

        // 저장 경로 설정: upload/board
        Path uploadDir = Paths.get("upload", "board");

        try {
            // 경로가 존재하지 않으면 생성
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir); // 폴더 생성
                log.info("파일 저장 경로 생성: " + uploadDir.toAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 경로를 생성하는 중 오류가 발생했습니다.", e);
        }

        // `articleId`로 `Post` 객체 조회
        BoardArticle boardArticle = boardArticleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 게시글을 찾을 수 없습니다. ID: " + articleId));

        // 파일 저장 반복
        for (MultipartFile file : files) {
            try {
                // 저장할 파일명 생성 (랜덤 UUID + 원본 파일명)
                String storedFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

                // 파일 저장 경로: upload/board/{storedFileName}
                Path filePath = uploadDir.resolve(storedFileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // 파일 메타데이터 저장
                BoardFile boardFile = BoardFile.builder()
                        .boardArticle(boardArticle) // `BoardArticle` 객체 설정
                        .fileOriginalName(file.getOriginalFilename()) // 원본 파일명
                        .fileStoredName(storedFileName) // 저장된 파일명
                        .fileSize((int) file.getSize()) // 파일 크기
                        .fileType(file.getContentType()) // 파일 타입
                        .createdAt(new Timestamp(System.currentTimeMillis()))
                        .build();

                // 데이터베이스에 파일 메타데이터 저장
                boardFileRepository.save(boardFile);

                log.info("파일 저장 완료: " + filePath.toAbsolutePath());
            } catch (IOException e) {
                throw new RuntimeException("파일 저장 중 오류 발생", e);
            }
        }
    }

    public List<BoardArticleDTO> getAllBoardArticles() {
        return boardArticleRepository.findAll().stream()
                .map(article -> {
                    // 중요 여부 조회
                    Boolean isImportant = importantArticleRepository
                            .findByUser_UserIdAndArticleId(article.getAuthor() != null ? article.getAuthor().getUserId() : null, article.getId())
                            .map(ImportantArticle::getIsImportant)
                            .orElse(false); // 기본값 false

                    Boolean mustRead = article.getMustRead() != null ? article.getMustRead() : false;
                    Boolean notification = article.getNotification() !=null ? article.getNotification() : false;

                    // DTO 생성
                    return new BoardArticleDTO(
                            article.getId(),
                            article.getTitle(),
                            article.getContent(),
                            article.getBoard() != null ? article.getBoard().getBoardName() : "Unknown", // 게시판 이름
                            article.getCreatedAt() != null ? article.getCreatedAt().toString() : "Unknown", // 작성일
                            article.getUpdatedAt() != null ? article.getUpdatedAt().toString() : "Unknown", // 수정일
                            article.getAuthor() != null ? article.getAuthor().getUsername() : "Unknown", // 작성자 이름
                            article.getAuthor() != null ? article.getAuthor().getUserId() : "Unknown",
                            String.valueOf(article.getTrashDate() != null ? article.getTrashDate() : "Unknown"),
                            article.getDeletedBy(),
                            article.getStatus(),
                            isImportant, // isImportant 추가
                            mustRead,
                            notification
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteArticles(List<Long> idList) {
        for (Long articleId : idList) {
            // 1. 게시물 ID로 파일 목록 조회
            List<BoardFile> files = boardFileRepository.findByBoardArticleId(articleId);

            // 2. 파일 삭제 처리
            for (BoardFile file : files) {
                try {
                    Path filePath = Paths.get("uploads", file.getFileStoredName());
                    Files.deleteIfExists(filePath); // 파일이 존재하면 삭제
                    boardFileRepository.delete(file); // 파일 메타데이터 삭제
                    log.info("파일 삭제 완료: " + file.getFileStoredName());
                } catch (IOException e) {
                    log.error("파일 삭제 중 오류 발생: " + file.getFileStoredName(), e);
                    throw new RuntimeException("파일 삭제 중 오류 발생", e);
                }
            }

            // 3. 게시물 삭제
            boardArticleRepository.deleteById(Math.toIntExact(articleId));
            log.info("게시물 삭제 완료: " + articleId);
        }
    }

    public List<BoardArticleDTO> getArticlesByBoard(Long boardId) {
        List<BoardArticle> articles = boardArticleRepository.findByBoard_BoardId(boardId);

        // 엔티티 -> DTO 변환
        return articles.stream()
                .map(article -> {
                    // 중요 여부 조회
                    Boolean isImportant = importantArticleRepository
                            .findByUser_UserIdAndArticleId(
                                    article.getAuthor() != null ? article.getAuthor().getUserId() : null,
                                    article.getId()
                            )
                            .map(ImportantArticle::getIsImportant)
                            .orElse(false); // 기본값 false

                    Boolean mustRead = article.getMustRead() != null ? article.getMustRead() : false;
                    Boolean notification = article.getNotification() !=null ? article.getNotification() : false;

                    // DTO 생성
                    return new BoardArticleDTO(
                            article.getId(),
                            article.getTitle(),
                            article.getContent(),
                            article.getBoard() != null ? article.getBoard().getBoardName() : "Unknown",
                            article.getCreatedAt() != null ? article.getCreatedAt().toString() : null,
                            article.getUpdatedAt() != null ? article.getUpdatedAt().toString() : null,
                            article.getAuthor() != null ? article.getAuthor().getUsername() : "Unknown",
                            article.getAuthor() != null ? article.getAuthor().getUserId().toString() : null,
                            article.getTrashDate() != null ? article.getTrashDate().toString() : null,
                            article.getDeletedBy(),
                            article.getStatus(),
                            isImportant, // isImportant 필드 추가
                            mustRead,
                            notification
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void moveBoardArticlesToBoard(List<Long> articleIds, Long boardId) {
        for (Long articleId : articleIds) {
            BoardArticle boardArticle = boardArticleRepository.findById(Math.toIntExact(articleId))
                    .orElseThrow(() -> new IllegalArgumentException("Invalid article ID: " + articleId));

            // 게시판 설정
            Board board = boardRepository.findById(boardId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid board ID: " + boardId));

            // 필드 값 변경
            boardArticle.setBoard(board); // 새로운 게시판으로 설정
            boardArticle.setStatus("active"); // 상태를 'active'로 변경
            boardArticle.setUpdatedAt(LocalDateTime.now()); // 수정 시간 업데이트
            boardArticle.setTrashDate(null); // 휴지통 날짜 제거
            boardArticle.setDeletedBy(null); // 삭제자 정보 제거

            boardArticleRepository.save(boardArticle); // 저장
        }
    }

    public List<BoardArticleDTO> getArticlesByUser(String userId) {
        List<BoardArticle> articles = boardArticleRepository.findByAuthor_UserIdAndStatus(userId, "active");
        return articles.stream()
                .map(article -> BoardArticleDTO.builder()
                        .id(Math.toIntExact(article.getId()))
                        .title(article.getTitle())
                        .content(article.getContent())
                        .boardName(article.getBoard().getBoardName())
                        .createdAt(article.getCreatedAt().toString())
                        .updatedAt(article.getUpdatedAt().toString())
                        .userName(article.getAuthor().getUsername())
                        .userId(article.getAuthor().getUserId())
                        .trashDate(article.getTrashDate() != null ? article.getTrashDate().toString() : null)
                        .deletedBy(article.getDeletedBy() != null ? article.getDeletedBy().getUsername() : "Unknown")
                        .status(article.getStatus())
                        .build()
                ).toList();
    }

    public boolean isArticleImportant(String userId, Long articleId) {
        // `ImportantArticle`에서 사용자와 게시글 ID로 중요 여부 조회
        return importantArticleRepository
                .findByUser_UserIdAndArticleId(userId, articleId)
                .map(ImportantArticle::getIsImportant)
                .orElse(false); // 중요하지 않으면 기본값 false 반환
    }


}
