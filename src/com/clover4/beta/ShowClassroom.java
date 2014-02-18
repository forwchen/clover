package com.clover4.beta;

import java.util.ArrayList;

import com.clover4.beta.utils.ClassDBUtil;
import com.clover4.beta.utils.Constants;
import com.clover4.beta.utils.GPSUtil;
import com.clover4.beta.utils.InfoItem;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ListView;

/**
 * 显示特定时间段的空教室
 *
 */
public class ShowClassroom extends Activity {

	boolean updated = false;
	double latitude = 0;
	double longitude = 0;
	int start_unit;
	int end_unit;
	Constants c = new Constants();
	ClassDBUtil mClassDBUtil = new ClassDBUtil();
	ArrayList<InfoItem> mList = new ArrayList<InfoItem>();
	
	public InfoItem getItem(int i){
		String name = c.BUILDING[i];
		ArrayList<String> mArrayList = mClassDBUtil.mQuery(name, start_unit, end_unit);
		System.out.println(mArrayList.size());
		InfoItem mItem = new InfoItem();
		mItem.name = name;
		
		for (int j = 0; j < mArrayList.size(); j++){
			String classroom = mArrayList.get(j);
			int floor = (int)(classroom.charAt(name.length()) - '0');
			if (floor < 4){
				if (mItem.count[floor] < 3){
					mItem.floor[floor] = mItem.floor[floor] + classroom +" ";
				}
				mItem.count[floor]++;
			}
			else {
				if (mItem.count[4] < 3){
					mItem.floor[4] = mItem.floor[4] + classroom +" ";
				}
				mItem.count[4]++;
			}
		}
	
		
		for (int j = 1; j <= 4; j++){
			if (mItem.count[j] > 3) mItem.floor[j] = 
					mItem.floor[j] + "..." + String.valueOf(mItem.count[j]-3) + "+";
		}
		
		return mItem;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_classroom);
		
		updated = getIntent().getBooleanExtra("updated", false);
		latitude = getIntent().getDoubleExtra("lat", 0);
		longitude = getIntent().getDoubleExtra("lon", 0);
		start_unit = getIntent().getIntExtra("start_unit", -1);
		end_unit = getIntent().getIntExtra("end_unit", -1);
		
		System.out.println(start_unit+" "+end_unit);
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
		
		
		ListView mListView = (ListView)findViewById(R.id.spare_classroom_list);
		InfoAdapter mAdapter = new InfoAdapter(this, mList);
		mListView.setAdapter(mAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_classroom, menu);
		return true;
	}

}
