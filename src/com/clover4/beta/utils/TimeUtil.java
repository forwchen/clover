package com.clover4.beta.utils;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import android.R.integer;




public class TimeUtil {
	private SimpleDateFormat mSimpleDateFormat;
	private Date mDate;
	private Calendar mCalendar;
	public String date;
	public String time;
	public int dayofweek;
	
	
	public TimeUtil() {
		// TODO Auto-generated constructor stub
		mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		mDate = new Date(System.currentTimeMillis());
		date = mSimpleDateFormat.format(mDate).substring(0, 10);
		time = mSimpleDateFormat.format(mDate).substring(11, 16);
		
		mCalendar = Calendar.getInstance();
		mCalendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		mCalendar.setTime(mDate);
		dayofweek = mCalendar.get(mCalendar.DAY_OF_WEEK);
		dayofweek = (dayofweek + 6) % 7;
	}
	
	public String getTime(){
		mDate = new Date(System.currentTimeMillis());
		time = mSimpleDateFormat.format(mDate).substring(11, 16);
		return time;
	}
	
	public double calc(String t1, String t2) {
		double result = 0;
		
		int h1 = Integer.parseInt(t1.substring(0,2));
		int h2 = Integer.parseInt(t2.substring(0,2));
		int m1 = Integer.parseInt(t1.substring(3,5));
		int m2 = Integer.parseInt(t2.substring(3,5));
		
		if (h1 > h2){
			result = (h1 - h2 - 1) + (60 - m2 + m1) * 1.0 / 60;
		}
		else if (h1 < h2){
			result = (h2 - h1 - 1) + (60 - m1 + m2) * 1.0 / 60;
		}
		else {
			result = Math.abs(m1 - m2) * 1.0 / 60;
		}
		
		return result;
		
	}
}
