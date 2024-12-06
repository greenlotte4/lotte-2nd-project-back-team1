package com.BackEndTeam1.repository;

import com.BackEndTeam1.entity.PlanHistory;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Modifying // 데이터 변경 작업임을 명시
    @Transactional // 트랜잭션을 보장
    @Query("DELETE FROM PlanHistory ph WHERE ph.user IS NOT NULL AND ph.user.userId = :userId")
    void deleteByUserId(@Param("userId") String userId);

    @Query("SELECT ph FROM PlanHistory ph WHERE ph.user.userId = :userId")
    PlanHistory findByUserId(@Param("userId") String userId);
}
