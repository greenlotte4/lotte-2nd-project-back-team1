package com.BackEndTeam1.document;

import com.BackEndTeam1.entity.TeamSpace;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "LoginState") // MongoDB의 Collection 이름
public class UserLoginDocument {
    @Id
    private String id;

    private String userId;
    private String profileimg;
    private String username;
    private String currentStatus; // online, dnd, away, logout

    private LocalDateTime lastUpdated;
    private List<String> roomname;
    private List<Long> teamid;
}
