package com.clover4.beta;

import java.util.ArrayList;

import com.clover4.beta.utils.InfoItem;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class InfoAdapter extends BaseAdapter {

	
	private Activity mActivity;
	private static LayoutInflater mLayoutInflater=null;
	private ArrayList<InfoItem> mlist = new ArrayList<InfoItem>();
	
	public InfoAdapter(Activity A, ArrayList<InfoItem> AL) {
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
		InfoItem mItem = mlist.get(position);
		mView = mLayoutInflater.inflate(R.layout.info_list_item, null);
		TextView textView2 = (TextView)mView.findViewById(R.id.textView2);
		TextView textView3 = (TextView)mView.findViewById(R.id.textView3);
		TextView textView1 = (TextView)mView.findViewById(R.id.textView1);
		TextView textView4 = (TextView)mView.findViewById(R.id.textView4);
		ImageView buildingname = (ImageView)mView.findViewById(R.id.name);
		
		textView2.setText(mItem.floor[2]);
		textView3.setText(mItem.floor[3]);
		textView1.setText(mItem.floor[1]);
		textView4.setText(mItem.floor[4]);
		
		
		return mView;
	}

}
