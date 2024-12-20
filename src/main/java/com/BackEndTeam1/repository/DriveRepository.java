package com.BackEndTeam1.repository;

import com.BackEndTeam1.entity.Drive;
import com.BackEndTeam1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
public interface DriveRepository extends JpaRepository<Drive, Integer> {
    Optional<Drive> findByUserAndDriveType(User user, Drive.DriveType driveType);
    Optional<Drive> findByDriveTypeAndUser_UserId(Drive.DriveType driveType, String userId);

}
