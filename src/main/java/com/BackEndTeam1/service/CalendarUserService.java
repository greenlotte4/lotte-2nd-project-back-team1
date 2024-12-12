package com.BackEndTeam1.service;

import com.BackEndTeam1.repository.CalendarUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CalendarUserService {
    private final CalendarUserRepository calendarUserRepository;

    public void deleteuserfromcalendar(Integer calendarId, String userId) {
        calendarUserRepository.deleteByCalendarIdAndUserId(calendarId, userId);
    }
}
