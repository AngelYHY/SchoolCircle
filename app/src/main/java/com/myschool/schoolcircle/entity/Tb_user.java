package com.myschool.schoolcircle.entity;

import java.io.Serializable;

/**
 * Created by Mr.R on 2016/7/16.
 */
public class Tb_user implements Serializable {

    private int user_id;
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String gender;
    private String birthday;
    private String startSchoolYear;
    private String signature;
    private String headView;

    public Tb_user() {

    }

    public Tb_user(String phone) {
        super();
        this.phone = phone;
    }

    public Tb_user(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    public Tb_user(String phone, String username, String password) {
        super();
        this.phone = phone;
        this.username = username;
        this.password = password;
    }

    public Tb_user(int id, String phone, String username, String password) {
        super();
        this.user_id = id;
        this.phone = phone;
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return user_id;
    }

    public void setId(int id) {
        this.user_id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getStartSchoolYear() {
        return startSchoolYear;
    }

    public void setStartSchoolYear(String startSchoolYear) {
        this.startSchoolYear = startSchoolYear;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getHeadView() {
        return headView;
    }

    public void setHeadView(String headView) {
        this.headView = headView;
    }

}
