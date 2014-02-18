package com.clover4.beta.utils;

/**
 * 课程信息，用于TableAdapter
 *
 */
public class ClassTableItem {
	
	public int type;
	public int startunit;
	public int units;
	public String code;
	public String name;
	public String classroom;
	
	public ClassTableItem(){
		type = 0;
		startunit = 0;
		units = 0;
		code = "";
		name = "";
		classroom = "";
	}
	
}