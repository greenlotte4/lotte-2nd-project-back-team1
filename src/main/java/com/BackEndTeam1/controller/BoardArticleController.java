package com.BackEndTeam1.controller;

import com.BackEndTeam1.dto.BoardArticleDTO;
import com.BackEndTeam1.entity.BoardArticle;
import com.BackEndTeam1.repository.BoardArticleRepository;
import com.BackEndTeam1.service.BoardArticleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/article")
public class BoardArticleController {

    private final BoardArticleService boardArticleService;
    private final ModelMapper modelMapper;
    private final BoardArticleRepository boardArticleRepository;

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

}
