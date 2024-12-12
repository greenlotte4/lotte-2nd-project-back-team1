package com.BackEndTeam1.repository.mongo;

import com.BackEndTeam1.document.ChatTextDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatTextRepository extends MongoRepository<ChatTextDocument, String> {
    public List<ChatTextDocument> findByChatId(int chatId);
}
