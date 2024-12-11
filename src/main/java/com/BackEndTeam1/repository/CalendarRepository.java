package com.BackEndTeam1.repository;

import com.BackEndTeam1.entity.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarRepository extends JpaRepository<Calendar, Integer> {
    boolean existsByCalendarCode(String inviteCode);
}
