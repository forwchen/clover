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
	
	public ArrayList<ClassroomItem> mQuery(String table, int timeunit) {
		ArrayList<ClassroomItem> result = new ArrayList<ClassroomItem>();
		
		Cursor mCursor = mDB.query(table, new String []{"name","stat"}, "(stat & ?) > 0", new String[] {"9216"} , null, null, null, null);
		while (mCursor.moveToNext()){
			ClassroomItem mItem = new ClassroomItem();
			mItem.name = mCursor.getString(mCursor.getColumnIndex("name"));
			result.add(mItem);
		}
		
		return result;
	}

}
