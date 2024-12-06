package com.BackEndTeam1.controller;

import com.BackEndTeam1.service.EmailService;
import com.BackEndTeam1.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
@RestController
public class EmailController {
    private final EmailService emailService;
    private final Map<String, String> emailAuthCodes = new HashMap<>();
    private final UserService userService;

    public EmailController(EmailService emailService, UserService userService) {
        this.emailService = emailService;
        this.userService = userService;
    }

    @PostMapping("/api/send")
    public String sendEmailAuth(@RequestBody Map<String, String> request) {
        log.info("요청왔다");
        log.info("이메일 : " + request);

        String email = request.get("email");
        String authCode = generateAuthCode();

        emailAuthCodes.put(email, authCode); // 인증번호 저장
        log.info("저장된 인증번호 : " + emailAuthCodes); // 저장된 인증번호 확인

        emailService.sendEmail(email, "회원가입 인증번호", "인증번호: " + authCode);
        log.info("인증번호 : " + authCode);

        return "인증번호가 이메일로 전송되었습니다.";
    }

    @PostMapping("/api/verify")
    public ResponseEntity<String> verifyEmailAuth(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String authCode = request.get("authCode");
        log.info("인증번호 확인 요청");
        log.info("받은 인증번호 : " + authCode);
        log.info("인증하는 이메일 : " + email);

        // 현재 저장된 인증번호 상태를 출력하여 확인
        log.info("현재 저장된 인증번호 맵 : " + emailAuthCodes);


        if (authCode.equals(emailAuthCodes.get(email))) {
            emailAuthCodes.remove(email); // 인증번호 사용 후 삭제
            log.info("인증 성공");
            return ResponseEntity.ok("인증 성공");
        } else {
            log.info("인증 실패");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증 실패");
        }
    }
    @PostMapping("/api/checkEmail")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestBody Map<String, String> request) {
        log.info("이메일 중복확인 요청");
        String email = request.get("email");

        // 이메일이 존재하면 false, 존재하지 않으면 true로 반환
        boolean isAvailable = !userService.isEmailExists(email);
        return  ResponseEntity.ok(Map.of("isAvailable",isAvailable));
    }
    private String generateAuthCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6자리 숫자 생성
        return String.valueOf(code);
    }
    @PostMapping("/api/findPassEmail")
    public String sendEmailPasAuth(@RequestBody Map<String, String> request) {
        log.info("요청왔다");
        log.info("이메일,아이디 : " + request);

        String email = request.get("email");
        String authCode = generateAuthCode();

        emailAuthCodes.put(email, authCode); // 인증번호 저장
        log.info("저장된 인증번호 : " + emailAuthCodes); // 저장된 인증번호 확인

        emailService.sendEmail(email, "회원가입 인증번호", "인증번호: " + authCode);
        log.info("인증번호 : " + authCode);

        return "인증번호가 이메일로 전송되었습니다.";
    }


}
