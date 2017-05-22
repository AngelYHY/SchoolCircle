package com.myschool.schoolcircle.entity;

import java.io.Serializable;
import java.util.List;

public class Tb_dynamic implements Serializable{
	
	private int _id;
	private String avatar;
	private String username;
	private String realName;
	private String textContent;
	private String imageList;
	private List<String> images;
	private String datetime;
	private int likeNum;
	private int commentNum;
	private String schoolName;
	
	public Tb_dynamic() {
		
	}
	
	public Tb_dynamic(int _id, String avatar, String username,String realName,
			String textContent, String imageList, String datetime, int likeNum,
			String schoolName) {
		super();
		this._id = _id;
		this.avatar = avatar;
		this.username = username;
		this.realName = realName;
		this.textContent = textContent;
		this.imageList = imageList;
		this.datetime = datetime;
		this.likeNum = likeNum;
		this.schoolName = schoolName;
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
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getTextContent() {
		return textContent;
	}
	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}
	public String getImageList() {
		return imageList;
	}
	public void setImageList(String imageList) {
		this.imageList = imageList;
	}
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public int getLikeNum() {
		return likeNum;
	}
	public void setLikeNum(int likeNum) {
		this.likeNum = likeNum;
	}
	public String getSchoolName() {
		return schoolName;
	}
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}
	
	public int getCommentNum() {
		return commentNum;
	}
	
	public void setCommentNum(int commentNum) {
		this.commentNum = commentNum;
	}
	
	public List<String> getImages() {
		return images;
	}
	
	public void setImages(List<String> images) {
		this.images = images;
	}

	@Override
	public String toString() {
		return "Tb_dynamic{" +
				"_id=" + _id +
				", avatar='" + avatar + '\'' +
				", username='" + username + '\'' +
				", realName='" + realName + '\'' +
				", textContent='" + textContent + '\'' +
				", imageList='" + imageList + '\'' +
				", images=" + images +
				", datetime='" + datetime + '\'' +
				", likeNum=" + likeNum +
				", commentNum=" + commentNum +
				", schoolName='" + schoolName + '\'' +
				'}';
	}
}
