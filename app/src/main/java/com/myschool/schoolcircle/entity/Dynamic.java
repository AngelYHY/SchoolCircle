package com.myschool.schoolcircle.entity;

/**
 * Created by Mr.R on 2016/7/13.
 */
public class Dynamic {
    private int headId;
    private String text;

    public Dynamic(int headId, String text) {
        this.headId = headId;
        this.text = text;
    }

    public int getHeadId() {
        return headId;
    }

    public void setHeadId(int headId) {
        this.headId = headId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
