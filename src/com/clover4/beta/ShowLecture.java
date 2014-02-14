package com.clover4.beta;

import java.util.ArrayList;

import com.clover4.beta.utils.EventItem;
import com.clover4.beta.utils.EventLoader;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ListView;

public class ShowLecture extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_lecture);
		
		EventLoader mEventLoader = new EventLoader();
		ArrayList<EventItem> mArrayList = mEventLoader.loadEvent(1);
		
		ListView mListView = (ListView)findViewById(R.id.lecturelist);
		LAdapter mAdapter = new LAdapter(this, mArrayList);
		mListView.setAdapter(mAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_lecture, menu);
		return true;
	}

}