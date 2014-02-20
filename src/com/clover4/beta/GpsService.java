package com.clover4.beta;

import android.app.Service;
import android.content.Context;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

/**
 * 后台GPS服务
 *
 */
public class GpsService extends Service implements LocationListener {

	private final Context mContext;

	boolean isGPSEnabled = false;

	Location location;
	double latitude = 0;
	double longitude = 0;
	boolean updated = false;

	///更新位置的最小间隔距离
	private static final long MIN_DISTANCE = 5; 
	///更新位置的最小间隔时间
	private static final long MIN_TIME = 1000 * 2;

	protected LocationManager locationManager;

	public GpsService(Context context) {
		this.mContext = context;
		locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
	}

	/**
	 * 开始使用GPS
	 * @return
	 */
	public Location getLocation() {
		
		locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
		isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (isGPSEnabled){
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER,
					MIN_TIME,
					MIN_DISTANCE, this);
		}

		return location;
	}

	/**
	 * 停止使用GPS
	 */
	public void stopUsingGPS(){
		if(locationManager != null){
			locationManager.removeUpdates(GpsService.this);
		}		
	}


	@Override
	/**
	 * 接收到位置更新，此时关闭GPS
	 */
	public void onLocationChanged(Location location) {
		this.location = location;
		this.latitude = location.getLatitude();
		this.longitude = location.getLongitude();
		this.updated = true;
		
		Intent broadcastIntent = new Intent("com.clover.LocationChangedBroadcast");
		broadcastIntent.putExtra("lat", location.getLatitude());
		broadcastIntent.putExtra("lon", location.getLongitude());
		mContext.sendBroadcast(broadcastIntent);

		this.stopUsingGPS();
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

}
