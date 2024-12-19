package com.BackEndTeam1.repository;

import com.BackEndTeam1.entity.Calendar;
import com.BackEndTeam1.entity.CalendarEvent;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Integer> {
    List<CalendarEvent> findByCalendar(Calendar calendar);
    @Modifying
    @Transactional
    @Query("DELETE FROM CalendarEvent e WHERE e.calendar.calendarId = :calendarId")
    void deleteByCalendarId(@Param("calendarId") Integer calendarId);

    List<CalendarEvent> findByCalendar_CalendarId(int i);
}
