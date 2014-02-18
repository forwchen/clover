package com.clover4.beta.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 管理SharedPreferences
 *
 */
public class SharedprefUtil {
	
	Context mContext;
	String FILE;
	


	public SharedprefUtil(String filename, Context context){
		FILE = filename;
		mContext = context;
	}
	
	public Long readLong(String key) {

		SharedPreferences mSharedPreferences = mContext.getSharedPreferences(FILE,
				mContext.MODE_PRIVATE);
		if (mSharedPreferences == null) return 0l;
		Long result = mSharedPreferences.getLong(key, 0);
		return result;
	}
	
	
	public String readString(String key) {

		SharedPreferences mSharedPreferences = mContext.getSharedPreferences(FILE,
				mContext.MODE_PRIVATE);
		if (mSharedPreferences == null) return "";
		String result =  mSharedPreferences.getString(key, "");
		return result;
	}
	
	public void writeString(String key, String value) {
		
		SharedPreferences mSharedPreferences = mContext.getSharedPreferences(FILE,
				mContext.MODE_APPEND);
		SharedPreferences.Editor mEditor = mSharedPreferences.edit();
		mEditor.putString(key, value);
		mEditor.commit();
		
	}
	
	public void writeLong(String key, Long value) {
		
		SharedPreferences mSharedPreferences = mContext.getSharedPreferences(FILE,
				mContext.MODE_APPEND);
		SharedPreferences.Editor mEditor = mSharedPreferences.edit();
		mEditor.putLong(key, value);
		mEditor.commit();
		
	}
	
	public void modifyString(String key, String value) {
		SharedPreferences mSharedPreferences = mContext.getSharedPreferences(FILE,
				mContext.MODE_APPEND);
		SharedPreferences.Editor mEditor = mSharedPreferences.edit();
		mEditor.remove(key);
		mEditor.putString(key, value);
		mEditor.commit();
	}
	
	
	public void modifyLong(String key, Long value) {
		SharedPreferences mSharedPreferences = mContext.getSharedPreferences(FILE,
				mContext.MODE_APPEND);
		SharedPreferences.Editor mEditor = mSharedPreferences.edit();
		mEditor.remove(key);
		mEditor.putLong(key, value);
		mEditor.commit();
	}
}
