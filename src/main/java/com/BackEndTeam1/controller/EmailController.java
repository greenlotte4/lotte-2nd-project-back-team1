package com.BackEndTeam1.controller;

import com.BackEndTeam1.service.EmailService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
public class EmailController {
    private final EmailService emailService;
    private final Map<String, String> emailAuthCodes = new HashMap<>();

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/api/send")
    public String sendEmailAuth(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String authCode = generateAuthCode();

        emailAuthCodes.put(email, authCode); // 인증번호 저장
        emailService.sendEmail(email, "회원가입 인증번호", "인증번호: " + authCode);
        return "인증번호가 이메일로 전송되었습니다.";
    }

    @PostMapping("/api/verify")
    public String verifyEmailAuth(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String authCode = request.get("authCode");

        if (authCode.equals(emailAuthCodes.get(email))) {
            emailAuthCodes.remove(email);
            return "인증 성공";
        }
        return "인증 실패";
    }

    private String generateAuthCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6자리 숫자 생성
        return String.valueOf(code);
    }
}
