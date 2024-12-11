package com.BackEndTeam1.repository;

import com.BackEndTeam1.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Integer> {
}
