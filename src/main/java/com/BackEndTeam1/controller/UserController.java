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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;


    // 회원가입
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody UserDTO userDTO) {
        log.info("화원가입 요청");
        UserDTO savedUser = userService.saveUser(userDTO);
        return ResponseEntity.status(HttpStatus.OK).body(savedUser);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody UserDTO userDTO) {
        log.info("로그인 요청"+userDTO.getUserId());
        try {
            log.info("로그인 트라이"+userDTO.getUserId());

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
            resultMap.put("username", user.getUsername());
            resultMap.put("userid", user.getUserId());
            resultMap.put("role", user.getRole());
            resultMap.put("email", user.getEmail());
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

    @GetMapping("/checkUserId/{userId}")
    public ResponseEntity<Map<String, Boolean>> checkUserId(@PathVariable String userId) {
        log.info("중복확인 요청" + userId);
        boolean isAvailable = userService.isUserIdAvailable(userId);
        return ResponseEntity.ok(Map.of("isAvailable", isAvailable));
    }




    @GetMapping("/api/check-phone/{hp}")
    public ResponseEntity<Map<String, Boolean>> checkHp(@PathVariable String userId) {
        boolean isAvailable = userService.isUserIdAvailable(userId);
        return ResponseEntity.ok(Map.of("isAvailable", isAvailable));
    }



}
