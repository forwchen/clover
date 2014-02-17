package com.clover4.beta;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import com.clover4.beta.utils.ClassTableItem;
import com.clover4.beta.utils.ClassroomItem;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class ClassroomInfo extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_classroom_info);
		
		getActionBar().hide();
		
		String building_name = getIntent().getStringExtra("building_name");
		int unit = getIntent().getIntExtra("unit", 0);
		
		String file = android.os.Environment.getExternalStorageDirectory()
				+"/clover/"+building_name+".txt";
		
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
				
				System.out.println(cstat.name+" "+cstat.stat[0]+" "+cstat.stat[1]+" "+cstat.stat[2]);
				
				mArrayList.add(cstat);

			}

			br.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		Button button0 = (Button) findViewById(R.id.morning);
		Button button1 = (Button) findViewById(R.id.midday);
		Button button2 = (Button) findViewById(R.id.night);
		
		
		final ListView mylistview = (ListView) findViewById(R.id.classroomlist);
		
		final ClassroomAdapter mAdapter0 = new ClassroomAdapter(this,mArrayList,0);
		final ClassroomAdapter mAdapter1 = new ClassroomAdapter(this,mArrayList,1);
		final ClassroomAdapter mAdapter2 = new ClassroomAdapter(this,mArrayList,2);
		
		switch (unit) {
		case 0:
			mylistview.setAdapter(mAdapter0);
			break;
		case 1:
			mylistview.setAdapter(mAdapter1);
			break;
		case 2:
			mylistview.setAdapter(mAdapter2);
			break;
		default:
			break;
		}
		
		button0.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){
				mylistview.setAdapter(mAdapter0);
			}
		});
		
		button1.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){
				mylistview.setAdapter(mAdapter1);
			}
		});
		
		button2.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){
				mylistview.setAdapter(mAdapter2);
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.classroom_info, menu);
		return true;
	}

}
