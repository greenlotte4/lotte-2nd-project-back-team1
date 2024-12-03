package com.lotte2backteam1.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user")
public class User {

    @Id
    private String uid;

    private String pass;
    private String name;
    private String email;
    private String hp;
    private String zip;
    private String addr1;
    private String addr2;

    @Builder.Default
    private String role = "USER";

    @CreationTimestamp
    private LocalDateTime regData;


}
