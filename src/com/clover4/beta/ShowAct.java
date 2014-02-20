package com.clover4.beta;

import java.util.ArrayList;

import com.clover4.beta.utils.EventItem;
import com.clover4.beta.utils.EventLoader;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.Menu;
import android.widget.ListView;

/**
 * 显示活动界面
 *
 */
public class ShowAct extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_act);
		
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show, menu);
		return true;
	}

}
