package com.clover4.beta.utils;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;



/**
 * 日期和时间工具
 *
 */
public class TimeUtil {
	private SimpleDateFormat mSimpleDateFormat;
	private Date mDate;
	private Calendar mCalendar;
	private String date;
	private String time;
	private int dayofweek;
	
	
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
	
	public int getdayofweek() {
		dayofweek = 2;
		
		return dayofweek;
	}
	
	public String getyear() {
		return date.substring(0,4);
	}
	
	public String getmonth() {
		return date.substring(5,7);
	}
	
	public String getday() {
		return date.substring(8,10);
	}
	
	public String getdate() {
		date = "2013-12-17";
		
		return date;
	}
	
	public String getTime(){
		mDate = new Date(System.currentTimeMillis());
		time = mSimpleDateFormat.format(mDate).substring(11, 16);
		
		time = "18:30";
		
		return time;
	}
	/**
	 * 
	 * @param t1
	 * @param t2
	 * @return t1 与 t2 的时间间隔
	 */
	
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
	
	/**
	 * 
	 * @return 一天中所处的时间段
	 * 0 morning
	 * 1 midday
	 * 2 night
	 */
	public int getunit(){
		mDate = new Date(System.currentTimeMillis());
		time = mSimpleDateFormat.format(mDate).substring(11, 16);
		int h = Integer.parseInt(time.substring(0,2));
		if (h<=11) return 0;
		if (h<=16) return 1;
		return 2;
	}
	
	/**
	 * time compare
	 * @param t1
	 * @param t2
	 * @return t1 is earlier than t2
	 */
	public boolean lt(String t1, String t2){
		int h1 = Integer.parseInt(t1.substring(0,2));
		int h2 = Integer.parseInt(t2.substring(0,2));
		int m1 = Integer.parseInt(t1.substring(3,5));
		int m2 = Integer.parseInt(t2.substring(3,5));
		if (h1 < h2) return true;
		if (h1 > h2) return false;
		if (m1 <= m2) return true;
		return false;
	}
}
