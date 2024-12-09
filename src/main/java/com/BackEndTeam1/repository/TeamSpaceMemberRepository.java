package com.BackEndTeam1.repository;

import com.BackEndTeam1.entity.TeamSpace;
import com.BackEndTeam1.entity.TeamSpaceMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamSpaceMemberRepository extends JpaRepository<TeamSpaceMember, Long> {
    void deleteAllByTeamSpace_TeamSpaceId(Long teamSpaceId);

    void deleteByTeamSpace_TeamSpaceIdAndUser_UserId(Long teamSpaceId, String userId);

    boolean existsByTeamSpace_TeamSpaceIdAndUser_UserId(Long teamspaceId, String userId);
}
