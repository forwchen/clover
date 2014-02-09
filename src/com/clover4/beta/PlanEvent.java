package com.clover4.beta;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ListView;

public class PlanEvent extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plan_event);
		
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

}
