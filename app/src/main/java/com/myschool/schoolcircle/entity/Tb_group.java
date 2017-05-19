package com.myschool.schoolcircle.entity;

import java.io.Serializable;

/**
 * Created by Mr.R on 2016/8/10.
 */
public class Tb_group implements Serializable {
    private int id;
    private long groupId;
    private String creatorUsername;
    private String groupImage;
    private String groupName;
    private String groupDescribe;
    private String groupSchoolName;
    private String groupGrade;
    private String groupSpecialty;
    private int joinNum;
    private String label;
    private String createTime;

    public Tb_group() {

    }

    public Tb_group(int id, long groupId,String creatorUsername,String groupImage,
                    String groupName, String groupDescribe, String groupSchoolName,
                    String groupGrade, String groupSpecialty,
                    int joinNum, String label,String createTime) {
        this.id = id;
        this.groupId = groupId;
        this.creatorUsername = creatorUsername;
        this.groupImage = groupImage;
        this.groupName = groupName;
        this.groupDescribe = groupDescribe;
        this.groupSchoolName = groupSchoolName;
        this.groupGrade = groupGrade;
        this.groupSpecialty = groupSpecialty;
        this.joinNum = joinNum;
        this.label = label;
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getCreatorUsername() {
        return creatorUsername;
    }

    public void setCreatorUsername(String creatorUsername) {
        this.creatorUsername = creatorUsername;
    }

    public String getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(String groupImage) {
        this.groupImage = groupImage;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDescribe() {
        return groupDescribe;
    }

    public void setGroupDescribe(String groupDescribe) {
        this.groupDescribe = groupDescribe;
    }

    public int getJoinNum() {
        return joinNum;
    }

    public String getGroupSchoolName() {
        return groupSchoolName;
    }

    public void setGroupSchoolName(String groupSchoolName) {
        this.groupSchoolName = groupSchoolName;
    }

    public String getGroupGrade() {
        return groupGrade;
    }

    public void setGroupGrade(String groupGrade) {
        this.groupGrade = groupGrade;
    }

    public String getGroupSpecialty() {
        return groupSpecialty;
    }

    public void setGroupSpecialty(String groupSpecialty) {
        this.groupSpecialty = groupSpecialty;
    }

    public void setJoinNum(int joinNum) {
        this.joinNum = joinNum;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
