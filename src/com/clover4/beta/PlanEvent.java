package com.clover4.beta;



import java.util.ArrayList;


import com.clover4.beta.utils.Constants;
import com.clover4.beta.utils.EventItem;
import com.clover4.beta.utils.EventLoader;
import com.clover4.beta.utils.GPSUtil;
import com.clover4.beta.utils.TimeUtil;


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
	GpsService mGpsService;
	CustomReceiver mCustomReceiver;
	private Constants c = new Constants();
	private TimeUtil mTimeUtil = new TimeUtil();
	private boolean isGPSregistered = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plan_event);
		
		
		start_unit = getIntent().getIntExtra("start_unit", -1);
		end_unit = getIntent().getIntExtra("end_unit", -1);
		
		updated = getIntent().getBooleanExtra("updated", false);
		
		if (! updated) mGpsService = new GpsService(PlanEvent.this);
		else {
			latitude = getIntent().getDoubleExtra("lat", 0);
			longitude = getIntent().getDoubleExtra("lon", 0);
		}
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
    			latitude = intent.getDoubleExtra("lat", 0);
    			longitude = intent.getDoubleExtra("lon", 0);
    			
    			GPSUtil mGpsUtil = new GPSUtil();
    			ArrayList<Double> mList = mGpsUtil.getDis(longitude, latitude);
    			EventLoader mEventLoader = new EventLoader();
    			ArrayList<EventItem> lectureList = mEventLoader.loadEvent(1);
    			ArrayList<EventItem> actList = mEventLoader.loadEvent(0);
    			boolean lecture_notified = false;
    			boolean act_notified = false;
    			NotificationManager mNotificationManager = 
    					(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    			String nowtime = mTimeUtil.getTime();
    			
    			for (int i = 0; i < lectureList.size(); i++){
    				EventItem mItem = lectureList.get(i);
    				String stime = mItem.stime.substring(11,16);
    				if (mTimeUtil.calc(nowtime, stime) < 0.35){
    					for (int j = 0; j < 7; j++)
    					if (mItem.building.equals(c.BUILDING[j])){
    						if (mList.get(j) < 50){
    							lecture_notified = true;
    							Intent lectureIntent = new Intent(PlanEvent.this,ShowLecture.class);
    							PendingIntent mPendingIntent= 
    									PendingIntent.getActivity(PlanEvent.this, 0, lectureIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    							Notification mNotification = new Notification.Builder(PlanEvent.this).
    									setSmallIcon(R.drawable.ic_launcher).
    									setContentTitle("附近有讲座"). 
    									setContentText("点击查看").
    									setContentIntent(mPendingIntent).
    									setAutoCancel(true).
    									setDefaults(3).
    									build();
    							
    							mNotificationManager.notify(202, mNotification);
    							//notify lecture
    						}
    						break;
    					}
    				}
    				if (lecture_notified) break;
    			}
    			
    			for (int i = 0; i < actList.size(); i++){
    				EventItem mItem = actList.get(i);
    				String stime = mItem.stime.substring(11,16);
    				if (mTimeUtil.calc(nowtime, stime) < 0.35){
    					for (int j = 0; j < 7; j++)
    					if (mItem.building.equals(c.BUILDING[j])){
    						if (mList.get(j) < 50){
    							act_notified = true;
    							Intent lectureIntent = new Intent(PlanEvent.this,ShowAct.class);
    							PendingIntent mPendingIntent= 
    									PendingIntent.getActivity(PlanEvent.this, 0, lectureIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    							Notification mNotification = new Notification.Builder(PlanEvent.this).
    									setSmallIcon(R.drawable.ic_launcher).
    									setContentTitle("附近有活动"). 
    									setContentText("点击查看").
    									setContentIntent(mPendingIntent).
    									setAutoCancel(true).
    									setDefaults(3).
    									build();
    							
    							mNotificationManager.notify(203, mNotification);
    							
    							//notify act
    						}
    						break;
    					}
    				}
    				if (act_notified) break;
    			}
    			
    			//Toast.makeText(context, "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_SHORT).show();
    		}
    	}

    }
	
    
    public void onResume(){
    	super.onResume();
    	if (!updated) {
    		registerReceiver(mCustomReceiver, new IntentFilter("com.clover.LocationChangedBroadcast"));
    		isGPSregistered = true;
    		mGpsService.getLocation();
    	}
    }
    
    
    public void onPause() {
    	super.onPause();
    	if (isGPSregistered){
    		unregisterReceiver(mCustomReceiver);
    		mGpsService.stopUsingGPS();
    	}
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
