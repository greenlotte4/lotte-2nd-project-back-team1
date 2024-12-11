package com.BackEndTeam1.service;

import com.BackEndTeam1.repository.CalendarEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CalendarEventService {
    private final CalendarEventRepository calendarEventRepository;
}
