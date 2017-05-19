package com.myschool.schoolcircle.entity;

/**
 * Created by Mr.R on 2016/8/24.
 */
public class Tb_notification {

    private int _id;
    private String username;
    private String avatar;
    private String title;
    private String message;
    private String type;
    private long time;

    public Tb_notification() {

    }

    public Tb_notification(int _id, String avatar, String title,
                           String message, String type, long time) {
        this._id = _id;
        this.avatar = avatar;
        this.title = title;
        this.message = message;
        this.type = type;
        this.time = time;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
