package com.clover4.beta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import com.clover4.beta.utils.Constants;
import com.clover4.beta.utils.EventItem;
import com.clover4.beta.utils.EventLoader;
import com.clover4.beta.utils.GPSUtil;
import com.clover4.beta.utils.SharedprefUtil;
import com.clover4.beta.utils.TimeUtil;


import android.R.bool;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;


/**
 * 后台GPS服务
 *
 */
public class GpsService extends Service implements LocationListener {

	private String BACK = "BACKGROUND";
	TimeUtil mTimeUtil;
	ConnectivityManager mConnectivityManager;
	NetworkInfo mWiFiNetworkInfo;
	SharedprefUtil mSharedprefUtil;
	EventLoader mEventLoader;
	GPSUtil mGpsUtil;
	Constants c;
	
	Location location;
	String isfree;
	double latitude = 0;
	double longitude = 0;
	WakeLock wakeLock= null;
	
	///更新位置的最小间隔距离
	private static final long MIN_DISTANCE = 5; 
	///更新位置的最小间隔时间
	private static final long MIN_TIME = 1000 * 2;
	///运行GPS的时间
	private static final long TIME_LIMIT = 5 * 1000;

	protected LocationManager locationManager;
	boolean isGPSEnabled;
	boolean isGPSAllowed;
	boolean isGPSStarted;
	boolean canGetLocation;
	boolean isEventGoing;
	boolean[] notified;
	ArrayList<EventItem> lectureList, actList;
	Handler mHandler;
	int curunit;
	
	public GpsService() {
		
	}

	public void onCreate(){
		isfree = new String();
		isGPSEnabled = false;
		isGPSAllowed = false;
		isGPSStarted = false;
		canGetLocation = false;
		isEventGoing = false;
		notified = new boolean[30];
		mConnectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		mTimeUtil = new TimeUtil();
		mGpsUtil = new GPSUtil();
		c = new Constants();
		mSharedprefUtil = new SharedprefUtil("setting", this);
		EventLoader mEventLoader = new EventLoader();
		lectureList = mEventLoader.loadEvent(1);
		actList = mEventLoader.loadEvent(0);
		
		AlarmManager mAlarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
		mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0, TIME_LIMIT * 30, null);
		
		Timer mTimer = new Timer();
		mTimer.schedule(new checkAllowGPS(), 0, 1000 * 60);
		mHandler = new Handler();	
		mHandler.postDelayed(turnGPS, 5 * 1000);
		super.onCreate();
	}
	
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(BACK, "STARTED");
		for (int i = 0; i < 30; i++) notified[i] = false;
	    return START_STICKY;
	}
	
	/**
	 * 开始使用GPS
	 * @return
	 */
	public void getLocation() {
		locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
		isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (isGPSEnabled){
			if (wakeLock == null){
				PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
				wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getCanonicalName());
				wakeLock.acquire();
			}
			isGPSStarted = true;
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER,
					MIN_TIME,
					MIN_DISTANCE, this);
		}

	}
	
	
	/**
	 * 打开或关闭GPS
	 *
	 */
	Runnable turnGPS= new Runnable() {
		public void run() {
			mHandler.postDelayed(this, 10 * 1000);
			if (isGPSAllowed){
				if (!isGPSStarted) getLocation();
				Log.d(BACK, "is gps allowed");
			}
			else {
				if (isGPSStarted) {
					stopUsingGPS();
					isGPSStarted = false;
				}
			}
		}
	};

	/**
	 * 检查允许GPS运行的条件
	 *
	 */
	public class checkAllowGPS extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			isfree = mSharedprefUtil.readString("ISFREE");
			curunit = mTimeUtil.getCurrentUnit();
			if (isfree.charAt(curunit) == '1'){
				isGPSAllowed = true;
				Log.d(BACK, "is free");
			}
			else Log.d(BACK, "is not free");
				
			
			
			isEventGoing = checkEventWithTime();
			if (isEventGoing){
				isGPSAllowed = true;
				Log.d(BACK, "is event going");
			}
			else Log.d(BACK,"no event going");
			 
			  
			mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
			if (mWiFiNetworkInfo != null) {  
				if (mWiFiNetworkInfo.isAvailable()) isGPSAllowed = false; 
			} 
			else Log.d(BACK, "not indoor");
						
		}
		
	}
	
	/**
	 * 检查当前时间点附近有无活动
	 * @return
	 */
	public boolean checkEventWithTime(){
		boolean result = false;
		String nowtime = mTimeUtil.getTime();
		EventItem mItem;
		String stime;
		
		for (int i = 0; i < lectureList.size(); i++){
			mItem = lectureList.get(i);
			stime = mItem.stime.substring(11,16);
			if (mTimeUtil.calc(nowtime, stime) <= 0.25){
				result = true;
				break;
			}
		}
		
		for (int i = 0; i < actList.size(); i++){
			mItem = actList.get(i);
			stime = mItem.stime.substring(11,16);
			if (mTimeUtil.calc(nowtime, stime) <= 0.25){
				result = true;
				break;
			}
		}
	
		return result;
	}
	
	
	/**
	 * 检查当前位置附近有无活动
	 * @return
	 */
	public int checkEventWithLocation(){
		
		ArrayList<Double> dist = mGpsUtil.getDist(longitude, latitude);
		EventItem mItem;
		for (int i = 0; i < lectureList.size(); i++){
			mItem = lectureList.get(i); 
			for (int j = 0; j < 7; j++)
				if (mItem.building.equals(c.BUILDING[j]) && dist.get(j) < 80) 
					return (i * 2 + 1);
		}
		
		for (int i = 0; i < actList.size(); i++){
			mItem = actList.get(i);
			for (int j = 0; j < 7; j++)
				if (mItem.building.equals(c.BUILDING[j]) && dist.get(j) < 80) 
					return (i * 2);
		}
		
		return -1;
	}
	
	/**
	 * 提醒活动
	 * @param mItem 活动
	 */
	public void mNotify(EventItem mItem) {
		
		NotificationManager mNotificationManager = 
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Intent lectureIntent = new Intent(this,ShowLecture.class);
		lectureIntent.putExtra("Building", mItem.building);
		PendingIntent mPendingIntent= 
				PendingIntent.getActivity(this, 0, lectureIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		
		String title = "附近 " + mItem.place;
		if (mItem.type == 1) title = title + " 有讲座";
		else title = title + " 有活动";
		
		Notification mNotification = new Notification.Builder(this).
				setSmallIcon(R.drawable.ic_launcher).
				setContentTitle(title). 
				setContentText("点击查看").
				setContentIntent(mPendingIntent).
				setAutoCancel(true).
				setDefaults(3).
				build();
		
		mNotificationManager.notify(202, mNotification);
	}
	
	/**
	 * 停止使用GPS
	 * 
	 */
	public void stopUsingGPS(){
		if(locationManager != null){
			locationManager.removeUpdates(GpsService.this);
		}		
		
		if (wakeLock !=null && wakeLock.isHeld()) {
			wakeLock.release();
			wakeLock = null;
		}
	}


	@Override
	/**
	 * 接收到位置更新
	 * 
	 */
	public void onLocationChanged(Location location) {
		this.location = location;
		this.latitude = location.getLatitude();
		this.longitude = location.getLongitude();
		
		mSharedprefUtil.modifyLong("time", System.currentTimeMillis());
		mSharedprefUtil.modifyString("latitude", String.valueOf(latitude));
		mSharedprefUtil.modifyString("longitude", String.valueOf(longitude));
		if (isEventGoing){
			int event = checkEventWithLocation();
			if (event != -1 && !notified[event]){
				EventItem mItem = (event & 1) == 1 ? lectureList.get(event / 2) : actList.get(event / 2);
				notified[event] = true;
				mNotify(mItem);
			}
		}
		
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public void onDestroy() {
		super.onDestroy();
	}


}
