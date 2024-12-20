package com.BackEndTeam1.service;

import com.BackEndTeam1.document.UserLoginDocument;
import com.BackEndTeam1.dto.TeamSpaceMemberDTO;
import com.BackEndTeam1.dto.TeamSpaceUsersDto;
import com.BackEndTeam1.dto.UserDTO;
import com.BackEndTeam1.entity.TeamSpace;
import com.BackEndTeam1.entity.TeamSpaceMember;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.repository.TeamSpaceMemberRepository;
import com.BackEndTeam1.repository.TeamSpaceRepository;
import com.BackEndTeam1.repository.UserRepository;
import com.BackEndTeam1.repository.mongo.UserLoginRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class TeamSpaceMemberService {
    private final TeamSpaceMemberRepository teamSpaceMemberRepository;
    private final UserLoginRepository userLoginRepository;
    private final UserRepository userRepository;
    private final TeamSpaceRepository teamSpaceRepository;
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
        TeamSpace teamSpace = teamSpaceRepository.findById(teamspaceId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 팀 스페이스 ID입니다."));
        String serialNumber = teamSpace.getSerialnumber();
        User owner = teamSpace.getUser(); // 방장 사용자
        int maxCollaborators = owner.getPlan().getMaxCollaborators();
        // 현재 팀스페이스의 멤버 수 확인
        int currentMembers = teamSpaceMemberRepository.countByTeamSpace_TeamSpaceId(teamspaceId);

        // 최대 인원 초과 여부 확인
        if (currentMembers >= maxCollaborators) {
            throw new IllegalStateException("최대 협업 인원 수를 초과했습니다. 추가 멤버를 초대할 수 없습니다.");
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

        // 3. UserLoginDocument 조회 (중복 키 처리 추가)
        Map<String, String> userStatusMap = teamSpaceMemberRepository.findByTeamSpace_TeamSpaceIdIn(teamSpaceIds).stream()
                .map(TeamSpaceMember::getUser)
                .filter(user -> !user.getUserId().equals(userId)) // 나 자신 제외
                .map(user -> {
                    UserLoginDocument status = userLoginRepository.findByUserId(user.getUserId())
                            .orElse(null);
                    return Map.entry(user.getUserId(), status != null ? status.getCurrentStatus() : "offline");
                })
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, replacement) -> replacement // 중복 발생 시 새로운 값으로 대체
                ));

        // 4. 팀스페이스와 일치하는 모든 멤버 조회
        List<TeamSpaceMember> allMembersInMyTeamSpaces = teamSpaceMemberRepository.findByTeamSpace_TeamSpaceIdIn(teamSpaceIds);

        // 5. 팀스페이스별로 그룹
        Map<TeamSpace, List<TeamSpaceMember>> teamToMembersMap = allMembersInMyTeamSpaces.stream()
                .collect(Collectors.groupingBy(
                        TeamSpaceMember::getTeamSpace,
                        Collectors.filtering(tsm -> !tsm.getUser().getUserId().equals(userId), Collectors.toList())
                ));

        // 6. Map을 DTO 형태로 변환
        return teamToMembersMap.entrySet().stream()
                .map(entry -> {
                    TeamSpace teamSpace = entry.getKey();
                    List<UserDTO> userDtos = entry.getValue().stream()
                            .map(TeamSpaceMember::getUser)
                            .map(user -> new UserDTO(
                                    user.getUserId(),
                                    user.getUsername(),
                                    user.getProfile(),
                                    userStatusMap.getOrDefault(user.getUserId(), "offline")
                            ))
                            .toList();

                    return new TeamSpaceUsersDto(
                            teamSpace.getTeamSpaceId(),
                            teamSpace.getRoomname(),
                            userDtos // 멤버가 없으면 빈 리스트로 전달
                    );
                })
                .toList();
    }
}
