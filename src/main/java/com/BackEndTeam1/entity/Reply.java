package com.BackEndTeam1.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reply")
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long replyId;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment; // 댓글과의 연관 관계

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 작성자 정보

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    private Timestamp createdAt;
}
