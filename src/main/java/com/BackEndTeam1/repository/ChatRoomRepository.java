package com.BackEndTeam1.repository;

import com.BackEndTeam1.entity.Chat;
import com.BackEndTeam1.entity.ChatRoom;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {
    public List<ChatRoom> findByUserUserId(String userId);

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.chat.chatId IN " +
            "(SELECT c.chat.chatId FROM ChatRoom c WHERE c.user.userId = :userId)")
    public List<ChatRoom> findByUserId(@Param("userId") String userId);


    List<ChatRoom> findByChatChatId(Integer chatId);

    @Query("SELECT COUNT(cr) FROM ChatRoom cr " +
            "WHERE cr.chat.dtype = 'DM' AND cr.user.userId = :userId")
    int countByDMAndUserId(@Param("userId") String userId);

}
