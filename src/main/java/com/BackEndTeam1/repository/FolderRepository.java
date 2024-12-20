package com.BackEndTeam1.repository;

import com.BackEndTeam1.entity.Drive;
import com.BackEndTeam1.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/*
    날짜 : 2024/12/04
    이름 : 최영진
    내용 : 드라이브  페이지생성

    추가내역
    -------------
    00.00  000 - 00000
*/
@Repository
public interface FolderRepository extends JpaRepository<Folder, Integer> {

   /* // 특정 드라이브의 모든 폴더 조회
    List<Folder> findByDrive(Drive drive);

    // 폴더명으로 폴더 조회
    List<Folder> findByName(String folderName);

    // 드라이브 ID로 폴더 조회
    List<Folder> findByDrive_DriveId(Integer driveId);

    // 삭제되지 않은 폴더 조회
    List<Folder> findByIsDeletedFalse();*/

    List<Folder> findByParentFolder(Folder parentFolder);

    // 내 드라이브에서 부모 폴더가 null인 폴더들만 조회
    List<Folder> findByDrive_DriveIdAndCreatedUser_UserIdAndParentFolder_FolderIdIsNull(Integer driveId, String userId);

    // 공유 드라이브에서 부모 폴더가 null인 폴더들만 조회
    List<Folder> findByDrive_DriveIdAndIsDeletedFalseAndParentFolder_FolderIdIsNull(Integer driveId);

}