package com.clover4.beta.utils;

public class Classroom 
{
	public String name;
	public int which, stat;
	public int[] used = new int[15];

	
	public Classroom(String NAME)
	{
		name = NAME;
		which = 0;
		for (int i = 0; i < 14; i++) used[i] = 0;
	}
	
	public void setUsed(int loc,int opt)
	{
		used[loc - 1] = opt;
	}
	
}
