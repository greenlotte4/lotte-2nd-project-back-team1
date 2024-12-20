package com.BackEndTeam1.repository;

import com.BackEndTeam1.entity.TeamSpace;
import com.BackEndTeam1.entity.TeamSpaceMember;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamSpaceMemberRepository extends JpaRepository<TeamSpaceMember, Long> {
    void deleteAllByTeamSpace_TeamSpaceId(Long teamSpaceId);

    void deleteByTeamSpace_TeamSpaceIdAndUser_UserId(Long teamSpaceId, String userId);

    boolean existsByTeamSpace_TeamSpaceIdAndUser_UserId(Long teamspaceId, String userId);

    @Query("SELECT tsm.teamSpace FROM TeamSpaceMember tsm WHERE tsm.user.userId = :userId")
    List<TeamSpace> findTeamSpacesByUser_UserId(@Param("userId") String userId);

    List<TeamSpaceMember> findByTeamSpace_TeamSpaceIdIn(List<Long> teamSpaceIds);

    List<TeamSpaceMember> findByUser_UserId(String userId);

    TeamSpaceMember findByTeamSpace_TeamSpaceId(Long teamspaceId);

    int countByTeamSpace_TeamSpaceId(Long teamspaceId);
}
