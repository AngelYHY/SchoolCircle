package com.myschool.schoolcircle.entity;

import java.io.Serializable;

public class Tb_activity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String avatar;
	private String username;
	private String theme;
	private String activityDescribe;
	private String picture;
	private String sponsor;
	private int number;
	private String place;
	private String activityBegin;
	private String activityEnd;
	private int commentNum;
	private int likeNum;
	private int joinNum;
	private String sDatetime;
	private String state;
	private String schoolName;
	
	public Tb_activity() {}
	
	public Tb_activity(String id, String username, String theme,
			String activityDescribe, String picture, String sponsor, 
			int number, String place,String activityBegin, String activityEnd,
			int likeNum, int joinNum,String sDatetime,String state) {
		super();
		this.id = id;
		this.username = username;
		this.theme = theme;
		this.activityDescribe = activityDescribe;
		this.picture = picture;
		this.sponsor = sponsor;
		this.number = number;
		this.place = place;
		this.activityBegin = activityBegin;
		this.activityEnd = activityEnd;
		this.likeNum = likeNum;
		this.joinNum = joinNum;
		this.sDatetime = sDatetime;
		this.state = state;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
	
	public String getsDatetime() {
		return sDatetime;
	}
	
	public void setsDatetime(String sDatetime) {
		this.sDatetime = sDatetime;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getActivityDescribe() {
		return activityDescribe;
	}

	public void setActivityDescribe(String activityDescribe) {
		this.activityDescribe = activityDescribe;
	}
	
	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getSponsor() {
		return sponsor;
	}

	public void setSponsor(String sponsor) {
		this.sponsor = sponsor;
	}
	
	public int getCommentNum() {
		return commentNum;
	}
	
	public void setCommentNum(int commentNum) {
		this.commentNum = commentNum;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getActivityBegin() {
		return activityBegin;
	}
	
	public void setActivityBegin(String activityBegin) {
		this.activityBegin = activityBegin;
	}
	
	public String getActivityEnd() {
		return activityEnd;
	}
	
	public void setActivityEnd(String activityEnd) {
		this.activityEnd = activityEnd;
	}
	
	public int getLikeNum() {
		return likeNum;
	}

	public void setLikeNum(int likeNum) {
		this.likeNum = likeNum;
	}

	public int getJoinNum() {
		return joinNum;
	}

	public void setJoinNum(int joinNum) {
		this.joinNum = joinNum;
	}
	
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public String getSchoolName() {
		return schoolName;
	}
	
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	@Override
	public String toString() {
		return "Tb_activity{" +
				"id='" + id + '\'' +
				", avatar='" + avatar + '\'' +
				", username='" + username + '\'' +
				", theme='" + theme + '\'' +
				", activityDescribe='" + activityDescribe + '\'' +
				", picture='" + picture + '\'' +
				", sponsor='" + sponsor + '\'' +
				", number=" + number +
				", place='" + place + '\'' +
				", activityBegin='" + activityBegin + '\'' +
				", activityEnd='" + activityEnd + '\'' +
				", commentNum=" + commentNum +
				", likeNum=" + likeNum +
				", joinNum=" + joinNum +
				", sDatetime='" + sDatetime + '\'' +
				", state='" + state + '\'' +
				", schoolName='" + schoolName + '\'' +
				'}';
	}
}
