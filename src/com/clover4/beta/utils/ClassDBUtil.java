package com.clover4.beta.utils;

import java.io.File;
import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ClassDBUtil {

	String dbPath;
	File dbFile;
	SQLiteDatabase mDB;

	
	public ClassDBUtil() {
		// TODO Auto-generated constructor stub
		dbPath = android.os.Environment.getExternalStorageDirectory()+"/clover/info.db";
		dbFile= new File(dbPath);
		if (dbFile.exists()) mDB = SQLiteDatabase.openOrCreateDatabase(dbPath, null);
	}
	
	
	public boolean isDBexists() {
		return dbFile.exists();
	}
	
	/**
	 * 查询某教学楼一段时间内空闲的教室
	 * @param table 表名，即教学楼名称
	 * @param start_unit 开始时间段
	 * @param end_unit 结束时间段
	 * @return 教室名列表
	 */
	public ArrayList<String> mQuery(String table, int start_unit, int end_unit) {
		ArrayList<String> result = new ArrayList<String>();
		
		int mask = 0;
		for (int i = start_unit; i <= end_unit; i++) mask += (1<<i);
		String MASK = String.valueOf(mask);
		
		Cursor mCursor = mDB.query(table, 
				new String []{"name"}, "((~stat) & "+MASK+") = "+MASK, null, null, null, null, null);
		String name;
		while (mCursor.moveToNext()){
			
			name = mCursor.getString(mCursor.getColumnIndex("name"));
			result.add(name);
		}
		
		return result;
	}

}
