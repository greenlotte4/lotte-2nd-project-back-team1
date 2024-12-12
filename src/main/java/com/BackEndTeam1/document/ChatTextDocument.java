package com.BackEndTeam1.document;

import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document(value = "ChatText") // Mongdb의 Collection

public class ChatTextDocument {
    @Id
    private String id;

    private String senderId;
    private String context;
    private int chatId;


    @CreatedDate
    private LocalDateTime SendTime; // 자동 생성 시간

}
