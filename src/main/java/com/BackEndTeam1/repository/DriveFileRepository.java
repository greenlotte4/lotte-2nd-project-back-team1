package com.BackEndTeam1.repository;

import com.BackEndTeam1.entity.DriveFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}
