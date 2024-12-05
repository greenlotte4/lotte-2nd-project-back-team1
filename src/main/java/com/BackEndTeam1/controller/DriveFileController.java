package com.BackEndTeam1.controller;

import com.BackEndTeam1.entity.DriveFile;
import com.BackEndTeam1.service.DriveFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/*
    날짜 : 2024/12/04
    이름 : 박수정
    내용 : 드라이브 파일저장 페이지생성

    추가내역
    -------------
    00.00  000 - 00000
*/

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/drive-files")
public class DriveFileController {

    private final DriveFileService driveFileService;

    // 파일 업로드
    @PostMapping("/upload")
    public ResponseEntity<DriveFile> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("driveId") Integer driveId,
            @RequestParam("folderId") Integer folderId,
            @RequestParam("userId") Integer userId) {
        DriveFile uploadedFile = driveFileService.uploadFile(file, driveId, folderId, userId);
        return ResponseEntity.ok(uploadedFile);
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
