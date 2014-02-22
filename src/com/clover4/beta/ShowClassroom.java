package com.clover4.beta;

import java.util.ArrayList;

import com.clover4.beta.utils.ClassDBUtil;
import com.clover4.beta.utils.Constants;
import com.clover4.beta.utils.EventItem;
import com.clover4.beta.utils.GPSUtil;
import com.clover4.beta.utils.InfoItem;
import com.clover4.beta.utils.TimeUtil;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 显示特定时间段的空教室界面
 *
 */
public class ShowClassroom extends Activity implements OnItemClickListener{

	boolean updated = false;
	double latitude = 0;
	double longitude = 0;
	int start_unit;
	int end_unit;
	final int MAX_PERROW = 5;
	CustomReceiver mCustomReceiver;
	Constants c = new Constants();
	ClassDBUtil mClassDBUtil = new ClassDBUtil();
	ArrayList<InfoItem> mList = new ArrayList<InfoItem>();
	
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
		mCustomReceiver = new CustomReceiver();
		updated = getIntent().getBooleanExtra("updated", false);
		latitude = getIntent().getDoubleExtra("lat", 0);
		longitude = getIntent().getDoubleExtra("lon", 0);
		start_unit = getIntent().getIntExtra("start_unit", -1);
		end_unit = getIntent().getIntExtra("end_unit", -1);
		
		
		if (!updated){
			for (int i = 0; i < 7; i++){
				InfoItem mItem = getItem(i);
				if (!mItem.isEmpty()) {
					mList.add(mItem);
				}
			}
			
		}
		else {
			GPSUtil mGpsUtil = new GPSUtil();
			ArrayList<Integer> Index = mGpsUtil.getClosest(longitude, latitude);
			for (int i = 0; i < 7; i++){
				InfoItem mItem = getItem(Index.get(i));
				if (!mItem.isEmpty()){
					mList.add(mItem);
					if (mList.size() == 3) break;
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
	}

	public class CustomReceiver extends BroadcastReceiver {

    	public CustomReceiver() {
    		// TODO Auto-generated constructor stub

    	}

    	@Override
    	/**
    	 * 当获取到GPS位置更新的时候，检查附近有无讲座或者活动，有则提醒
    	 */
    	public void onReceive(Context context, Intent intent) {
    		// TODO Auto-generated method stub
    		if ("com.clover.LocationChangedBroadcast".equals(intent.getAction())){
    			latitude = intent.getDoubleExtra("lat", 0);
    			longitude = intent.getDoubleExtra("lon", 0);
    			
    			GPSUtil mGpsUtil = new GPSUtil();
    			EventItem mEventItem= mGpsUtil.getNearbyEvent(longitude, latitude);
    			
    			if (mEventItem != null){

    				if (mEventItem.type == 1){
    					NotificationManager mNotificationManager = 
    							(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    					Intent lectureIntent = new Intent(ShowClassroom.this,ShowLecture.class);
						PendingIntent mPendingIntent= 
								PendingIntent.getActivity(ShowClassroom.this, 0, lectureIntent, PendingIntent.FLAG_CANCEL_CURRENT);
						Notification mNotification = new Notification.Builder(ShowClassroom.this).
								setSmallIcon(R.drawable.ic_launcher).
								setContentTitle("附近 "+mEventItem.place+" 有讲座"). 
								setContentText("点击查看").
								setContentIntent(mPendingIntent).
								setAutoCancel(true).
								setDefaults(3).
								build();
						
						mNotificationManager.notify(202, mNotification);
    				}
    				else {
						NotificationManager mNotificationManager = 
								(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
						Intent lectureIntent = new Intent(ShowClassroom.this,ShowAct.class);
						PendingIntent mPendingIntent= 
								PendingIntent.getActivity(ShowClassroom.this, 0, lectureIntent, PendingIntent.FLAG_CANCEL_CURRENT);
						Notification mNotification = new Notification.Builder(ShowClassroom.this).
								setSmallIcon(R.drawable.ic_launcher).
								setContentTitle("附近 "+mEventItem.place+" 有活动"). 
								setContentText("点击查看").
								setContentIntent(mPendingIntent).
								setAutoCancel(true).
								setDefaults(3).
								build();
						
						mNotificationManager.notify(203, mNotification);
    				}
    			}
    		}
    	}
    }
	
    
    public void onResume(){
    	super.onResume();
    	registerReceiver(mCustomReceiver, new IntentFilter("com.clover.LocationChangedBroadcast"));
    	
    }
    
    
    public void onPause() {
    	super.onPause();
    	unregisterReceiver(mCustomReceiver);

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
