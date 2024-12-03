package com.lotte2backteam1.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private String uid;
    private String pass;
    private String name;
    private String email;
    private String hp;
    private String zip;
    private String addr1;
    private String addr2;
    private String regData;
    private String role;
}
