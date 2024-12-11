package com.BackEndTeam1.controller;

import com.BackEndTeam1.dto.BoardArticleDTO;
import com.BackEndTeam1.dto.MoveArticlesDTO;
import com.BackEndTeam1.entity.BoardArticle;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.repository.BoardArticleRepository;
import com.BackEndTeam1.repository.UserRepository;
import com.BackEndTeam1.service.BoardArticleService;
import com.BackEndTeam1.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping("write")
    public ResponseEntity<?> write(@RequestBody BoardArticleDTO boardArticleDTO) {
        try {
            log.info(boardArticleDTO.toString());

            // 저장 로직 처리
            int id = boardArticleService.save(boardArticleDTO);

            // 응답으로 게시글 ID 반환
            return ResponseEntity.ok(id);
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
        Optional<BoardArticle> articleOptional = boardArticleRepository.findById(Math.toIntExact(id));
        if (!articleOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다.");
        }

        BoardArticle article = articleOptional.get();

        // DTO로 매핑
        BoardArticleDTO articleDTO = modelMapper.map(article, BoardArticleDTO.class);

        // 작성자 정보 설정
        if (article.getAuthor() != null) {
            articleDTO.setUserName(article.getAuthor().getUsername()); // 작성자 이름 설정
            articleDTO.setUserId(article.getAuthor().getUserId()); // 작성자 ID 설정
        } else {
            articleDTO.setUserName("알 수 없음");
            articleDTO.setUserId(null);
        }

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
    public ResponseEntity<List<BoardArticleDTO>> getTrashArticles() {
        try {
            List<BoardArticle> trashArticles = boardArticleRepository.findByStatus("trash");

            // 엔티티를 DTO로 변환
            List<BoardArticleDTO> trashDTOs = trashArticles.stream()
                    .map(article -> new BoardArticleDTO(
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
                            article.getStatus()
                    ))
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
}
