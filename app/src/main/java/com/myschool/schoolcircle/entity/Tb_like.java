package com.myschool.schoolcircle.entity;

public class Tb_like {
	
	private int _id;
	private int targetId;
	private String username;
	private String targetType;
	
	public Tb_like() {
		
	}
	
	public Tb_like(int _id, int targetId, String username, 
			String targetType) {
		super();
		this._id = _id;
		this.targetId = targetId;
		this.username = username;
		this.targetType = targetType;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public int getTargetId() {
		return targetId;
	}

	public void setTargetId(int targetId) {
		this.targetId = targetId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}
	
}
