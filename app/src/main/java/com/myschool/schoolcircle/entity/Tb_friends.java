package com.myschool.schoolcircle.entity;

public class Tb_friends {

	private String friend_name;
	private String user_name;
	
	public Tb_friends() {
		
	}
	
	public Tb_friends(String friend_name, String user_name) {
		this.friend_name = friend_name;
		this.user_name = user_name;
	}
	
	public String getFriend_name() {
		return friend_name;
	}
	
	public void setFriend_name(String friend_name) {
		this.friend_name = friend_name;
	}
	
	public String getUser_name() {
		return user_name;
	}
	
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
}
