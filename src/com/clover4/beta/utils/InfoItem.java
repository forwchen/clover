package com.clover4.beta.utils;

/**
 * 记录一个教学楼内空教室个数,用于InfoAdapter
 *
 */
public class InfoItem {

	///教学楼名
	public String name;
	///每个楼层的空教室，字符串形式
	public String[] floor = new String[5];
	///每个楼层的空教室数量
	public int [] count = new int[5];
	
	public InfoItem() {
		// TODO Auto-generated constructor stub
		for (int i = 0; i < 5; i++){
			floor[i] = "";
			count[i] = 0;
		}
	}
	
	public boolean isEmpty(){
		for (int i = 1; i < 5; i++)
			if (count[i] > 0) return false;
		return true;
	}

}
