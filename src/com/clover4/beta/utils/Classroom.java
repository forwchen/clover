package com.clover4.beta.utils;

/**
 * 记录教室使用情况
 *
 */
public class Classroom 
{
	///教室名称
	public String name;
	///位于哪座教学楼
	public int which;
	///教室使用情况，0空，1占用
	public int[] used;
	///字符串形式表示
	public String used_str;
	///二进制形式表示
	public int stat;
	
	public Classroom(String NAME)
	{
		name = NAME;
		which = 0;
		used_str = "";
		stat = 0;
		used = new int[15];
		for (int i = 0; i < 14; i++) used[i] = 0;
	}
	
	public void setUsed(int unit,int flag)
	{
		used[unit - 1] = flag;
	}
	
	public void toStr(){

		for (int i = 0; i < 14; i++)
			if (used[i] == 1) used_str = used_str + "1";
			else used_str = used_str +"0";
	}
	
}
