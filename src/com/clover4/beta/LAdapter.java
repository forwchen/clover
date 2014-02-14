package com.clover4.beta;

import java.util.ArrayList;

import com.clover4.beta.utils.ClassroomItem;
import com.clover4.beta.utils.EventItem;

import android.R.mipmap;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LAdapter extends BaseAdapter {

	private Activity mActivity;
	private static LayoutInflater mLayoutInflater=null;
	private ArrayList<EventItem> mlist = new ArrayList<EventItem>();
	
	public LAdapter(Activity A, ArrayList<EventItem> AL) {
		// TODO Auto-generated constructor stub
		mlist = AL;
		mActivity = A;
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
		return null;
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
		EventItem mItem = mlist.get(position);
		mView = mLayoutInflater.inflate(R.layout.lecture_list_item, null);
		TextView title = (TextView)mView.findViewById(R.id.lecture_title);
		TextView place = (TextView)mView.findViewById(R.id.lecture_place);
		TextView time = (TextView)mView.findViewById(R.id.lecture_time);
		TextView speaker = (TextView)mView.findViewById(R.id.lecture_speaker);
		
		title.setText(mItem.title);
		place.setText(mItem.place);
		time.setText(mItem.stime.substring(11,16));
		speaker.setText(mItem.speaker);
		
		return mView;
	}

}
