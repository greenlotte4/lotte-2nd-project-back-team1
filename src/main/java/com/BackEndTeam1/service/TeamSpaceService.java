package com.BackEndTeam1.service;

import com.BackEndTeam1.entity.TeamSpace;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.repository.TeamSpaceMemberRepository;
import com.BackEndTeam1.repository.TeamSpaceRepository;
import com.BackEndTeam1.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class TeamSpaceService {
    private final TeamSpaceRepository teamSpaceRepository;
    private final TeamSpaceMemberRepository teamSpaceMemberRepository;
    private final UserRepository userRepository;
    public boolean serialNumberExists(String serialNumber) {
        return teamSpaceRepository.existsBySerialnumber(serialNumber);
    }
    //방정보 찾기
    public TeamSpace findByUserAndRoomname(String userId, String roomname) {
        return (TeamSpace) teamSpaceRepository.findByUser_UserIdAndRoomname(userId, roomname)
                .orElseThrow(() -> new IllegalArgumentException("No TeamSpace found for userId: " + userId + " and roomname: " + roomname));
    }
    //방생성
    public void save(TeamSpace teamSpace) {
        User user = userRepository.findByUserId(teamSpace.getUser().getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));
        log.info("user : " + user);
        String userId  = user.getUserId();
        int MaxProject = user.getPlan().getMaxProject();
        log.info("MaxProject : " + MaxProject);
        int currentProjectCount = teamSpaceRepository.countByUser_UserId(userId);
        log.info("currentProjectCount : " + currentProjectCount);
        if (currentProjectCount == MaxProject) {
            throw new IllegalStateException("사용자의 최대 프로젝트 수를 초과할 수 없습니다.");
        }
        teamSpaceRepository.save(teamSpace);
    }

    //방삭제
    public void delete(TeamSpace teamSpace) {
        teamSpaceRepository.delete(teamSpace);
    }

    public TeamSpace findBySerialNumber(String serialNumber) {
        return (TeamSpace) teamSpaceRepository.findBySerialnumber(serialNumber)
                .orElse(null);
    }
    //방정보변경
    public boolean updateRoomName(String userId, Long teamspaceId, String newRoomName) {
        // teamspaceId와 userId를 기반으로 TeamSpace 조회
        TeamSpace teamSpace = teamSpaceRepository.findById(teamspaceId)
                .orElseThrow(() -> new IllegalArgumentException("TeamSpace not found for ID: " + teamspaceId));

        // 권한 확인 (userId가 소유자인지 확인)
        if (!teamSpace.getUser().getUserId().equals(userId)) {
            return false; // 권한 없음
        }

        // roomname 업데이트
        teamSpace.setRoomname(newRoomName);
        teamSpaceRepository.save(teamSpace);

        return true;
    }


    public List<TeamSpace> getTeamSpacesByUserId(String userId) {
        // TeamSpaceMember 테이블에서 userId 기준으로 TeamSpace 조회
        return teamSpaceMemberRepository.findTeamSpacesByUser_UserId(userId);
    }
}
