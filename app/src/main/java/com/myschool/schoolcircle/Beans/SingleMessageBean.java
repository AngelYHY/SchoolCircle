package com.myschool.schoolcircle.Beans;

/**
 * Created by Mr.R on 2016/8/14.
 */
public class SingleMessageBean {
    private int id;
    private String fromUsername;
    private String targetUsername;
    private String realName;
    private String headView;
    private String content;
    private String time;
    private String type;

    public SingleMessageBean() {

    }

    public SingleMessageBean(int id, String fromUsername, String targetUsername,
                             String realName, String headView, String content,
                             String time,String type) {
        this.id = id;
        this.fromUsername = fromUsername;
        this.targetUsername = targetUsername;
        this.realName = realName;
        this.headView = headView;
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

    public String getFromUsername() {
        return fromUsername;
    }

    public void setFromUsername(String fromUsername) {
        this.fromUsername = fromUsername;
    }

    public String getTargetUsername() {
        return targetUsername;
    }

    public void setTargetUsername(String targetUsername) {
        this.targetUsername = targetUsername;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getHeadView() {
        return headView;
    }

    public void setHeadView(String headView) {
        this.headView = headView;
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

    public void setTime(long time) {
        this.time = time+"";
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
