package com.BackEndTeam1.controller;

import com.BackEndTeam1.dto.*;
import com.BackEndTeam1.entity.BoardArticle;
import com.BackEndTeam1.entity.ImportantArticle;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.repository.BoardArticleRepository;
import com.BackEndTeam1.repository.ImportantArticleRepository;
import com.BackEndTeam1.repository.UserRepository;
import com.BackEndTeam1.service.BoardArticleService;
import com.BackEndTeam1.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        try {
            log.info("Received BoardArticleDTO: " + boardArticleDTO);

            // 파일이 있을 경우 로그 출력
            if (files != null && !files.isEmpty()) {
                log.info("Received files: " + files.size() + " files");
            } else {
                log.info("No files received.");
            }

            // 게시글 저장
            int articleId = boardArticleService.save(boardArticleDTO);

            // 파일이 있을 경우에만 저장
            if (files != null && !files.isEmpty()) {
                boardArticleService.saveFiles(articleId, files);
            }

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
        articleDTO.setMustRead(article.getMustRead());

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
    public ResponseEntity<Page<BoardArticleDTO>> getTrashArticles(
            @RequestParam String userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<BoardArticle> trashArticles = boardArticleRepository.findByStatusAndDeletedBy("trash", userId, pageable);

            // 엔티티를 DTO로 변환
            Page<BoardArticleDTO> trashDTOs = trashArticles.map(article -> {
                Boolean isImportant = importantArticleRepository
                        .findByUser_UserIdAndArticleId(userId, article.getId())
                        .map(ImportantArticle::getIsImportant)
                        .orElse(false); // 기본값 false

                Boolean mustRead = article.getMustRead() != null ? article.getMustRead() : false;
                Boolean notification = article.getNotification() != null ? article.getNotification() : false;

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
                        isImportant, // isImportant 필드 추가
                        mustRead,
                        notification
                );
            });

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
    public ResponseEntity<Map<String, Object>> getArticlesByBoard(
            @PathVariable Long boardId,
            @RequestParam(defaultValue = "0") int page, // 기본값 0 (첫 페이지)
            @RequestParam(defaultValue = "10") int size // 기본값 20개씩
    ) {
        // 페이징된 결과 가져오기
        Page<BoardArticleDTO> articlePage = boardArticleService.getArticlesByBoard(boardId, page, size);

        // boardId로 boardName 조회
        String boardName = boardService.getBoardNameById(boardId)
                .describeConstable().orElseThrow(() -> new IllegalArgumentException("Invalid board ID: " + boardId));


        // 응답 데이터 구성
        Map<String, Object> response = new HashMap<>();
        response.put("boardName", boardName);
        response.put("articles", articlePage.getContent()); // 현재 페이지의 게시글 목록
        response.put("currentPage", articlePage.getNumber()); // 현재 페이지 번호
        response.put("totalPages", articlePage.getTotalPages()); // 전체 페이지 수
        response.put("totalElements", articlePage.getTotalElements()); // 전체 게시글 수

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
    public ResponseEntity<Map<String, Object>> getArticlesByUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page, // 페이지 번호
            @RequestParam(defaultValue = "10") int size // 페이지 크기
    ) {
        Page<BoardArticleDTO> articlesPage = boardArticleService.getArticlesByUser(userId, page, size);

        articlesPage.getContent().forEach(article -> {
            boolean isImportant = boardArticleService.isArticleImportant(userId, (long) article.getId());
            article.setIsImportant(isImportant); // DTO에 설정
        });

        // 응답 데이터 구성
        Map<String, Object> response = new HashMap<>();
        response.put("articles", articlesPage.getContent());
        response.put("currentPage", articlesPage.getNumber());
        response.put("totalPages", articlesPage.getTotalPages());
        response.put("totalElements", articlesPage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/must-read")
    public ResponseEntity<Page<BoardArticleDTO>> getMustReadArticles(
            @RequestParam(defaultValue = "0") int page, // 기본값 0 (첫 번째 페이지)
            @RequestParam(defaultValue = "10") int size // 기본값 10 (한 페이지 당 10개)
    ) {
        Page<BoardArticleDTO> articles = boardArticleService.getMustReadArticles(page, size);
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/must-read/latest")
    public ResponseEntity<List<BoardArticleDTO>> getLatestMustReadArticles() {
        List<BoardArticleDTO> articles = boardArticleService.getLatestMustReadArticles();
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/recent")
    public Page<BoardArticleDTO> getRecentArticles(
            @RequestParam(defaultValue = "0") int page,  // 페이지 번호 (0부터 시작)
            @RequestParam(defaultValue = "10") int size // 한 페이지당 게시글 수
    ) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        // Pageable 객체 생성 (페이지 번호, 크기, 정렬 기준)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        // Repository 호출하여 페이징된 데이터 가져오기
        Page<BoardArticle> articles = boardArticleRepository.findRecentArticles(thirtyDaysAgo, pageable);

        // DTO로 변환
        return articles.map(article -> new BoardArticleDTO(
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
                null,
                article.getMustRead(),
                article.getNotification()
        ));
    }

    @GetMapping("/recent/ten")
    public List<BoardArticleDTO> getTop10RecentArticles() {
        Pageable pageable = PageRequest.of(0, 10); // 첫 번째 페이지, 10개만 가져오기
        List<BoardArticle> articles = boardArticleRepository.findTop10ByStatusOrderByCreatedAtDesc(pageable);

        // DTO로 변환하여 반환
        return articles.stream()
                .map(article -> new BoardArticleDTO(
                        article.getId(),
                        article.getTitle(),
                        article.getContent(),
                        article.getBoard().getBoardName(),
                        article.getCreatedAt().toString(),
                        article.getUpdatedAt().toString(),
                        article.getAuthor().getUsername(),
                        article.getAuthor().getUserId().toString(),
                        article.getTrashDate() != null ? article.getTrashDate().toString() : null,
                        article.getDeletedBy() != null ? article.getDeletedBy() : null,
                        article.getStatus(),
                        null,
                        article.getMustRead(),
                        article.getNotification()
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("/board/{boardId}/search")
    public ResponseEntity<?> searchArticles(
            @PathVariable Integer boardId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<BoardArticleDTO> articles = boardArticleService.searchArticlesByTitle(boardId, keyword, page, size);
            return ResponseEntity.ok(articles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("검색 중 오류가 발생했습니다.");
        }
    }

    @GetMapping("/freerecent")
    public List<BoardArticle> getRecentFreeBoardArticles(
            @RequestParam(defaultValue = "자유게시판") String boardName) {
        return boardArticleService.getRecentArticlesByBoardName(boardName);
    }

}
