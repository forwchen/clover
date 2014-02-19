package com.clover4.beta.utils;

/**
 * 课程表元素，用于TableAdapter
 *
 */
public class ClassTableItem {
	///0无课，1有课
	public int type;
	///开始时间段
	public int startunit;
	///持续时间
	public int units;
	///课程代号
	public String code;
	///课程名称
	public String name;
	///上课教室
	public String classroom;
	///教师
	public String teacher;
	
	public ClassTableItem(){
		type = 0;
		startunit = 0;
		units = 0;
		code = "";
		name = "";
		classroom = "";
	}
	
}