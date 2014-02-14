package com.clover4.beta;

import java.util.ArrayList;

import com.clover4.beta.utils.*;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class TableAdapter extends BaseAdapter {

	private Activity mActivity;
	private static LayoutInflater mLayoutInflater=null;
	private ArrayList<ClassTableItem> mlist = new ArrayList<ClassTableItem>();
	
	public TableAdapter(Activity A, ArrayList<ClassTableItem> AL) {
		// TODO Auto-generated constructor stub
		mActivity = A;
		mlist = AL;
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
		ClassTableItem mItem = mlist.get(position);
		Constants c = new Constants();
		
		//Log.d("tag", mItem.name);
		
		if (mItem.type == 1){
			mView = mLayoutInflater.inflate(R.layout.classtime_list_item, null);
			TextView name = (TextView)mView.findViewById(R.id.lesson_name);
	        TextView time = (TextView)mView.findViewById(R.id.lesson_time);
	        TextView classroom = (TextView)mView.findViewById(R.id.lesson_classroom);
	        Button btn = (Button)mView.findViewById(R.id.lesson_btn);
	        
	        
	        btn.setText(mItem.code.toUpperCase().subSequence(0, 1));
	        name.setText(mItem.name);
	        time.setText(c.start_time[mItem.startunit]+"-"+c.end_time[mItem.startunit+mItem.units-1]);
	        classroom.setText(mItem.classroom);
	        
		}
		else{
			mView = mLayoutInflater.inflate(R.layout.freetime_list_item, null);
			TextView free = (TextView)mView.findViewById(R.id.free);
			TextView freetm = (TextView)mView.findViewById(R.id.freetm);
			ImageView plus = (ImageView)mView.findViewById(R.id.plus);
			freetm.setText(c.start_time[mItem.startunit]+"-"+c.end_time[mItem.startunit+mItem.units-1]);
		}
		
		
		return mView;
	}

}
