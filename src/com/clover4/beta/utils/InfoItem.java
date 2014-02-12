package com.clover4.beta.utils;

public class InfoItem {

	public String name;
	public String[] floor = new String[5];
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
