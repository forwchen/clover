package com.clover4.beta;

import java.util.ArrayList;

import com.clover4.beta.utils.ClassDBUtil;
import com.clover4.beta.utils.Constants;
import com.clover4.beta.utils.GPSUtil;
import com.clover4.beta.utils.InfoItem;
import com.clover4.beta.utils.SharedprefUtil;
import com.clover4.beta.utils.TimeUtil;

import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 显示特定时间段的空教室界面
 *
 */
public class ShowClassroom extends Activity implements OnItemClickListener{

	double latitude = 0;
	double longitude = 0;
	int start_unit;
	int end_unit;
	final int MAX_PERROW = 5;
	Constants c = new Constants();
	ClassDBUtil mClassDBUtil = new ClassDBUtil();
	ArrayList<InfoItem> mList = new ArrayList<InfoItem>();
	SharedprefUtil mSharedprefUtil;
	
	public InfoItem getItem(int i){
		String name = c.BUILDING[i];
		ArrayList<String> mArrayList = mClassDBUtil.mQuery(name, start_unit, end_unit);
		
		InfoItem mItem = new InfoItem();
		mItem.name = name;
		
		for (int j = 0; j < mArrayList.size(); j++){
			String classroom = mArrayList.get(j);
			
			if (i == 0){
				if (j < 4){
					mItem.count[2]++;
					mItem.floor[2] = mItem.floor[2] + classroom.substring(name.length()) +" ";
				}
				else if (j < 8){
					mItem.count[3]++;
					mItem.floor[3] = mItem.floor[3] + classroom.substring(name.length()) +" ";
				}else if (j < 12){
					mItem.count[1]++;
					mItem.floor[1] = mItem.floor[1] + classroom.substring(name.length()) +" ";
				}
				
				continue;
			}
			
			int floor = (int)(classroom.charAt(name.length()) - '0');
			if (floor < 4){
				if (mItem.count[floor] < MAX_PERROW){
					mItem.floor[floor] = mItem.floor[floor] + classroom.substring(name.length()) +" ";
				}
				mItem.count[floor]++;
			}
			else {
				if (mItem.count[4] < MAX_PERROW){
					mItem.floor[4] = mItem.floor[4] + classroom.substring(name.length()) +" ";
				}
				mItem.count[4]++;
			}
		}
	
		
		for (int j = 1; j <= 4; j++){
			if (mItem.count[j] > MAX_PERROW) mItem.floor[j] = 
					mItem.floor[j] + "..." + String.valueOf(mItem.count[j] - MAX_PERROW) + "+";
		}
		
		return mItem;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_classroom);

		mSharedprefUtil = new SharedprefUtil("setting", getApplicationContext());
		long time = mSharedprefUtil.readLong("time");
		String lat = mSharedprefUtil.readString("latitude");
		String lon = mSharedprefUtil.readString("longitude");
		if (lat != null && lon != null && lat.length() > 1 && lon.length() > 1){
			latitude = Double.parseDouble(lat);
			longitude = Double.parseDouble(lon);
		}
		start_unit = getIntent().getIntExtra("start_unit", -1);
		end_unit = getIntent().getIntExtra("end_unit", -1);
		
		LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
		boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean gpsvalid = false;
		
		if (isGPSEnabled && latitude >0 && longitude > 0 && System.currentTimeMillis() - time < 120 * 1000){
			GPSUtil mGpsUtil = new GPSUtil();
			ArrayList<Integer> Index = mGpsUtil.getClosest(longitude, latitude);
			for (int i = 0; i < 7; i++){
				InfoItem mItem = getItem(Index.get(i));
				if (!mItem.isEmpty()){
					mList.add(mItem);
					if (mList.size() == 3) break;
				}
			}
			gpsvalid = true;
		}
		else {
			for (int i = 0; i < 7; i++){
				InfoItem mItem = getItem(i);
				if (!mItem.isEmpty()) {
					mList.add(mItem);
				}
			}
		}
		
		if (mList.size() == 0){
			new AlertDialog.Builder(this)
			.setTitle("空教室信息未找到").
			setMessage("可能是还未下载，请打开网络再试一试").
			setPositiveButton("确定", null).
			show();
		}
		
		ListView mListView = (ListView)findViewById(R.id.spare_classroom_list);
		InfoAdapter mAdapter = new InfoAdapter(this, mList);
		mListView.setAdapter(mAdapter);
		
		mListView.setOnItemClickListener(this);
		
		if (gpsvalid && mList.size() > 0) {
			Toast.makeText(getApplicationContext(), "这些可能是离你比较近的空教室~", Toast.LENGTH_LONG).show();
		}
	}

    
    public void onResume(){
    	super.onResume();
    	
    }
    
    
    public void onPause() {
    	super.onPause();

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_classroom, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		InfoItem mItem = mList.get(arg2);
		TimeUtil mTimeUtil = new TimeUtil();
		
		Intent intent = new Intent(this, ClassroomInfo.class);
		intent.putExtra("building_name", mItem.name);
		intent.putExtra("unit", mTimeUtil.getunit());
        startActivity(intent);
	}

}
