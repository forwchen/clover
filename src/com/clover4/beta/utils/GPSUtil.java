package com.clover4.beta.utils;

import java.util.ArrayList;

/**
 * 工具类
 * 根据GPS信息计算距离
 *
 */
public class GPSUtil {

	private final double EARTH_RADIUS = 6378137.0;
	private Constants c = new Constants();
	private LocationDBUtil mDbUtil;

	public GPSUtil() {
		// TODO Auto-generated constructor stub
		mDbUtil = new LocationDBUtil();
	}

	/**
	 * 
	 * @param lng_a longitude A
	 * @param lat_a latitude A
	 * @param lng_b longitude B
	 * @param lat_b latitude B
	 * @return 两个经纬度坐标之间的距离
	 */
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
	
	/**
	 * 将所有教学楼按照离lon,lat点的距离排序返回
	 * @param lon longitude
	 * @param lat latitude
	 * @return 教学楼代号列表
	 */
	public ArrayList<Integer> getClosest(double lon, double lat){
		ArrayList<Integer> result = new ArrayList<Integer>();
		int [] index = new int[7];
		double [] dis = new double[7];
		
		for (int i = 0; i < 7; i++){
			ArrayList<Double> mlon = mDbUtil.getLon(c.BUILDING[i]);
			ArrayList<Double> mlat = mDbUtil.getLat(c.BUILDING[i]);
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
		}
		
		return result;
	}
	
	/**
	 * 计算所有教学楼离 lon,lat 点的距离
	 * @param lon longitude
	 * @param lat latitude
	 * @return 距离值列表
	 */
	public ArrayList<Double> getDist(double lon, double lat){
		ArrayList<Double> dist = new ArrayList<Double>();
		
		for (int i = 0; i < 7; i++){
			ArrayList<Double> mlon = mDbUtil.getLon(c.BUILDING[i]);
			ArrayList<Double> mlat = mDbUtil.getLat(c.BUILDING[i]);
			double dis = 1e20;
			for (int j = 0; j < mlon.size(); j++){
				double d = gps2m(lon, lat, mlon.get(j), mlat.get(j));
				if (d < dis) dis = d;
			}
			dist.add(dis);
		}
		
		return dist;
	}
	
}

