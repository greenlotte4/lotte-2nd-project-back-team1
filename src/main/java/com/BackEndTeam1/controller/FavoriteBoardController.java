package com.BackEndTeam1.controller;

import com.BackEndTeam1.dto.FavoriteBoardDTO;
import com.BackEndTeam1.entity.Board;
import com.BackEndTeam1.entity.FavoriteBoard;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.repository.FavoriteBoardRepository;
import com.BackEndTeam1.service.FavoriteBoardService;
import com.BackEndTeam1.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/favorite")
public class FavoriteBoardController {

    private final FavoriteBoardRepository favoriteRepository;
    private final UserService userService; // 현재 로그인한 유저를 가져오기 위한 서비스
    private final FavoriteBoardService favoriteBoardService;

    @PostMapping("/favoriteboard")
    public ResponseEntity<?> toggleFavorite(@RequestBody FavoriteBoardDTO requestDto) {
        Board boardId = requestDto.getBoardId();
        Boolean isFavorite = requestDto.getBoardId().getIsFavorite();
        User userId = requestDto.getUserId(); // 프론트에서 userId를 보내거나 세션에서 가져와야 함

        favoriteBoardService.toggleFavorite(boardId, isFavorite, userId);

        return ResponseEntity.ok().build();
    }

}
