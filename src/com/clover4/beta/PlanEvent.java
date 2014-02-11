package com.clover4.beta;


import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.widget.ListView;
import android.widget.Toast;

public class PlanEvent extends Activity {

	boolean updated = false;
	double latitude = 0;
	double longitude = 0;
	GpsService mGpsService;
	CustomReceiver mCustomReceiver;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plan_event);
		
		updated = getIntent().getBooleanExtra("updated", false);
		if (! updated) mGpsService = new GpsService(PlanEvent.this);
		else {
			latitude = getIntent().getDoubleExtra("lat", 0);
			longitude = getIntent().getDoubleExtra("lon", 0);
			//Toast.makeText(getApplicationContext(), String.valueOf(latitude)+" "+String.valueOf(longitude), Toast.LENGTH_LONG).show();
		}
		mCustomReceiver = new CustomReceiver();
		
		
		ListView mylistview = (ListView) findViewById(R.id.eventlist);
		EventAdapter mAdapter = new EventAdapter(this);
		mylistview.setAdapter(mAdapter);
		
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
    	public void onReceive(Context context, Intent intent) {
    		// TODO Auto-generated method stub
    		if ("com.clover.LocationChangedBroadcast".equals(intent.getAction())){
    			updated = true;
    			latitude = intent.getDoubleExtra("lat", 0);
    			longitude = intent.getDoubleExtra("lon", 0);
    			
    			//Toast.makeText(context, "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_SHORT).show();
    		}
    	}

    }
	
    
    public void onResume(){
    	super.onResume();
    	if (!updated) {
    		registerReceiver(mCustomReceiver, new IntentFilter("com.clover.LocationChangedBroadcast"));
    		mGpsService.getLocation();
    	}
    }
    
    
    public void onPause() {
    	super.onPause();
    	if (!updated){
    		unregisterReceiver(mCustomReceiver);
    		mGpsService.stopUsingGPS();
    	}
	}
}
