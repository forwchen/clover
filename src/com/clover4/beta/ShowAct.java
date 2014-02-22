package com.clover4.beta;

import java.util.ArrayList;

import com.clover4.beta.utils.EventItem;
import com.clover4.beta.utils.EventLoader;
import com.clover4.beta.utils.GPSUtil;

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
import android.widget.ListView;

/**
 * 显示活动界面
 *
 */
public class ShowAct extends Activity {

	CustomReceiver mCustomReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_act);
		mCustomReceiver = new CustomReceiver();
		EventLoader mEventLoader = new EventLoader();
		ArrayList<EventItem> mArrayList = mEventLoader.loadEvent(0);
		
		if (mArrayList.size() == 0){
			new AlertDialog.Builder(this)
			.setTitle("活动信息未找到").
			setMessage("可能是还未下载，请打开网络再试一试").
			setPositiveButton("确定", null).
			show();
		}
		
		ListView mListView = (ListView)findViewById(R.id.actlist);
		LAdapter mAdapter = new LAdapter(this, mArrayList);
		mListView.setAdapter(mAdapter);
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
    			double latitude = intent.getDoubleExtra("lat", 0);
    			double longitude = intent.getDoubleExtra("lon", 0);
    			
    			GPSUtil mGpsUtil = new GPSUtil();
    			EventItem mEventItem= mGpsUtil.getNearbyEvent(longitude, latitude);
    			
    			if (mEventItem != null){

    				if (mEventItem.type == 1){
    					NotificationManager mNotificationManager = 
    							(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    					Intent lectureIntent = new Intent(ShowAct.this,ShowLecture.class);
						PendingIntent mPendingIntent= 
								PendingIntent.getActivity(ShowAct.this, 0, lectureIntent, PendingIntent.FLAG_CANCEL_CURRENT);
						Notification mNotification = new Notification.Builder(ShowAct.this).
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
						Intent lectureIntent = new Intent(ShowAct.this,ShowAct.class);
						PendingIntent mPendingIntent= 
								PendingIntent.getActivity(ShowAct.this, 0, lectureIntent, PendingIntent.FLAG_CANCEL_CURRENT);
						Notification mNotification = new Notification.Builder(ShowAct.this).
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
		getMenuInflater().inflate(R.menu.show, menu);
		return true;
	}

}
