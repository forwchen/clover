package com.clover4.beta.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class EventLoader {

	public EventLoader() {
		// TODO Auto-generated constructor stub
	}

	
	public ArrayList<EventItem> loadEvent(int i) {
		
		ArrayList<EventItem> eventList = new ArrayList<EventItem>();
		
		String cache = android.os.Environment.getExternalStorageDirectory()+"/clover/cache";
		
		try {
			FileReader fr;
			fr = new FileReader(cache);
			BufferedReader br = new BufferedReader(fr);
			
			String line = "null";
			
			while ((line = br.readLine()) != null){

				EventItem mItem = new EventItem();

				mItem.title = br.readLine();
				mItem.stime = br.readLine();
				mItem.place = br.readLine();
				mItem.speaker = br.readLine();
				mItem.building = br.readLine();
				if (Integer.parseInt(line) == i) eventList.add(mItem);
			}

			br.close();	
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return eventList;
	}
}
