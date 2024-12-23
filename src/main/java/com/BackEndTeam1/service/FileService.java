package com.BackEndTeam1.service;

import com.BackEndTeam1.entity.Drive;
import com.BackEndTeam1.entity.DriveFile;
import com.BackEndTeam1.entity.Folder;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.repository.DriveFileRepository;
import com.BackEndTeam1.repository.DriveRepository;
import com.BackEndTeam1.repository.FolderRepository;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final DriveRepository driveRepository;
    private final FolderRepository folderRepository;
    private final DriveFileRepository driveFileRepository;
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
        String baseUrl = "https://hubflow.store/user/thumb/";

        String fileDownloadUri = "/user/thumb/" + fileName;
        String saveProfile = baseUrl + fileName;

        // 사용자 정보 업데이트: 파일 경로를 profile 필드에 저장
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setProfile(saveProfile);  // 프로필 URL을 user 엔티티에 저장
        userRepository.save(user);  // 변경된 사용자 정보 저장

        return fileDownloadUri;
    }

    public List<String> uploadFiles(String userId, Integer folderId, Integer driveId, MultipartFile[] files) throws IOException {
        // 사용자 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 드라이브 정보 조회
        Drive drive = driveRepository.findById(driveId)
                .orElseThrow(() -> new RuntimeException("드라이브를 찾을 수 없습니다."));

        // 폴더 정보 조회
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("폴더를 찾을 수 없습니다."));

        List<String> uploadedFileUrls = new ArrayList<>(); // 업로드된 파일 URL을 저장할 리스트

        // 여러 파일을 처리하기 위해 반복문 사용
        for (MultipartFile file : files) {
            // 파일 크기 제한 (예: 10MB)
            if (file.getSize() > 10 * 1024 * 1024) { // 10MB
                throw new RuntimeException("파일 크기가 너무 큽니다.");
            }

            // 파일 형식 제한 (예: 이미지 파일만 허용)
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image")) {
                throw new RuntimeException("허용되지 않는 파일 형식입니다.");
            }

            // 파일 이름을 고유하게 생성
            String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

            // 파일 저장 위치 설정 (uploadDir은 환경 설정에서 동적으로 설정하거나, 서버 경로를 사용)
            Path targetLocation = Paths.get(uploadDir, fileName);

            // 파일을 지정된 위치에 저장
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // 파일의 다운로드 URL 생성
            String baseUrl = "https://hubflow.store/user/thumb/";  // 서버의 기본 URL로 변경 필요
            String fileUrl = baseUrl + fileName;

            // DriveFile 객체 생성
            DriveFile driveFile = DriveFile.builder()
                    .drive(drive)  // 드라이브
                    .folder(folder)  // 폴더
                    .createdUser(user)  // 파일을 업로드한 사용자
                    .fileOriginalName(file.getOriginalFilename())  // 원본 파일 이름
                    .fileStoredName(fileName)  // 저장된 파일 이름
                    .fileSize((int) file.getSize())  // 파일 크기 (단위: 바이트)
                    .fileType(file.getContentType())  // 파일 타입 (MIME 타입)
                    .createdAt(new Timestamp(System.currentTimeMillis()))  // 생성 시간
                    .updatedAt(new Timestamp(System.currentTimeMillis()))  // 수정 시간
                    .build();

            // 파일 정보 데이터베이스에 저장
            driveFileRepository.save(driveFile);

            // 업로드된 파일 URL 리스트에 추가
            uploadedFileUrls.add(fileUrl);
        }

        // 업로드된 파일들의 URL 리스트 반환
        return uploadedFileUrls;
    }


    public String uploadMessageImage(MultipartFile file) throws IOException {
// 파일명 생성 (UUID를 사용하여 고유한 파일 이름을 생성)
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        // 파일 저장 위치 설정
        Path targetLocation = Paths.get(uploadDir, fileName);
        // 파일을 지정된 위치에 저장
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        // 파일의 다운로드 URL 생성
        String baseUrl = "https://hubflow.store/message/img/";

        String fileDownloadUri = "/message/img/" + fileName;
        String saveProfile = baseUrl + fileName;


        return fileDownloadUri;
    }
}
