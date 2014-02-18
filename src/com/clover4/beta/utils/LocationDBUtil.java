package com.clover4.beta.utils;

import java.io.File;
import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 访问每个教学楼位置信息的数据库
 *
 */
public class LocationDBUtil {

	String dbPath;
	File dbFile;
	SQLiteDatabase mDB;
	
	public LocationDBUtil() {
		// TODO Auto-generated constructor stub
		dbPath = android.os.Environment.getExternalStorageDirectory()+"/clover/location.db";
		dbFile= new File(dbPath);
		if (dbFile.exists()) mDB = SQLiteDatabase.openOrCreateDatabase(dbPath, null);
	}

	public boolean isDBexists() {
		return dbFile.exists();
	}
	
	/**
	 * 
	 * @param name
	 * @return 返回name教学楼的longitude
	 */
	public ArrayList<Double> getLon(String name){
		ArrayList<Double> result = new ArrayList<Double>();
		
		name = "\"" + name + "\"";
		Cursor mCursor = mDB.query("location", 
				new String []{"lon"}, "name = "+name, null, null, null, null, null);
		Double lon;
		while (mCursor.moveToNext()){
			
			lon = Double.parseDouble(mCursor.getString((mCursor.getColumnIndex("lon"))));
			result.add(lon);
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param name
	 * @return 返回name教学楼的latitude
	 */
	public ArrayList<Double> getLat(String name){
		ArrayList<Double> result = new ArrayList<Double>();
		
		name = "\"" + name + "\"";
		Cursor mCursor = mDB.query("location", 
				new String []{"lat"}, "name = "+name, null, null, null, null, null);
		Double lon;
		while (mCursor.moveToNext()){
			
			lon = Double.parseDouble(mCursor.getString((mCursor.getColumnIndex("lat"))));
			result.add(lon);
		}
		
		return result;
	}
}
