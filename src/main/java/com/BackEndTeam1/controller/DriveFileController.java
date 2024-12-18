package com.BackEndTeam1.controller;

import com.BackEndTeam1.dto.FolderRequestDTO;
import com.BackEndTeam1.dto.FolderResponseDTO;
import com.BackEndTeam1.entity.DriveFile;
import com.BackEndTeam1.entity.Folder;
import com.BackEndTeam1.service.DriveFileService;
import com.BackEndTeam1.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/*
    날짜 : 2024/12/04
    이름 : 박수정
    내용 : 드라이브 파일저장 페이지생성

    추가내역
    -------------
    00.00  000 - 00000
*/

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@RequestMapping("/api/drive-files")
public class DriveFileController {

    private final DriveFileService driveFileService;
    private final FileService fileService;

    // 파일 업로드
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("userId") String userId, @RequestParam("files") MultipartFile files) {
        log.info("Uploading file");
        try {
            // 서비스 호출
            String fileDownloadUri = fileService.uploadProfileImage(userId, files);
            return ResponseEntity.status(HttpStatus.OK).body(fileDownloadUri); // 성공 시 이미지 URL 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("업로드 실패: " + e.getMessage());
        }
    }

    @PostMapping("/create/folder")
    public ResponseEntity createFolder(@RequestBody Map<String, String> requestBody) {
        log.info("폴더 생성 요청");
        String userId = requestBody.get("userId");
        String folderName = requestBody.get("folderName");
        String driveId = requestBody.get("driveId");
        log.info("폴더이름" + folderName);
        log.info("드라이브타입" + driveId);
        log.info("아이디" + userId);

        boolean isShared = "2".equals(driveId);  // 2이면 공유 드라이브, 1이면 개인 드라이브

        return ResponseEntity.status(HttpStatus.OK).body(driveFileService.createFolder(userId,folderName,isShared));
    }


    // 폴더 목록 조회
    @GetMapping
    public ResponseEntity<List<DriveFile>> getAllFiles() {
        List<DriveFile> files = driveFileService.getAllFiles();
        return ResponseEntity.ok(files);
    }

    // 파일 상세 조회
    @GetMapping("/{fileId}")
    public ResponseEntity<DriveFile> getFileById(@PathVariable Integer fileId) {
        return driveFileService.getFileById(fileId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 파일 삭제
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Integer fileId) {
        driveFileService.deleteFile(fileId);
        return ResponseEntity.noContent().build();
    }
}
