package com.clover4.beta;

import java.util.ArrayList;

import com.clover4.beta.utils.ClassroomItem;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ClassroomAdapter extends BaseAdapter {

	private int time_of_day;
	private Activity mActivity;
	private static LayoutInflater mLayoutInflater=null;
	private ArrayList<ClassroomItem> mlist = new ArrayList<ClassroomItem>();
	
	public ClassroomAdapter(Activity A, ArrayList<ClassroomItem> AL, int T) {
		// TODO Auto-generated constructor stub
		mlist = AL;
		mActivity = A;
		time_of_day = T;
		mLayoutInflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mlist.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mlist.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View mView = convertView;
		ClassroomItem mItem = mlist.get(position);
		
		if (time_of_day == 2){
			mView = mLayoutInflater.inflate(R.layout.table2_list_item, null);
			TextView name = (TextView)mView.findViewById(R.id.nclassroom);
			Button button0 = (Button)mView.findViewById(R.id.nbutton1);
			Button button1 = (Button)mView.findViewById(R.id.nbutton2);
			Button button2 = (Button)mView.findViewById(R.id.nbutton3);
			Button button3 = (Button)mView.findViewById(R.id.nbutton4);
			

			name.setText(mItem.name);
			if (mItem.stat[time_of_day].charAt(0) == '1') button0.
			setBackgroundColor(button0.getContext().getResources().getColor(R.color.tli_btn_red));
			else button0.
			setBackgroundColor(button0.getContext().getResources().getColor(R.color.tli_btn_green));
			
			if (mItem.stat[time_of_day].charAt(1) == '1') button1.
			setBackgroundColor(button1.getContext().getResources().getColor(R.color.tli_btn_red));
			else button1.
			setBackgroundColor(button1.getContext().getResources().getColor(R.color.tli_btn_green));
			
			if (mItem.stat[time_of_day].charAt(2) == '1') button2.
			setBackgroundColor(button2.getContext().getResources().getColor(R.color.tli_btn_red));
			else button2.
			setBackgroundColor(button2.getContext().getResources().getColor(R.color.tli_btn_green));
			
			if (mItem.stat[time_of_day].charAt(3) == '1') button3.
			setBackgroundColor(button3.getContext().getResources().getColor(R.color.tli_btn_red));
			else button3.
			setBackgroundColor(button3.getContext().getResources().getColor(R.color.tli_btn_green));
			
		}
		else {
			mView = mLayoutInflater.inflate(R.layout.table1_list_item, null);
			TextView name = (TextView)mView.findViewById(R.id.mclassroom);
			Button button0 = (Button)mView.findViewById(R.id.mbutton1);
			Button button1 = (Button)mView.findViewById(R.id.mbutton2);
			Button button2 = (Button)mView.findViewById(R.id.mbutton3);
			Button button3 = (Button)mView.findViewById(R.id.mbutton4);
			Button button4 = (Button)mView.findViewById(R.id.mbutton5);
			
			name.setText(mItem.name);
			if (mItem.stat[time_of_day].charAt(0) == '1') button0.
			setBackgroundColor(button0.getContext().getResources().getColor(R.color.tli_btn_red));
			else button0.
			setBackgroundColor(button0.getContext().getResources().getColor(R.color.tli_btn_green));
			
			if (mItem.stat[time_of_day].charAt(1) == '1') button1.
			setBackgroundColor(button1.getContext().getResources().getColor(R.color.tli_btn_red));
			else button1.
			setBackgroundColor(button1.getContext().getResources().getColor(R.color.tli_btn_green));
			
			if (mItem.stat[time_of_day].charAt(2) == '1') button2.
			setBackgroundColor(button2.getContext().getResources().getColor(R.color.tli_btn_red));
			else button2.
			setBackgroundColor(button2.getContext().getResources().getColor(R.color.tli_btn_green));
			
			if (mItem.stat[time_of_day].charAt(3) == '1') button3.
			setBackgroundColor(button3.getContext().getResources().getColor(R.color.tli_btn_red));
			else button3.
			setBackgroundColor(button3.getContext().getResources().getColor(R.color.tli_btn_green));
			
			if (mItem.stat[time_of_day].charAt(4) == '1') button4.
			setBackgroundColor(button4.getContext().getResources().getColor(R.color.tli_btn_red));
			else button4.
			setBackgroundColor(button4.getContext().getResources().getColor(R.color.tli_btn_green));
		}
		
		return mView;
	}

}
