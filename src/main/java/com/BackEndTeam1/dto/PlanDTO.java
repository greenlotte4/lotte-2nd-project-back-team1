package com.BackEndTeam1.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlanDTO {
    private Long planId;
    private String name;
    private BigDecimal price;
    private Integer driveCapacity;
    private Integer maxFileSize;
    private Integer maxProject;
    private Integer maxCollaborators;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
