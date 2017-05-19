package com.myschool.schoolcircle.entity;

/**
 * Created by Mr.R on 2016/6/7.
 */

/**
 * 聊天消息的实体类
 */
public class Tb_chat_message {
    private int id;
    private String receiverUsername;//接收者用户名
    private Tb_user sender;//发送者
    private String content;//消息内容
    private String time;//时间
    private String type;//类型

    public Tb_chat_message() {

    }

    public Tb_chat_message(String receiverUsername, Tb_user sender,
                           String content, String time, String type) {
        this.receiverUsername = receiverUsername;
        this.sender = sender;
        this.content = content;
        this.time = time;
        this.type = type;
    }

    public Tb_chat_message(int id, String receiverUsername, Tb_user sender,
                           String content, String time, String type) {
        this.id = id;
        this.receiverUsername = receiverUsername;
        this.sender = sender;
        this.content = content;
        this.time = time;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
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
