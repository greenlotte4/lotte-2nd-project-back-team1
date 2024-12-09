package com.BackEndTeam1.service;

import com.BackEndTeam1.entity.TeamSpace;
import com.BackEndTeam1.entity.TeamSpaceMember;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.repository.TeamSpaceMemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

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

    public void deleteByTeamspaceIdAndUserId(Long teamspaceId, String userId) {
        teamSpaceMemberRepository.deleteByTeamSpace_TeamSpaceIdAndUser_UserId(teamspaceId, userId);
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
}
