package com.clover4.beta;

import com.clover4.beta.utils.Constants;
import com.clover4.beta.utils.TimeUtil;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * 选择教学楼的界面
 *
 */
public class SelectBuilding extends Activity implements OnItemClickListener{

	Constants c = new Constants();
	TimeUtil mTimeUtil = new TimeUtil();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_building);
		getActionBar().hide();
		ListView mylistview = (ListView) findViewById(R.id.buildinglist);
		String[] data = new String[]
		{
				"邯郸 光华楼东辅楼",
				"邯郸 光华楼西辅楼",
				"邯郸 第二教学楼",
				"邯郸 第三教学楼",
				"邯郸 第四教学楼",
				"邯郸 第五教学楼",
				"邯郸 第六教学楼",
		};
		ArrayAdapter<String> listData= new ArrayAdapter<String> (this,R.layout.simple_list_item,data );
		mylistview.setAdapter(listData);

		
		mylistview.setOnItemClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select_building, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
		Intent intent = new Intent(this, ClassroomInfo.class);
		intent.putExtra("building_name", c.BUILDING[arg2]);
		intent.putExtra("unit", mTimeUtil.getunit());
        startActivity(intent);
	}

}
