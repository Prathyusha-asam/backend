package com.demo.dms.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "`USER_ACCOUNT`")
@NoArgsConstructor
@AllArgsConstructor
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`USER_ID`")
    private int userId;

    @Column(name = "`FULL_NAME`")
    private String fullName;

    @Column(name = "`EMAIL`")
    private String email;

    @Column(name = "`PASSWORD`")
    private String password;

    @Column(name = "`ROLE`")
    private String role;

    @Column(name = "`DEV_TYPE`")
    private String devType;

}
