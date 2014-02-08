package com.clover4.beta.utils;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;




public class TimeUtil {
	private SimpleDateFormat mSimpleDateFormat;
	private Date mDate;
	private Calendar mCalendar;
	public String date;
	public int dayofweek;
	
	
	public TimeUtil() {
		// TODO Auto-generated constructor stub
		mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		mDate = new Date(System.currentTimeMillis());
		date = mSimpleDateFormat.format(mDate);
		
		mCalendar = Calendar.getInstance();
		mCalendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		mCalendar.setTime(mDate);
		dayofweek = mCalendar.get(mCalendar.DAY_OF_WEEK);
		dayofweek = (dayofweek + 6) % 7;
		
		dayofweek = 4;
	}
}
