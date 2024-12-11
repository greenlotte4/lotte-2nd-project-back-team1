package com.BackEndTeam1.repository;

import com.BackEndTeam1.entity.Calendar;
import com.BackEndTeam1.entity.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Integer> {
    List<CalendarEvent> findByCalendar(Calendar calendar);
}
