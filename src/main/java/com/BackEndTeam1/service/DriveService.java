package com.BackEndTeam1.service;

import com.BackEndTeam1.entity.Drive;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.repository.DriveRepository;
import com.BackEndTeam1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DriveService {
    private final DriveRepository driveRepository;
    private final UserRepository userRepository;

    public void initializeDrivesForUser() {
        String userId = getCurrentUserId();  // 현재 로그인된 유저의 ID를 받아오는 메서드 호출
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 개인 드라이브가 이미 존재하는지 확인
        Optional<Drive> existingPersonalDrive = driveRepository.findByUserAndDriveType(user, Drive.DriveType.PERSONAL);
        if (existingPersonalDrive.isPresent()) {
            return; // 개인 드라이브가 이미 존재하면 리턴
        }

        // 공유 드라이브가 이미 존재하는지 확인
        Optional<Drive> existingSharedDrive = driveRepository.findByUserAndDriveType(user, Drive.DriveType.SHARED);
        if (existingSharedDrive.isPresent()) {
            return; // 공유 드라이브가 이미 존재하면 리턴
        }

        // 개인 드라이브 생성
        Drive personalDrive = Drive.builder()
                .user(user)
                .maxCollaborators(5)
                .driveCapacity(1024)
                .maxFileSize(500)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .updatedAt(new Timestamp(System.currentTimeMillis()))
                .driveType(Drive.DriveType.PERSONAL)
                .build();

        // 공유 드라이브 생성
        Drive sharedDrive = Drive.builder()
                .user(user)
                .maxCollaborators(100)
                .driveCapacity(1024)
                .maxFileSize(1000)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .updatedAt(new Timestamp(System.currentTimeMillis()))
                .driveType(Drive.DriveType.SHARED)
                .build();

        // 드라이브 저장
        driveRepository.save(personalDrive);
        driveRepository.save(sharedDrive);
    }

    // 현재 로그인된 사용자 ID를 반환하는 메서드
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == "anonymousUser") {
            throw new RuntimeException("사용자가 인증되지 않았습니다.");
        }
        return authentication.getName();  // 일반적으로 사용자 이름을 ID로 사용
    }
}
