package com.clover4.beta;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import com.clover4.beta.utils.ClassroomItem;
import com.clover4.beta.utils.Constants;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

/**
 * 显示空教室信息
 *
 */
public class ClassroomInfo extends Activity {

	Constants c = new Constants();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_classroom_info);
		
		getActionBar().hide();
		
		String building_name = getIntent().getStringExtra("building_name");
		int unit = getIntent().getIntExtra("unit", 0);
		
		String file = c.appdir+"/"+building_name+".txt";
		
		ArrayList<ClassroomItem> mArrayList = new ArrayList<ClassroomItem>();
		
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			
			String line = "";
			
			while ((line = br.readLine()) != null){

				ClassroomItem cstat = new ClassroomItem();
				if (line.charAt(0) == 'H'){
					cstat.name = line;
					cstat.stat[0] = br.readLine();
					cstat.stat[1] = br.readLine();
					cstat.stat[2] = br.readLine();
				}
				
				mArrayList.add(cstat);

			}

			br.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (mArrayList.size() == 0){
			new AlertDialog.Builder(this)
			.setTitle("空教室信息未找到").
			setMessage("可能是还未下载，请打开网络再试一试").
			setPositiveButton("确定", null).
			show();
		}
		
		
		final Button button0 = (Button) findViewById(R.id.morning);
		final Button button1 = (Button) findViewById(R.id.midday);
		final Button button2 = (Button) findViewById(R.id.night);
		
		
		final ListView mylistview = (ListView) findViewById(R.id.classroomlist);
		
		final ClassroomAdapter mAdapter0 = new ClassroomAdapter(this,mArrayList,0);
		final ClassroomAdapter mAdapter1 = new ClassroomAdapter(this,mArrayList,1);
		final ClassroomAdapter mAdapter2 = new ClassroomAdapter(this,mArrayList,2);
		
		switch (unit) {
		case 0:
			mylistview.setAdapter(mAdapter0);
			setbright(button0);
			setdark(button1);
			setdark(button2);
			break;
		case 1:
			mylistview.setAdapter(mAdapter1);
			setbright(button1);
			setdark(button0);
			setdark(button2);
			break;
		case 2:
			mylistview.setAdapter(mAdapter2);
			setbright(button2);
			setdark(button1);
			setdark(button0);
			break;
		default:
			break;
		}
		
		button0.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){
				mylistview.setAdapter(mAdapter0);
				setbright(button0);
				setdark(button1);
				setdark(button2);
			}
		});
		
		button1.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){
				mylistview.setAdapter(mAdapter1);
				setbright(button1);
				setdark(button0);
				setdark(button2);
			}
		});
		
		button2.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){
				mylistview.setAdapter(mAdapter2);
				setbright(button2);
				setdark(button1);
				setdark(button0);
			}
		});
		
	}
	
	public void setbright(Button btn){
		btn.setBackgroundColor(getResources().getColor(R.color.aci_btn_bright));
	}
	
	public void setdark(Button btn){
		btn.setBackgroundColor(getResources().getColor(R.color.aci_btn_dark));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.classroom_info, menu);
		return true;
	}

}
