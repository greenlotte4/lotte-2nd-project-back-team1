package com.BackEndTeam1.service;

import com.BackEndTeam1.entity.Drive;
import com.BackEndTeam1.entity.DriveFile;
import com.BackEndTeam1.entity.Folder;
import com.BackEndTeam1.repository.DriveFileRepository;
import com.BackEndTeam1.repository.DriveRepository;
import com.BackEndTeam1.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/*
    날짜 : 2024/12/04
    이름 : 박수정
    내용 : 드라이브 파일저장 페이지생성

    추가내역
    -------------
    00.00  000 - 00000
*/

@Slf4j
@Service
@RequiredArgsConstructor
public class DriveFileService {

    private final DriveFileRepository driveFileRepository;
    private final DriveRepository driveRepository;
    private final FolderRepository folderRepository;


    // 파일 업로드 /1/1/das/file.pdf
    public DriveFile uploadFile(MultipartFile file, Integer driveId, Integer folderId, Integer userId) {
        try {
            DriveFile driveFile = DriveFile.builder()
                    // 조건 필요 : 아이디 값이 없으면 최 상단 루트 (드라이브 바로 아래에 저장되게 해야함)
                    .driveFileId(null) // 새로 생성되는 ID
                    .fileOriginalName(file.getOriginalFilename())
                    .fileStoredName(generateStoredFileName(file.getOriginalFilename()))
                    .fileSize((int) file.getSize())
                    .fileType(file.getContentType())
                    .isDeleted(false)
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .build();

            // Drive와 Folder, User 엔티티의 ID를 설정 (추후 로직 추가 가능)
            // driveFile.setDrive(...);
            // driveFile.setFolder(...);
            // driveFile.setCreatedUser(...);

            return driveFileRepository.save(driveFile);
        } catch (Exception e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }

    // 파일 목록 조회
    public List<DriveFile> getAllFiles() {
        return driveFileRepository.findAll();
    }

    // 파일 상세 조회
    public Optional<DriveFile> getFileById(Integer fileId) {
        return driveFileRepository.findById(fileId);
    }

    // 파일 삭제
    public void deleteFile(Integer fileId) {
        driveFileRepository.findById(fileId).ifPresent(file -> {
            file.setIsDeleted(true);
            file.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            driveFileRepository.save(file);
        });
    }

    // 저장 파일명 생성 (중복 방지를 위해 UUID 사용 가능)
    private String generateStoredFileName(String originalName) {
        return System.currentTimeMillis() + "_" + originalName;
    }


    // 폴더생성
    public Folder createFolder( String userId, String folderName, boolean isShared) {
        log.info("폴더이름" + folderName);
        log.info("드라이브타입" + isShared);
        log.info("아이디" + userId);
        // 드라이브 타입 설정 (공유/개인)
        Drive.DriveType driveType = isShared ? Drive.DriveType.SHARED : Drive.DriveType.PERSONAL;

        // 드라이브 찾기 (유저의 드라이브 타입에 맞는 드라이브를 찾음)
        Drive drive = driveRepository.findByDriveTypeAndUser_UserId(driveType, userId)
                .orElseThrow(() -> new RuntimeException("사용자의 드라이브를 찾을 수 없습니다. userId: " + userId + ", driveType: " + driveType));

        // 폴더 객체 생성
        Folder folder = Folder.builder()
                .drive(drive)  // 해당 드라이브에 폴더 연결
                .name(folderName)  // 폴더 이름 설정
                .createdAt(new Timestamp(System.currentTimeMillis()))  // 생성 시간
                .updatedAt(new Timestamp(System.currentTimeMillis()))  // 업데이트 시간
                .createdUser(drive.getUser())
                .isDeleted(false)  // 삭제 여부
                .isShared(isShared)
                .build();

        // 폴더 저장
        return folderRepository.save(folder);  // 폴더를 DB에 저장
    }
}
