package com.BackEndTeam1.controller;

import com.BackEndTeam1.dto.FavoriteBoardDTO;
import com.BackEndTeam1.entity.Board;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.repository.BoardRepository;
import com.BackEndTeam1.repository.FavoriteBoardRepository;
import com.BackEndTeam1.repository.UserRepository;
import com.BackEndTeam1.service.BoardService;
import com.BackEndTeam1.service.FavoriteBoardService;
import com.BackEndTeam1.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/favorite")
public class FavoriteBoardController {

    private final FavoriteBoardRepository favoriteRepository;
    private final UserService userService; // 현재 로그인한 유저를 가져오기 위한 서비스
    private final FavoriteBoardService favoriteBoardService;
    private final BoardService boardService;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final FavoriteBoardRepository favoriteBoardRepository;

    @GetMapping("/getFavoriteBoards")
    public List<FavoriteBoardDTO> getFavoriteBoards(@RequestParam String userId) {
        System.out.println("Fetching favorite boards for user: " + userId);
        List<FavoriteBoardDTO> favoriteBoards = favoriteBoardService.getFavoriteBoards(userId);
        favoriteBoards.forEach(fb -> System.out.println(fb));
        return favoriteBoards;
    }

    @PostMapping("/favoriteboard")
    public ResponseEntity<?> toggleFavorite(@RequestBody FavoriteBoardDTO requestDto) {
        // DTO에서 ID를 가져옴
        Long boardId = requestDto.getBoardId();
        String userId = requestDto.getUserId(); // userId는 String으로 처리
        boolean isFavorite = requestDto.isFavorite();
        System.out.println("Received userId: " + userId);  // 유저 아이디 확인

        Optional<User> userOptional = userRepository.findByUserId(userId);
        if (userOptional.isPresent()) {
            System.out.println("User found: " + userOptional.get().getUserId());
        } else {
            System.out.println("User not found for ID: " + userId);
        }

        // 필요한 경우 서비스 계층에서 엔티티 조회
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + boardId));

        // userId를 String으로 받아서 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));


        // 좋아요 상태를 변경
        favoriteBoardService.toggleFavorite(board, isFavorite, user);

        return ResponseEntity.ok().build();
    }

}
