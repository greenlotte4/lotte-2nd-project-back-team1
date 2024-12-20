package com.BackEndTeam1.entity;


import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.sql.Timestamp;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long taskId;

    @ManyToOne
    @JoinColumn(name = "project_item_id", nullable = false)
    private ProjectItem projectItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;

    private String name;

    private String description;

    private Integer priority;

    // 상태값은 projectItem의 name과 같음
    private String status;

    // Task 시작일자
    @Column(name = "start_date")
    private Date startDate;
    // Task 종료일자
    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    public void setAsignee(String assignee) {
        this.assignee = User.builder().userId(assignee).build();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStartDate(Date startDate) {this.startDate = startDate;}

    public void setEndDate(Date endDate) {this.endDate = endDate;}
}

