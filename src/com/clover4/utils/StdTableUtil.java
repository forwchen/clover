package com.clover4.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.R.bool;
import android.R.integer;

class ClassItem{
	
	public int week;
	public String startunit,units,code,name,classroom,teacher;
	
	ClassItem(int classweek, String classstart, String classunit, 
			String classcode, String classname, String classroom, String classteacher)
	{
		this.week = classweek;
		this.startunit = classstart;
		this.units = classunit;
		this.code = classcode;
		this.name = classname;
		this.classroom = classroom;
		this.teacher = classteacher;
	}
}


public class StdTableUtil {
	
	final private String arg = "arranges[index]";
	private int[] NumofClass = new int[8];
	private ClassItem[][] classitem = new ClassItem[8][14];

	public StdTableUtil(StringBuffer str){
		
		for (int i = 0; i < 8; i++) NumofClass[i] = 0;
	
		
		int index = 0;
		while ((index = str.indexOf(arg)) != -1){
			
			str.delete(0, index);
			str.delete(0, str.indexOf(";")+1);
			str.delete(0, str.indexOf("=")+1);
			int classweek = toInt(str.substring(0, str.indexOf(";")));
			str.delete(0, str.indexOf("=")+1);
			String classstart = str.substring(0, str.indexOf(";"));
			str.delete(0, str.indexOf("=")+1);
			String classunit = str.substring(0, str.indexOf(";"));
			str.delete(0, str.indexOf("=")+1);
			String classcode = (String) str.subSequence(1, str.indexOf("<"));
			str.delete(0, str.indexOf(">")+1);
			String classname = (String) str.substring(0, str.indexOf("<"));
			str.delete(0, str.indexOf(">")+1);
			String classroom = (String) str.substring(0, str.indexOf("("));
			str.delete(0, str.indexOf("(")+1);
			String classteacher = (String) str.substring(0, str.indexOf(""));
			str.delete(0, str.indexOf(";")+1);
			
			NumofClass[classweek]++;
			classitem[classweek][NumofClass[classweek]] = 
					new ClassItem(classweek, classstart, classunit, classcode, classname, classroom, classteacher);
			
			//debug
			System.out.println("day:"+classweek+" "+classstart+" "+classunit+" "+classcode+" "+classname+" "+classroom+" "+classteacher);
		}
		
		System.out.println("Parsing Done");
	}
	
	private int toInt(String str){
		int temp = 0;
		for (int i = 0; i < str.length(); i++)
			temp = temp*10+(str.charAt(i)-'0');
		return temp;
	}

	public void storeTable(){
		int havingclass[] = new int[25];
		for (int week = 1; week <=5; week++){
			
			for (int i = 0; i < 20; i++) havingclass[i] = 0;
			for (int i = 1; i <= NumofClass[week]; i++)
				for (int j = toInt(classitem[week][i].startunit); 
						j < toInt(classitem[week][i].startunit) + toInt(classitem[week][i].units); j++)
					havingclass[j] = i;
			havingclass[5] = 555555;
			havingclass[10] = 101010;
			
			try {
				FileWriter fw;
				fw = new FileWriter(android.os.Environment.getExternalStorageDirectory()
						+"/clover"+"/day"+String.valueOf(week)+".txt");
				BufferedWriter bw = new BufferedWriter(fw);
				
				int i = 1 ,j = 1;
				while (i <= 14 && j <= 14){
					while (havingclass[j] == havingclass[i] && j <= 14) j++;
					if (havingclass[i] > 0 && havingclass[i] < 100){
						bw.write("1\n");
						bw.write(classitem[week][havingclass[i]].startunit + "\n");
						bw.write(classitem[week][havingclass[i]].units + "\n");
						bw.write(classitem[week][havingclass[i]].code + "\n");
						bw.write(classitem[week][havingclass[i]].name + "\n");
						bw.write(classitem[week][havingclass[i]].classroom + "\n");
					}
					else{
						bw.write("0\n");
						bw.write(String.valueOf(i) + "\n");
						bw.write(String.valueOf(j - i) + "\n");
					}
					i = j;
				}
				
				
				bw.flush();
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
}
