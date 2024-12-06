package com.BackEndTeam1.repository;

import com.BackEndTeam1.entity.PlanHistory;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanHistoryRepository extends JpaRepository<PlanHistory, Long> {
    @Query("""
        SELECT ph
        FROM PlanHistory ph
        WHERE ph.user.userId IN :userIds
    """)
    List<PlanHistory> findPlanHistoriesByUserIds(@Param("userIds") List<String> userIds);
}
