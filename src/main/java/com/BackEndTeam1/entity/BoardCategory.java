package com.BackEndTeam1.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@Entity
//@Table(name = "board_category")
public class BoardCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_category_id")
    private Integer boardCategoryId;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    private String name;

    @Enumerated(EnumType.STRING)
    private BoardCategoryType type; // Enum: global, personal

    private String description;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;
}

enum BoardCategoryType {
    GLOBAL, PERSONAL
}

