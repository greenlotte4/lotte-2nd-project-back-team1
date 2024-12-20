package com.BackEndTeam1.repository;

import com.BackEndTeam1.entity.Drive;
import com.BackEndTeam1.entity.DriveFile;
import com.BackEndTeam1.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
    날짜 : 2024/12/04
    이름 : 박수정
    내용 : 드라이브 파일저장 페이지생성

    추가내역
    -------------
    00.00  000 - 00000
*/
@Repository
public interface DriveFileRepository extends JpaRepository<DriveFile, Integer> {

    // 특정 유저의 파일 목록 조회 (User ID로 조회)
    List<DriveFile> findAllByCreatedUser_UserId(String userId);

    // 특정 폴더에 속한 파일 조회 (폴더 ID로 조회)
    List<DriveFile> findByFolder_FolderId(Integer folderId);

    // 특정 드라이브에 속한 파일 목록 조회 (Drive ID로 조회)
    List<DriveFile> findByDrive_DriveId(Integer driveId);

}
