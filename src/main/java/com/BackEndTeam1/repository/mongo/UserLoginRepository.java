package com.BackEndTeam1.repository.mongo;

import com.BackEndTeam1.document.UserLoginDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserLoginRepository extends MongoRepository<UserLoginDocument, String> {
    Optional<UserLoginDocument> findByUserId(String userId);

    List<UserLoginDocument> findByTeamidIn(List<Long> teamIds);
}
