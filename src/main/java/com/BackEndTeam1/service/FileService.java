package com.BackEndTeam1.service;

import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${file.upload.path}")
    private String uploadDir;  // application.yml에서 경로 설정

    private final UserRepository userRepository;



    public String uploadProfileImage(String userId, MultipartFile file) throws IOException {
// 파일명 생성 (UUID를 사용하여 고유한 파일 이름을 생성)
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

        // 파일 저장 위치 설정
        Path targetLocation = Paths.get(uploadDir, fileName);

        // 파일을 지정된 위치에 저장
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // 파일의 다운로드 URL 생성
        String baseUrl = "http://localhost:8080/user/thumb/";

        String fileDownloadUri = "/user/thumb/" + fileName;
        String saveProfile = baseUrl + fileName;

        // 사용자 정보 업데이트: 파일 경로를 profile 필드에 저장
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setProfile(saveProfile);  // 프로필 URL을 user 엔티티에 저장
        userRepository.save(user);  // 변경된 사용자 정보 저장

        return fileDownloadUri;

    }
}
