package com.BackEndTeam1.controller;

import com.BackEndTeam1.dto.BoardArticleDTO;
import com.BackEndTeam1.dto.BoardFileDTO;
import com.BackEndTeam1.dto.MoveArticlesDTO;
import com.BackEndTeam1.entity.BoardArticle;
import com.BackEndTeam1.entity.BoardFile;
import com.BackEndTeam1.entity.ImportantArticle;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.repository.BoardArticleRepository;
import com.BackEndTeam1.repository.ImportantArticleRepository;
import com.BackEndTeam1.repository.UserRepository;
import com.BackEndTeam1.service.BoardArticleService;
import com.BackEndTeam1.service.BoardService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/article")
public class BoardArticleController {

    private final BoardArticleService boardArticleService;
    private final ModelMapper modelMapper;
    private final BoardArticleRepository boardArticleRepository;
    private final UserRepository userRepository;
    private final BoardService boardService;
    private final ImportantArticleRepository importantArticleRepository;
    private final com.BackEndTeam1.repository.boardFileRepository boardFileRepository;

    @Value("${file.upload.path}") // yml 설정값 가져오기
    private String uploadBasePath;

    @PostMapping("/write")
    public ResponseEntity<?> write(
            @RequestPart("boardArticleDTO") BoardArticleDTO boardArticleDTO,
            @RequestPart("files") List<MultipartFile> files) {
        try {
            log.info("Received BoardArticleDTO: " + boardArticleDTO);
            log.info("Received files: " + files.size() + " files");

            // 게시글 저장
            int articleId = boardArticleService.save(boardArticleDTO);

            // 파일 저장
            boardArticleService.saveFiles(articleId, files);

            // 응답으로 게시글 ID 반환
            return ResponseEntity.ok(articleId);
        } catch (Exception e) {
            log.error("게시글 등록 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 등록에 실패했습니다.");
        }
    }

    @GetMapping("/view")
    public List<BoardArticle> getAllBoardArticles() {
        return boardArticleRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @GetMapping("/view/detail")
    public ResponseEntity<BoardArticleDTO> getArticle(@RequestParam Long id) {
        // 게시글 조회
        BoardArticle article = boardArticleRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));

        // DTO로 매핑
        BoardArticleDTO articleDTO = new BoardArticleDTO();

        articleDTO.setId(Math.toIntExact(article.getId()));
        articleDTO.setTitle(article.getTitle());
        articleDTO.setContent(article.getContent());
        articleDTO.setCreatedAt(String.valueOf(article.getCreatedAt()));
        articleDTO.setUpdatedAt(String.valueOf(article.getUpdatedAt()));

        // 작성자 정보 설정
        if (article.getAuthor() != null) {
            articleDTO.setUserName(article.getAuthor().getUsername());
            articleDTO.setUserId(article.getAuthor().getUserId());
        } else {
            articleDTO.setUserName("알 수 없음");
            articleDTO.setUserId(null);
        }

        // 게시판 정보 설정
        if (article.getBoard() != null) {
            articleDTO.setBoardName(article.getBoard().getBoardName());
        } else {
            articleDTO.setBoardName("알 수 없음");
        }

        // 중요 게시글 설정
        if (article.getAuthor() != null) {
            Optional<ImportantArticle> importantArticle = importantArticleRepository.findByUser_UserIdAndArticleId(
                    article.getAuthor().getUserId(),
                    article.getId()
            );
            articleDTO.setIsImportant(importantArticle.map(ImportantArticle::getIsImportant).orElse(false));
        } else {
            articleDTO.setIsImportant(false);
        }

        // 파일 정보 추가
        List<BoardFileDTO> fileDTOs = article.getFiles() != null ? article.getFiles().stream().map(file -> {
            BoardFileDTO boardFileDTO = new BoardFileDTO();
            boardFileDTO.setBoardFileId(file.getBoardFileId());
            boardFileDTO.setFileOriginalName(file.getFileOriginalName());
            boardFileDTO.setFileStoredName(file.getFileStoredName());
            boardFileDTO.setFileSize(file.getFileSize());
            boardFileDTO.setFileType(file.getFileType());
            boardFileDTO.setCreatedAt(String.valueOf(file.getCreatedAt()));
            boardFileDTO.setUpdatedAt(String.valueOf(file.getUpdatedAt()));
            return boardFileDTO;
        }).collect(Collectors.toList()) : Collections.emptyList();
        articleDTO.setFiles(fileDTOs);

        return ResponseEntity.ok(articleDTO);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteArticle(
            @RequestParam Long id,
            @RequestParam String userId) { // userId 매개변수 추가
        try {
            Optional<BoardArticle> articleOptional = boardArticleRepository.findById(Math.toIntExact(id));
            if (!articleOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글을 찾을 수 없습니다.");
            }

            BoardArticle article = articleOptional.get();

            // userId 검증 (예: article의 작성자와 userId가 일치하는지 확인)
            if (!article.getAuthor().getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 사용자입니다.");
            }
            Optional<User> userOptional = userRepository.findByUserId(userId);
            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 사용자입니다.");
            }
            User user = userOptional.get();
            article.setDeletedBy(user);

            article.setStatus("trash");
            article.setTrashDate(LocalDateTime.now());
            boardArticleRepository.save(article);

            return ResponseEntity.ok("게시글이 휴지통으로 이동되었습니다.");
        } catch (Exception e) {
            log.error("게시글 삭제 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 삭제에 실패했습니다.");
        }
    }

    @GetMapping("/trash")
    public ResponseEntity<List<BoardArticleDTO>> getTrashArticles(@RequestParam String userId) {
        try {
            List<BoardArticle> trashArticles = boardArticleRepository.findByStatusAndDeletedBy("trash", userId);

            // 엔티티를 DTO로 변환
            List<BoardArticleDTO> trashDTOs = trashArticles.stream()
                    .map(article -> {
                        // 중요 여부 조회
                        Boolean isImportant = importantArticleRepository
                                .findByUser_UserIdAndArticleId(userId, article.getId())
                                .map(ImportantArticle::getIsImportant)
                                .orElse(false); // 기본값 false

                        // DTO 생성
                        return new BoardArticleDTO(
                                article.getId(),
                                article.getTitle(),
                                article.getContent(),
                                article.getBoard() != null ? article.getBoard().getBoardName() : "Unknown",
                                article.getCreatedAt() != null ? article.getCreatedAt().toString() : "Unknown",
                                article.getUpdatedAt() != null ? article.getUpdatedAt().toString() : "Unknown",
                                article.getAuthor() != null ? article.getAuthor().getUsername() : "Unknown",
                                article.getAuthor() != null ? article.getAuthor().getUserId() : "Unknown",
                                article.getTrashDate() != null ? article.getTrashDate().toString() : "Unknown",
                                article.getDeletedBy(),
                                article.getStatus(),
                                isImportant // isImportant 필드 추가
                        );
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(trashDTOs);
        } catch (Exception e) {
            log.error("휴지통 조회 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/trash/permanent")
    public ResponseEntity<?> deleteTrashArticles(@RequestParam("ids") String ids) {
        System.out.println("Received ids: " + ids); // 요청 로그 확인

        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().body("삭제할 게시글 ID가 없습니다.");
        }

        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        try {
            boardArticleService.deleteArticles(idList);
            return ResponseEntity.ok("선택한 게시글이 영구 삭제되었습니다.");
        } catch (Exception e) {
            e.printStackTrace(); // 에러 로그 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 삭제에 실패했습니다.");
        }
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> updateArticle(@PathVariable Long id, @RequestBody BoardArticleDTO updatedArticle) {
        try {
            Optional<BoardArticle> optionalArticle = boardArticleRepository.findById(Math.toIntExact(id));
            if (!optionalArticle.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글을 찾을 수 없습니다.");
            }

            BoardArticle article = optionalArticle.get();
            article.setTitle(updatedArticle.getTitle());
            article.setContent(updatedArticle.getContent());
            article.setUpdatedAt(LocalDateTime.now());

            boardArticleRepository.save(article);

            return ResponseEntity.ok("게시글이 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 수정에 실패했습니다.");
        }
    }

    @GetMapping("/boards/{boardId}/articles")
    public ResponseEntity<Map<String, Object>> getArticlesByBoard(@PathVariable Long boardId) {
        // boardId로 모든 articles 가져오기
        List<BoardArticleDTO> allArticles = boardArticleService.getArticlesByBoard(boardId);

        // status가 'active'인 글만 필터링
        List<BoardArticleDTO> activeArticles = allArticles.stream()
                .filter(article -> "active".equals(article.getStatus()))
                .collect(Collectors.toList());

        // boardId로 boardName 조회
        String boardName = boardService.getBoardNameById(boardId);

        // 응답 데이터 구성
        Map<String, Object> response = new HashMap<>();
        response.put("boardName", boardName);
        response.put("articles", activeArticles);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/articles/move")
    public ResponseEntity<?> moveArticlesToBoard(@RequestBody MoveArticlesDTO moveArticlesDTO) {
        try {
            boardArticleService.moveBoardArticlesToBoard(moveArticlesDTO.getArticleIds(), moveArticlesDTO.getBoardId());
            return ResponseEntity.ok("게시글이 성공적으로 이동되었습니다.");
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 이동 중 오류 발생");
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BoardArticleDTO>> getArticlesByUser(@PathVariable String userId) {
        List<BoardArticleDTO> articles = boardArticleService.getArticlesByUser(userId);

        // `isImportant` 값을 설정
        articles.forEach(article -> {
            boolean isImportant = boardArticleService.isArticleImportant(userId, (long) article.getId());
            article.setIsImportant(isImportant); // DTO에 설정
        });

        return ResponseEntity.ok(articles);
    }

}
