package com.BackEndTeam1.repository;

import com.BackEndTeam1.entity.CalendarUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CalendarUserRepository extends JpaRepository<CalendarUser, Integer> {
    List<CalendarUser> findByUserUserId(String userId);
}
