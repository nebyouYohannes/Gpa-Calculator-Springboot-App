package com.example.gpaCalculator.model;

public class Course {
	private String Code;
	private String Name;
	private int creditHr;
	private String grade;
	public String getCode() {
		return Code;
	}
	public void setCode(String code) {
		Code = code;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public int getCreditHr() {
		return creditHr;
	}
	public void setCreditHr(int creditHr) {
		this.creditHr = creditHr;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public Course(String code, String name, int creditHr, String grade) {
		super();
		Code = code;
		Name = name;
		this.creditHr = creditHr;
		this.grade = grade;
	}
	public Course() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}

