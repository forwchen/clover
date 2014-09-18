package com.clover4.beta;



import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
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

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plan_event);
		
		start_unit = getIntent().getIntExtra("start_unit", -1);
		end_unit = getIntent().getIntExtra("end_unit", -1);
		
		
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

    
    public void onResume(){
    	super.onResume();
    	
    }
    
    
    public void onPause() {
    	super.onPause();

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		switch (arg2) {
		case 0:
			Intent SCintent = new Intent(PlanEvent.this, ShowClassroom.class);
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
