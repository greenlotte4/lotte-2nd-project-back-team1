package com.BackEndTeam1.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "boardarticle")
public class BoardArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 글 고유 ID

    @Column(nullable = false, length = 200)
    private String title; // 글 제목

    @Lob
    private String content; // 글 본문

    @ManyToOne(fetch = FetchType.LAZY) // 작성자와의 관계 설정
    @JoinColumn(name = "user_id") // 외래 키(user_id)를 매핑
    private User author;

    @Column(nullable = false, updatable = false)
    private LocalDateTime created_At; // 글 작성 시간

    @Column(nullable = false)
    private LocalDateTime updated_At; // 글 수정 시간

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;
}
