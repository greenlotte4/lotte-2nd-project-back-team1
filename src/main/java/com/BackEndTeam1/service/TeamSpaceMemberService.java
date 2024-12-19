package com.BackEndTeam1.service;

import com.BackEndTeam1.document.UserLoginDocument;
import com.BackEndTeam1.dto.TeamSpaceMemberDTO;
import com.BackEndTeam1.dto.TeamSpaceUsersDto;
import com.BackEndTeam1.dto.UserDTO;
import com.BackEndTeam1.entity.TeamSpace;
import com.BackEndTeam1.entity.TeamSpaceMember;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.repository.TeamSpaceMemberRepository;
import com.BackEndTeam1.repository.mongo.UserLoginRepository;
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
        // 1. MongoDB에서 UserLoginDocument 조회
        UserLoginDocument userLoginDocument = userLoginRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with userId: " + userId));

        // 2. 사용자의 teamid 리스트 가져오기
        List<Long> teamIds = userLoginDocument.getTeamid();
        List<String> roomNames = userLoginDocument.getRoomname();

        if (teamIds == null || teamIds.isEmpty()) {
            return List.of(); // 팀이 없으면 빈 리스트 반환
        }

        // 3. MongoDB에서 teamid에 해당하는 UserLoginDocuments 조회
        List<UserLoginDocument> membersInTeams = userLoginRepository.findByTeamidIn(teamIds);

        // 4. 팀별로 사용자 데이터 그룹화
        Map<Long, List<UserLoginDocument>> teamToMembersMap = membersInTeams.stream()
                .flatMap(doc -> doc.getTeamid().stream().map(teamId -> Map.entry(teamId, doc))) // 각 팀 아이디에 대해 UserLoginDocument 매핑
                .collect(Collectors.groupingBy(Map.Entry::getKey, // 팀 아이디로 그룹화
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList()))); // UserLoginDocument 리스트 생성


        // 5. DTO로 변환
        return teamToMembersMap.entrySet().stream()
                .map(entry -> {
                    Long teamSpaceId = entry.getKey();
                    List<UserLoginDocument> members = entry.getValue();

                    // roomname 매칭
                    int index = teamIds.indexOf(teamSpaceId);
                    String roomName;
                    if (index >= 0 && index < roomNames.size()) {
                        roomName = roomNames.get(index);
                    } else {
                        throw new IllegalStateException("Room name not found for teamSpaceId: " + teamSpaceId);
                    }

                    // 사용자 DTO 리스트 생성 - 나 자신 제외
                    List<UserDTO> userDtos = members.stream()
                            .filter(member -> !member.getUserId().equals(userId)) // 나 자신 제외
                            .map(member -> new UserDTO(member.getUserId(), member.getUsername()))
                            .toList();

                    return new TeamSpaceUsersDto(teamSpaceId, roomName, userDtos);
                })
                .toList();
    }
}
