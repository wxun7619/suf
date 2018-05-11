package com.lonntec.domainservice.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name="t_sys_user")
public class User {
    @Id
    @Column(name="frowid", length = 36)
    String rowId;

    @Column(name="fusername")
    String userName;

    @Column(name="fnickname")
    String nickName;

    @Column(name="fmobile")
    String mobile;

    @Column(name="femail")
    String email;

    @Column(name="fisadmin")
    Boolean isAdmin;

    @Column(name="fisenable")
    Boolean isEnable;

    public User() {
        rowId = UUID.randomUUID().toString();
    }

    public String getRowId() {
        return rowId;
    }

    public void setRowId(String rowId) {
        this.rowId = rowId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Boolean getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(Boolean enable) {
        isEnable = enable;
    }

    @Override
    public String toString() {
        return "User{" +
                "rowId='" + rowId + '\'' +
                ", userName='" + userName + '\'' +
                ", nickName='" + nickName + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", isAdmin=" + isAdmin +
                ", isEnable=" + isEnable +
                '}';
    }
}
