package com.clover4.beta.utils;

import java.util.ArrayList;

import android.database.CursorJoiner.Result;

public class GPSUtil {

	private final double EARTH_RADIUS = 6378137.0;
	private Constants c = new Constants();
	private LocationDBUtil mDbUtil;


	public GPSUtil() {
		// TODO Auto-generated constructor stub
		mDbUtil = new LocationDBUtil();
	}

	
	public double gps2m(double lng_a, double lat_a, double lng_b, double lat_b) {
		double radLat1 = (lat_a * Math.PI / 180.0);
		double radLat2 = (lat_b * Math.PI / 180.0);
		double a = radLat1 - radLat2;
		double b = (lng_a - lng_b) * Math.PI / 180.0;
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}
	
	
	public ArrayList<Integer> getClosest(double lon, double lat){
		ArrayList<Integer> result = new ArrayList<Integer>();
		
		int [] index = new int[7];
		double [] dis = new double[7];
		
		for (int i = 0; i < 7; i++){
			ArrayList<Double> mlon = mDbUtil.getLon(c.CLASSBUILD[i]);
			ArrayList<Double> mlat = mDbUtil.getLat(c.CLASSBUILD[i]);
			dis[i] = 1e20;
			for (int j = 0; j < mlon.size(); j++){
				double d = gps2m(lon, lat, mlon.get(j), mlat.get(j));
				if (d < dis[i]) dis[i] = d;
			}
			index[i] = i;
		}
		
		for (int i = 0; i < 7; i++)
			for (int j = i + 1; j < 7; j++)
				if (dis[i] > dis[j]){
					double tmp = dis[i];
					dis[i] = dis[j];
					dis[j] = tmp;
					
					int temp = index[i];
					index[i] = index[j];
					index[j] = temp;
				}
		for (int i = 0; i < 7; i++) {
			result.add(index[i]);
			System.out.println(dis[i]);
		}
		
		return result;
	}
	
	
	
}
