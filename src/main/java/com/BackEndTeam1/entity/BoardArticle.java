package com.BackEndTeam1.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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
    @JsonIgnore
    private Board board;

    @Column(nullable = false, length = 20)
    private String status = "active"; // "active", "trash", "deleted"

    @Column(name = "trash_date" , nullable = true)
    private LocalDateTime trashDate; // 휴지통 이동 날짜

    @ManyToOne(fetch = FetchType.LAZY) // 삭제자와의 관계 설정
    @JoinColumn(name = "deleted_by") // 외래 키(deleted_by)를 매핑
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User deletedBy; // 삭제자

    @OneToMany(mappedBy = "boardArticle", cascade = CascadeType.ALL, fetch = FetchType.LAZY) // BoardFile과 연관 설정
    private List<BoardFile> files; // 게시글에 첨부된 파일들

    @Column(name = "must_read", nullable = false)
    private Boolean mustRead = false; // 필독 여부 (기본값: false)

    @Column(name = "notification", nullable = false)
    private Boolean notification = false;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ImportantArticle> importantArticles;



    public BoardArticle(Long articleId) {
        this.id = articleId;
    }
}
