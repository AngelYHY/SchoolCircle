package com.myschool.schoolcircle.entity;

/**
 * Created by Mr.R on 2016/7/12.
 */
public class Person {
    private String name;
    private String signature;
    private int headId;

    public Person(String name, String signature, int headId) {
        this.name = name;
        this.signature = signature;
        this.headId = headId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getHeadId() {
        return headId;
    }

    public void setHeadId(int headId) {
        this.headId = headId;
    }
}
