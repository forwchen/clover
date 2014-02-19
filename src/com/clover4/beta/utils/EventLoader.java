package com.clover4.beta.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * 从存储卡中加载活动信息
 *
 */
public class EventLoader {

	Constants c = new Constants();
	public EventLoader() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param i 0加载活动信息，1加载讲座信息
	 * @return EventItem列表
	 */
	public ArrayList<EventItem> loadEvent(int i) {
		
		ArrayList<EventItem> eventList = new ArrayList<EventItem>();
		
		String cache = c.appdir+"/cache";
		
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
