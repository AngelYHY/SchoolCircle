package com.myschool.schoolcircle.entity;

import java.io.Serializable;

/**
 * Created by Mr.R on 2016/7/11.
 */
public class Tb_message_info implements Serializable{
    private int _id;
    private String receiverUsername;
    private long targetId;
    private Tb_user sender;
    private String content;
    private String time;
    private String type;

    public Tb_message_info() {

    }

    public Tb_message_info(String receiverUsername, Tb_user sender, String content,
                           String time, String type) {
        this.receiverUsername = receiverUsername;
        this.sender = sender;
        this.content = content;
        this.time = time;
        this.type = type;
    }

    public Tb_message_info(int _id, String receiverUsername, Tb_user sender,
                           String content, String time, String type) {
        this._id = _id;
        this.receiverUsername = receiverUsername;
        this.sender = sender;
        this.content = content;
        this.time = time;
        this.type = type;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }

    public long getTargetId() {
        return targetId;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public Tb_user getSender() {
        return sender;
    }

    public void setSender(Tb_user sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
