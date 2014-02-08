package com.clover4.beta.utils;

public class Classroom 
{
	public String name, str;
	public int which, stat;
	public int[] used = new int[15];

	
	public Classroom(String NAME)
	{
		name = NAME;
		which = 0;
		str = "";
		for (int i = 0; i < 14; i++) used[i] = 0;
	}
	
	public void setUsed(int loc,int opt)
	{
		used[loc - 1] = opt;
	}
	
	public void toStr(){

		for (int i = 0; i < 14; i++)
			if (used[i] == 1) str = str + "1";
			else str = str +"0";
	}
	
}
