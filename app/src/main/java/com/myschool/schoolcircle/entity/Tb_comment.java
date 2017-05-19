package com.myschool.schoolcircle.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Mr.R on 2016/8/19.
 */
public class Tb_comment implements Serializable{

    private int _id;
    private String avatar;
    private String realName;
    private String targetName;
    private String targetUserName;
    private String username;
    private String textContent;
    private String datetime;
    private int parentId;
    private int childId;
    private String commentType;
    private List<Tb_comment> childComments;

    public Tb_comment() {

    }

    public Tb_comment(int _id, String avatar, String realName,
                      String username, String textContent,String datetime,
                      int parentId, int childId,String commentType) {
        this._id = _id;
        this.avatar = avatar;
        this.realName = realName;
        this.username = username;
        this.textContent = textContent;
        this.datetime = datetime;
        this.parentId = parentId;
        this.childId = childId;
        this.commentType = commentType;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
    
    public String getTargetName() {
		return targetName;
	}
    
    public String getTargetUserName() {
		return targetUserName;
	}
    
    public void setTargetUserName(String targetUserName) {
		this.targetUserName = targetUserName;
	}

    public void setTargetName(String targetName) {
		this.targetName = targetName;
	}
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getChildId() {
        return childId;
    }

    public void setChildId(int childId) {
        this.childId = childId;
    }
    
    public String getCommentType() {
		return commentType;
	}
    
    public void setCommentType(String commentType) {
		this.commentType = commentType;
	}
    
    public List<Tb_comment> getChildComments() {
		return childComments;
	}
    
    public void setChildComments(List<Tb_comment> childComments) {
		this.childComments = childComments;
	}
}
