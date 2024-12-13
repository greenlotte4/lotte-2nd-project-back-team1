package com.BackEndTeam1.service;

import com.BackEndTeam1.dto.TeamSpaceMemberDTO;
import com.BackEndTeam1.dto.TeamSpaceUsersDto;
import com.BackEndTeam1.dto.UserDTO;
import com.BackEndTeam1.entity.TeamSpace;
import com.BackEndTeam1.entity.TeamSpaceMember;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.repository.TeamSpaceMemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class TeamSpaceMemberService {
    private final TeamSpaceMemberRepository teamSpaceMemberRepository;
    public void membersave(TeamSpaceMember teamSpaceMember) {
        teamSpaceMemberRepository.save(teamSpaceMember);
    }

    public void deleteAllByTeamspaceId(Long teamSpaceId) {
        teamSpaceMemberRepository.deleteAllByTeamSpace_TeamSpaceId(teamSpaceId);
    }

    public boolean deleteByTeamspaceIdAndUserId(Long teamspaceId, String userId) {
        boolean exists = teamSpaceMemberRepository
                .existsByTeamSpace_TeamSpaceIdAndUser_UserId(teamspaceId, userId);

        if (exists) {
            teamSpaceMemberRepository.deleteByTeamSpace_TeamSpaceIdAndUser_UserId(teamspaceId, userId);
            return true; // 삭제 성공
        }
        return false; // 삭제 대상이 없음
    }

    public boolean addMemberToTeamSpace(Long teamspaceId, String userId) {
        // 이미 존재 여부 확인
        boolean exists = teamSpaceMemberRepository.existsByTeamSpace_TeamSpaceIdAndUser_UserId(teamspaceId, userId);
        if (exists) {
            return false; // 이미 존재
        }

        // 새 멤버 저장
        TeamSpaceMember member = TeamSpaceMember.builder()
                .teamSpace(TeamSpace.builder().teamSpaceId(teamspaceId).build())
                .user(User.builder().userId(userId).build())
                .build();

        teamSpaceMemberRepository.save(member);
        return true;
    }

    public List<TeamSpaceUsersDto> getUsersInTeamSpacesByUserId(String userId) {
        // 1. 아이디와 일치하는 팀스페이스 조회
        List<TeamSpaceMember> userTeamSpaces = teamSpaceMemberRepository.findByUser_UserId(userId);
        // 2. 일치한 팀스페이스 아이디 추출
        List<Long> teamSpaceIds = userTeamSpaces.stream()
                .map(tsm -> tsm.getTeamSpace().getTeamSpaceId())
                .distinct()
                .toList();
        // 팀스페이스가 없는 경우 빈 리스트 반환
        if (teamSpaceIds.isEmpty()) {
            return List.of();
        }

        // 3. 팀스페이스와 일치하는 모든 멤버 조회
        List<TeamSpaceMember> allMembersInMyTeamSpaces = teamSpaceMemberRepository.findByTeamSpace_TeamSpaceIdIn(teamSpaceIds);

        // 4. 팀스페이스별로 그룹
        Map<TeamSpace, List<TeamSpaceMember>> teamToMembersMap = allMembersInMyTeamSpaces.stream()
                .collect(Collectors.groupingBy(TeamSpaceMember::getTeamSpace));

        // 5. Map을 DTO 형태로 변환
        return teamToMembersMap.entrySet().stream()
                .map(entry -> {
                    TeamSpace teamSpace = entry.getKey();
                    List<UserDTO> userDtos = entry.getValue().stream()
                            .map(TeamSpaceMember::getUser)
                            .map(user -> new UserDTO(user.getUserId(), user.getUsername())) // 실제 User 필드명에 맞게 수정
                            .toList();

                    return new TeamSpaceUsersDto(teamSpace.getTeamSpaceId(), teamSpace.getRoomname(), userDtos);
                })
                .toList();
    }
}
