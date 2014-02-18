package com.clover4.beta.utils;

/**
 * 教室空闲信息，用于ClassroomAdapter
 *
 */
public class ClassroomItem {
	///教室名称
	public String name;
	///3个String表示3个时段使用情况
	public String [] stat = new String[3];
	
	public ClassroomItem() {
		// TODO Auto-generated constructor stub
		
	}

}
