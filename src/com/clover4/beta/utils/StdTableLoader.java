package com.clover4.beta.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;




/**
 * 从存储卡中加载课程表信息
 *
 */
public class StdTableLoader {
	
	ArrayList<ClassTableItem> mArrayList;
	Constants mConstants;
	String Tablefile;
	TimeUtil mTimeUtil;
	
	public StdTableLoader(){
		mArrayList = new ArrayList<ClassTableItem>();
		mConstants = new Constants();
		mTimeUtil = new TimeUtil();
		Tablefile = android.os.Environment.getExternalStorageDirectory()
				+"/clover/day"+String.valueOf(mTimeUtil.getdayofweek())+".txt";
	}
	
	/**
	 * 从存储卡加载课程表
	 * @return 课程表元素列表
	 */
	public ArrayList<ClassTableItem> getTable(){
		
		try {
			FileReader fr = new FileReader(Tablefile);
			BufferedReader br = new BufferedReader(fr);
			
			String line = "null";
			
			while ((line = br.readLine()) != null){

				ClassTableItem ctable = new ClassTableItem();
				if (Integer.parseInt(line) == 0){
					ctable.type = 0;
					ctable.startunit = Integer.parseInt(br.readLine());
					ctable.units = Integer.parseInt(br.readLine());
				}
				else{
					ctable.type = 1;
					ctable.startunit = Integer.parseInt(br.readLine());
					ctable.units = Integer.parseInt(br.readLine());
					ctable.code = br.readLine();
					ctable.name = br.readLine();
					ctable.classroom = br.readLine();
				}

				mArrayList.add(ctable);

			}

			br.close();
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		return mArrayList;
	}
}
