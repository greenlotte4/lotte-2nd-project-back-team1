package com.BackEndTeam1.repository;

import com.BackEndTeam1.entity.CalendarUser;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CalendarUserRepository extends JpaRepository<CalendarUser, Integer> {
    List<CalendarUser> findByUserUserId(String userId);
    @Modifying
    @Transactional
    @Query("DELETE FROM CalendarUser u WHERE u.calendar.calendarId = :calendarId")
    void deleteByCalendarId(@Param("calendarId") Integer calendarId);

    @Transactional
    @Modifying
    @Query("DELETE FROM CalendarUser cu WHERE cu.calendar.calendarId = :calendarId AND cu.user.userId = :userId")
    void deleteByCalendarIdAndUserId(@Param("calendarId") Integer calendarId, @Param("userId") String userId);

    boolean existsByCalendar_CalendarIdAndUser_UserId(Integer calendarId, String userId);

    int countByCalendar_CalendarId(Integer calendarId);
}
