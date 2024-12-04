package com.BackEndTeam1.controller;

import com.BackEndTeam1.dto.FavoriteBoardDTO;
import com.BackEndTeam1.entity.Board;
import com.BackEndTeam1.entity.FavoriteBoard;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.repository.FavoriteBoardRepository;
import com.BackEndTeam1.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/favorite")
public class FavoriteBoardController {

    private final FavoriteBoardRepository favoriteRepository;
    private final UserService userService; // 현재 로그인한 유저를 가져오기 위한 서비스

    @PutMapping("/board/{boardId}")
    public ResponseEntity<Void> updateFavorite(@PathVariable Long boardId, @RequestBody FavoriteBoardDTO request) {
        User currentUser = userService.getLoggedInUser(); // 현재 로그인한 유저 정보 가져오기

        if (request.isFavorite()) {
            // 즐겨찾기 추가
            if (!favoriteRepository.existsByUser_UserIdAndBoard_BoardId(String.valueOf(Long.valueOf(currentUser.getUserId())), boardId)) {
                FavoriteBoard favoriteBoard = new FavoriteBoard();
                favoriteBoard.setUser(currentUser);
                favoriteBoard.setBoard(new Board()); // boardId로 Board 엔티티 생성
                favoriteRepository.save(favoriteBoard);
            }
        } else {
            // 즐겨찾기 삭제
            favoriteRepository.deleteByUser_UserIdAndBoard_BoardId(String.valueOf(Long.valueOf(currentUser.getUserId())), boardId);
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/user")
    public ResponseEntity<List<Board>> getUserFavorites() {
        User currentUser = userService.getLoggedInUser(); // 현재 로그인한 유저 정보
        List<FavoriteBoard> favorites = favoriteRepository.findByUser_UserId(String.valueOf(Long.valueOf(currentUser.getUserId())));
        List<Board> favoriteBoards = favorites.stream()
                .map(FavoriteBoard::getBoard)
                .collect(Collectors.toList());
        return ResponseEntity.ok(favoriteBoards);
    }
}
