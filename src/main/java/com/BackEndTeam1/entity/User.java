package com.BackEndTeam1.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "user")
public class User {

    @Id
    @Column(name = "user_id", nullable = false)
    private String userId;

    private String username;

    private String email;

    @Column(name = "pass")
    private String pass;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plan plan; // 유료/ 종류

    @Builder.Default
    private String role = "USER";

    private String hp;

    private String addr1;

    private String addr2;

    private String zipcode;

    private String status;

    @Column(name = "created_at")
    private Timestamp createdAt; // 생성일

    @Column(name = "updated_at")
    private Timestamp updatedAt; // 로그인 날

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    private List<Project> projects = new ArrayList<>();

}
