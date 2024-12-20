package com.BackEndTeam1.repository;

import com.BackEndTeam1.entity.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CalendarRepository extends JpaRepository<Calendar, Integer> {
    boolean existsByCalendarCode(String inviteCode);

    Optional<Object> findByCalendarCode(String calendarCode);

    int countByUser_UserId(String userId);
}
