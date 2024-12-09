package com.BackEndTeam1.service;

import com.BackEndTeam1.entity.TeamSpace;
import com.BackEndTeam1.repository.TeamSpaceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class TeamSpaceService {
    private final TeamSpaceRepository teamSpaceRepository;
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
}
