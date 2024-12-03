package com.BackEndTeam1.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "plan")
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Long planId;

    private String name;

    private BigDecimal price;

    @Column(name = "drive_capacity")
    private Integer driveCapacity;

    @Column(name = "max_file_size")
    private Integer maxFileSize;

    @Column(name = "max_project")
    private Integer maxProject;

    @Column(name = "max_collaborators")
    private Integer maxCollaborators;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;
}
