package com.clover4.beta;





import com.clover4.beta.utils.EventItem;
import com.clover4.beta.utils.GPSUtil;


import android.os.Bundle;
import android.app.Activity;
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
 * 选择活动的界面
 *
 */
public class PlanEvent extends Activity implements OnItemClickListener{

	boolean updated = false;
	double latitude = 0;
	double longitude = 0;
	int start_unit;
	int end_unit;
	CustomReceiver mCustomReceiver;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plan_event);
		
		updated = getIntent().getBooleanExtra("updated", false);
		latitude = getIntent().getDoubleExtra("lat", 0);
		longitude = getIntent().getDoubleExtra("lon", 0);
		start_unit = getIntent().getIntExtra("start_unit", -1);
		end_unit = getIntent().getIntExtra("end_unit", -1);
		
		mCustomReceiver = new CustomReceiver();
		
		
		ListView mListView = (ListView) findViewById(R.id.eventlist);
		EventAdapter mAdapter = new EventAdapter(this);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(PlanEvent.this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.plan_event, menu);
		return true;
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
    			updated = true;
    			double latitude = intent.getDoubleExtra("lat", 0);
    			double longitude = intent.getDoubleExtra("lon", 0);
    			
    			GPSUtil mGpsUtil = new GPSUtil();
    			EventItem mEventItem= mGpsUtil.getNearbyEvent(longitude, latitude);
    			
    			if (mEventItem != null){

    				if (mEventItem.type == 1){
    					NotificationManager mNotificationManager = 
    							(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    					Intent lectureIntent = new Intent(PlanEvent.this,ShowLecture.class);
						PendingIntent mPendingIntent= 
								PendingIntent.getActivity(PlanEvent.this, 0, lectureIntent, PendingIntent.FLAG_CANCEL_CURRENT);
						Notification mNotification = new Notification.Builder(PlanEvent.this).
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
						Intent lectureIntent = new Intent(PlanEvent.this,ShowAct.class);
						PendingIntent mPendingIntent= 
								PendingIntent.getActivity(PlanEvent.this, 0, lectureIntent, PendingIntent.FLAG_CANCEL_CURRENT);
						Notification mNotification = new Notification.Builder(PlanEvent.this).
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		switch (arg2) {
		case 0:
			Intent SCintent = new Intent(PlanEvent.this, ShowClassroom.class);
			SCintent.putExtra("updated", updated);
			SCintent.putExtra("lon", longitude);
			SCintent.putExtra("lat", latitude);
			SCintent.putExtra("start_unit", start_unit);
			SCintent.putExtra("end_unit", end_unit);
			startActivity(SCintent);
			break;
		case 1:
			Intent SLintent = new Intent(PlanEvent.this, ShowLecture.class);
			startActivity(SLintent);
			break;
		case 2:
			Intent SAintent = new Intent(PlanEvent.this, ShowAct.class);
			startActivity(SAintent);
			break;
		default:
			break;
		}
	}
}
