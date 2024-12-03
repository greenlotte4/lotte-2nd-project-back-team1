package com.lotte2backteam1.controller;

import com.lotte2backteam1.dto.UserDTO;
import com.lotte2backteam1.entity.User;
import com.lotte2backteam1.jwt.JwtProvider;
import com.lotte2backteam1.security.MyUserDetails;
import com.lotte2backteam1.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @PostMapping("/user/login")
    public ResponseEntity login(@RequestBody UserDTO userDTO){
        log.info("Login 요청");

        try {
            UsernamePasswordAuthenticationToken token
                    = new UsernamePasswordAuthenticationToken(userDTO.getUid(), userDTO.getPass());

            Authentication authentication = authenticationManager.authenticate(token);

            MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();
            log.info("user : " + user);

            String accessToken = jwtProvider.createToken(user,1);
            String refreshToken = jwtProvider.createToken(user,7);

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("username", user.getUid());
            resultMap.put("role", user.getRole());
            resultMap.put("accessToken", accessToken);
            resultMap.put("refreshToken", refreshToken);
            log.info("로그인 완료");

            return ResponseEntity.ok(resultMap);
        }catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("USER NOT FOUND");
        }
    }

    @PostMapping("/user")
    public ResponseEntity register(@RequestBody UserDTO userDTO){
        log.info("회원가입 요청");
        UserDTO saveduser = userService.save(userDTO);

        return ResponseEntity.status(HttpStatus.OK).body(saveduser);
    }
}
