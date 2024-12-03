package com.BackEndTeam1.controller;

import com.BackEndTeam1.dto.UserDTO;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.jwt.JwtProvider;
import com.BackEndTeam1.security.MyUserDetails;
import com.BackEndTeam1.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody UserDTO userDTO) {
        UserDTO savedUser = userService.saveUser(userDTO);
        return ResponseEntity.status(HttpStatus.OK).body(savedUser);
    }


//    @PostMapping("/login")
//    public ResponseEntity login(@RequestBody UserDTO userDTO) {
//        userService.findByUserId(userDTO.getUserId());
//        return ResponseEntity.status(HttpStatus.OK).body(userDTO);
//    }
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody UserDTO userDTO) {

        try {
            // 시큐리티 사용자 검증
            UsernamePasswordAuthenticationToken token
                    = new UsernamePasswordAuthenticationToken(userDTO.getUserId(), userDTO.getPass());

            Authentication authentication = authenticationManager.authenticate(token);

            MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();
            log.info("user : " + user);

            // JWT 토큰 발행
            String accessToken = jwtProvider.createToken(user, 1);
            String refreshToken = jwtProvider.createToken(user, 7);
            log.info("accessToken : " + accessToken);

            // 리프레쉬 토큰 DB저장

            // 토큰 전송
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("username", user.getUserId());
            resultMap.put("role", user.getRole());
            resultMap.put("accessToken", accessToken);
            resultMap.put("refreshToken", refreshToken);

            return ResponseEntity.ok(resultMap);

        }catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("USER NOT FOUND");
        }
    }

    @GetMapping("/api/check-user-id/{userId}")
    public ResponseEntity<Map<String, Boolean>> checkUserId(@PathVariable String userId) {
        boolean isAvailable = userService.isUserIdAvailable(userId);
        return ResponseEntity.ok(Map.of("isAvailable", isAvailable));
    }

    @GetMapping("/api/check-phone/{hp}")
    public ResponseEntity<Map<String, Boolean>> checkHp(@PathVariable String userId) {
        boolean isAvailable = userService.isUserIdAvailable(userId);
        return ResponseEntity.ok(Map.of("isAvailable", isAvailable));
    }



}
