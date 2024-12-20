package com.BackEndTeam1.repository;

import com.BackEndTeam1.entity.TeamSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamSpaceRepository extends JpaRepository<TeamSpace, Long> {
    boolean existsBySerialnumber(String serialNumber);
    Optional<Object> findByUser_UserIdAndRoomname(String userId, String roomname);

    Optional<Object> findBySerialnumber(String serialNumber);
    int countByUser_UserId(String userId);
}
