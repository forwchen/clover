package com.clover4.beta;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EventAdapter extends BaseAdapter {
	
	
	private Activity mActivity;
	private static LayoutInflater mLayoutInflater=null;

	public EventAdapter(Activity A) {
		// TODO Auto-generated constructor stub
		mActivity = A;
		mLayoutInflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		View mView = convertView;
		mView = mLayoutInflater.inflate(R.layout.event_list_item, null);
		ImageView icon = (ImageView)mView.findViewById(R.id.myicon);
		ImageView arrow = (ImageView)mView.findViewById(R.id.myarrow);
		TextView text = (TextView)mView.findViewById(R.id.event);
		 
		
		
		switch (position) {
		case 0:
			text.setText("找空教室");
			icon.setImageDrawable(icon.getContext().getResources().getDrawable(R.drawable.emptyclass));
			break;
		case 1:
			text.setText("去听讲座");
			icon.setImageDrawable(icon.getContext().getResources().getDrawable(R.drawable.lecture));
			break;
		case 2:
			text.setText("有什么活动");
			icon.setImageDrawable(icon.getContext().getResources().getDrawable(R.drawable.activity));
			break;
		default:
			break;
		}
		
		return mView;
	}

}
