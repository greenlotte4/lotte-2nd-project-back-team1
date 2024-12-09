package com.BackEndTeam1.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "teamspace")
public class TeamSpace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teamspace_id")
    private Long teamSpaceId;

    private String roomname;
    private String serialnumber;
    @Column(name = "end_date")
    private LocalDate endDate;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
