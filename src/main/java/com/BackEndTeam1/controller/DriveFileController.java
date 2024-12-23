package com.BackEndTeam1.controller;

import com.BackEndTeam1.dto.DriveItem;
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

import java.io.IOException;
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
    public ResponseEntity<Object> uploadFile(
            @RequestParam("files") MultipartFile[] files,  // 여러 파일을 받을 때
            @RequestParam("folderId") Integer folderId,    // 폴더 ID
            @RequestParam("driveId") Integer driveId,      // 드라이브 ID
            @RequestParam("userId") String userId          // 사용자 ID
    ) {
        try {
            if (folderId == 0) {
                folderId = null;  // 최상위 폴더를 의미하도록 null을 그대로 유지
            }
            // 파일 업로드 처리 및 데이터베이스 저장
            List<String> fileUrls = fileService.uploadFiles(userId, folderId, driveId, files);

            // 파일 URL 리스트를 JSON 형식으로 반환
            return ResponseEntity.ok(fileUrls);  // 성공적으로 파일 URL들을 반환
        } catch (IOException e) {
            e.printStackTrace();
            // 파일 업로드 실패시 에러 메시지 반환
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일 업로드 실패: " + e.getMessage());
        }
    }

    @PostMapping("/create/folder")
    public ResponseEntity createFolder(@RequestBody Map<String, String> requestBody) {
        log.info("폴더 생성 요청");
        String userId = requestBody.get("userId");
        String folderName = requestBody.get("folderName");
        String driveId = requestBody.get("driveId");
        Integer folderId = null;
        try {
            String folderIdStr = requestBody.get("folderId");
            if (folderIdStr != null && !folderIdStr.isEmpty()) {
                folderId = Integer.valueOf(folderIdStr);  // Null과 빈 문자열을 체크한 후 숫자로 변환
            }
        } catch (NumberFormatException e) {
            log.error("폴더 ID 변환 실패", e); // 예외 로깅
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid folderId");
        }
        boolean isShared = "2".equals(driveId);  // 2이면 공유 드라이브, 1이면 개인 드라이브

        return ResponseEntity.status(HttpStatus.OK).body(driveFileService.createFolder(userId,folderName,isShared,folderId));
    }


    // 폴더 목록 조회
    @GetMapping
    public ResponseEntity<List<DriveFile>> getAllFiles() {
        List<DriveFile> files = driveFileService.getAllFiles();
        return ResponseEntity.ok(files);
    }

    @PostMapping("/select/driveData")
    public ResponseEntity<List<Folder>> selectDriveUser(
            @RequestParam("driveId") Integer driveId,
            @RequestParam("userId") String userId){
        log.info("드라이브 요청");
        try {
            // 드라이브 및 폴더, 파일 목록 가져오기
            List<Folder> folders = driveFileService.getUserFoldersWithFiles(userId, driveId);
            log.info("파일 목록 가져오기 결과: {}", folders);

            return ResponseEntity.status(HttpStatus.OK).body(folders);
        } catch (RuntimeException e) {
            // 예외 처리 (드라이브를 찾을 수 없는 경우)
            log.info("파일 목록 가져오기 실패: ", e);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
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

    // 자식 폴더 조회
    @PostMapping("/child/folder")
    public ResponseEntity<List<Folder>> getChildFolders(@RequestBody FolderRequestDTO folderRequest) {
        try {
            // 요청받은 folderId에 대해 자식 폴더 조회
            List<Folder> childFolders = driveFileService.getChildFolders(folderRequest.getFolderId());
            return ResponseEntity.ok(childFolders);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(null);  // 폴더를 찾을 수 없으면 404 반환
        }
    }
}
