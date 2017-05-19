package com.myschool.schoolcircle.entity;

import java.io.Serializable;

public class Tb_school implements Serializable{

	private int _id;
	private String schoolName;
	
	public Tb_school() {

	}

	public Tb_school(int _id, String schoolName) {
		super();
		this._id = _id;
		this.schoolName = schoolName;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}
	
}
