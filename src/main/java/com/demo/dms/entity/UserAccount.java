package com.demo.dms.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "`USER_ACCOUNT`")
public class UserAccount {

    public UserAccount() {
    }

    public UserAccount(int userId, String fullName, String email, String password, String role, String devType) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.devType = devType;
    }

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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDevType() {
        return devType;
    }

    public void setDevType(String devType) {
        this.devType = devType;
    }
}
