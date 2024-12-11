package com.BackEndTeam1.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User author;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 글 작성 시간

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // 글 수정 시간

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(nullable = false, length = 20)
    private String status = "active"; // "active", "trash", "deleted"

    @Column(name = "trash_date" , nullable = true)
    private LocalDateTime trashDate; // 휴지통 이동 날짜

    @ManyToOne(fetch = FetchType.LAZY) // 삭제자와의 관계 설정
    @JoinColumn(name = "deleted_by") // 외래 키(deleted_by)를 매핑
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User deletedBy; // 삭제자


}
