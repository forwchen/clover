package com.clover4.beta;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.clover4.beta.PlanEvent.CustomReceiver;
import com.clover4.beta.utils.*;

import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.R.integer;
import android.app.Activity;
import android.app.ExpandableListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateFormat;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemClickListener{

	
	private final String TAG = "TEST";
	private final String SUCC = "success message";
	
	//Log.d(TAG, SUCC);
	
	boolean updated = false;
	double latitude = 0;
	double longitude = 0;
	GpsService mGpsService;
	CustomReceiver mCustomReceiver;
	
	private EditText mpwdText;
	private EditText musrText;
	private Button mLoginButton;
	private String mpassword;
	private String musrname;
	private SharedprefUtil mSharedprefUtil;
	private ProgressDialog mProgressDialog;
	private boolean isFree[]= new boolean[16];
	private ArrayList<ClassTableItem> ClassList;
	private TimeUtil mTimeUtil = new TimeUtil();
	private Constants c = new Constants();
	
	
	public boolean isNetworkOn() {
		ConnectivityManager connectivity = (ConnectivityManager) 
				MainActivity.this.getSystemService(MainActivity.this.CONNECTIVITY_SERVICE);
		if (connectivity.getActiveNetworkInfo() == null) return false;
		if (! connectivity.getActiveNetworkInfo().isConnected()) return false;
		return true;
	}
	
	public class LoginTask extends AsyncTask<Void, Void, Boolean>{

		@Override
		protected void onPreExecute() {
			musrname = musrText.getText().toString();
			mpassword = mpwdText.getText().toString();
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Boolean result = true;
			try {
				String LoginURL = "http://61.129.42.58:9083/school/login?user=13307130195&psw=6gjpq3a";
				//String LoginURL = "http://61.129.42.58:9083/school/login?"+"user="+musrname+"&psw="+mpassword;
				
				HttpGet mHttpGet = new HttpGet(LoginURL);
			    DefaultHttpClient mDefaultHttpClient = new DefaultHttpClient();

			    
				HttpResponse mHttpResponse = mDefaultHttpClient.execute(mHttpGet);
			    if (mHttpResponse.getStatusLine().getStatusCode() == 200)
			    {
			    	String str = EntityUtils.toString(mHttpResponse.getEntity());
			    	if (str.contains("false")) result = false;
			    }
			    else {
			    	result = false;
			    }
			    mHttpGet.abort();
			    

			} catch (Exception e) {
				// TODO Auto-generated catch block
				result = false;
				e.printStackTrace();
			}
			return result;
		}
		
		@Override
		protected void onPostExecute(Boolean result){
			if (result == true) {
				//setContentView(R.layout.activity_main);
				doMain();
			}
		}
		
	}
	
	public class getStdTable extends AsyncTask<Void, Void, String>{

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String result = "";
			try {
				HttpPost mHttpPost = new HttpPost("http://uis1.fudan.edu.cn/amserver/UI/Login");
			
				DefaultHttpClient mDefaultHttpClient = new DefaultHttpClient();
			    mDefaultHttpClient.getParams().setParameter("http.protocol.allow-circular-redirects", 
			    		Boolean.valueOf(true));
			    ArrayList<BasicNameValuePair> mArrayList = new ArrayList<BasicNameValuePair>();
			    mArrayList.add(new BasicNameValuePair("IDToken1", "13307130195"));
			    mArrayList.add(new BasicNameValuePair("IDToken2", "6gjpq3a"));
			    mHttpPost.setEntity(new UrlEncodedFormEntity(mArrayList, "UTF-8"));
			    
			    mDefaultHttpClient.execute(mHttpPost);
			    
			    mHttpPost.abort();

			    HttpGet mHttpGet = new HttpGet("http://xk.fudan.edu.cn/fdeams/stdCourseTable.action?xq=2013201402");
			    HttpResponse mHttpResponse = mDefaultHttpClient.execute(mHttpGet);
			    if (mHttpResponse.getStatusLine().getStatusCode() == 200)
			    {
			    	result = EntityUtils.toString(mHttpResponse.getEntity());
			    	System.out.println(result.length());
			    	if (result.contains("arranges") == false) result = ""; 
			    }
			    else{
			    	result = "";
			    }
			    mHttpGet.abort();
			    

			} catch (Exception e) {
				result = "";
				e.printStackTrace();
				// TODO: handle exception
			}
	    
		
			return result;
	
		}
	
		@Override
		protected void onPostExecute(String result){
			mProgressDialog.cancel();
			if (result != "") {
				mSharedprefUtil.writeLong("table_downloaded", 1L);
				
				StdTableUtil mStdTableUtil = new StdTableUtil(new StringBuffer(result));
				mStdTableUtil.storeTable();
				
				doInflate();
			}
			else{
				Toast.makeText(getApplicationContext(), 
						"Please check your network connection or use wifi \n try reopening the app", 
						Toast.LENGTH_LONG);
			}
		}

	
		@Override
		protected void onPreExecute(){
			mProgressDialog = new ProgressDialog(MainActivity.this);
			mProgressDialog.setMessage("Downloading...");
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
		}
	}

	public class fetchInfo extends AsyncTask<Void, Void, Boolean>{

		
		
		private Classroom[] classroom = new Classroom[300];
		private int classroomsum = 0;
		private String sclassroomname = "", scodelesson = "", susedflag = "", sclasstime = "", sclassroomid = "";
		
		
		@Override
		protected Boolean doInBackground(Void... params) 
		{
			// TODO Auto-generated method stub
			Boolean result = true;
			for (int i = 1; i < c.NUMOFCLASSBUILD; i++)
				for (int j = 0; j < c.NUMOFCLASSTIMER; j++){
					
					HttpResponse mHttpResponse = null;
					String url = "http://61.129.42.58:9083/sid/queryClassroomService/vid/buildDetail?year=2013&month=11&day=13&timeFlag="+c.CLASSTIMER[j]+"&idBuilding="+c.CLASSBUILD[i]+"&returnType=android";
					String jsonData = new String();

					try 
					{
						HttpGet mHttpGet = new HttpGet(url);
						mHttpResponse = new DefaultHttpClient().execute(mHttpGet);
						if (mHttpResponse.getStatusLine().getStatusCode() == 200)
						{
							jsonData = EntityUtils.toString(mHttpResponse.getEntity());
							System.out.println(c.CLASSBUILD[i]+" at "+c.CLASSTIMER[j]+" is parsing");
							JsonReader reader = new JsonReader(new StringReader(jsonData));
							readMessage(reader);
						}
						else {
							result = false;
						}
					}
					catch (Exception e)
					{
						result = false;
						e.printStackTrace();
					}
					
				}
			
			if (result == true){
				
				for (int i = 1; i <= classroomsum; i++){
					for (int j = 0; j < 14; j++){
						classroom[i].stat += (classroom[i].used[j]<<j);
						System.out.print(classroom[i].used[j]);
					}
					classroom[i].toStr();
					
					for (int j = 0; j < c.NUMOFCLASSBUILD; j++){
						if (classroom[i].name.startsWith(c.CLASSBUILD[j])){
							classroom[i].which = j + 1;
							break;
						}
					}
					
					System.out.println("--->"+classroom[i].name+" "+classroom[i].which);
				}
				
				try {
					String dbPath = android.os.Environment.getExternalStorageDirectory()+"/clover/info.db";
					File dbFile= new File(dbPath);
					if (dbFile.exists()) dbFile.delete();
	
					SQLiteDatabase mDB = SQLiteDatabase.openOrCreateDatabase(dbPath, null);
					FileWriter fw;
					
					
					int [] begin = new int[9];
					int [] end = new int[9];
					int N = 0;
					
					begin[0] = 1;
					for (int i = 1; i <= classroomsum; i++){
						if (i == 1 || classroom[i].which != classroom[i-1].which) {
							String createTable = "CREATE TABLE "+c.CLASSBUILD[classroom[i].which-1]+
									" (id integer primary key autoincrement, name varchar(10), stat integer)";
							mDB.execSQL(createTable);
							if (i > 1){
								end[N] = i - 1;
								N++;
								begin[N] = i;
							}
						}
						
						ContentValues mContentValues = new ContentValues();
						mContentValues.put("name", classroom[i].name);
						mContentValues.put("stat", classroom[i].stat);
						
						mDB.insert(c.CLASSBUILD[classroom[i].which-1], null, mContentValues);
						
					}
					end[N] = classroomsum;
					
					for (int i = 0; i <= N; i++){
						fw = new FileWriter(android.os.Environment.getExternalStorageDirectory()
						+"/clover/"+c.CLASSBUILD[i]+".txt");
						
						for (int j = begin[i]; j <= end[i]; j++){
							fw.write(classroom[j].name+"\n");
							fw.write(classroom[j].str.substring(0, 5)+"\n");
							fw.write(classroom[j].str.substring(5, 10)+"\n");
							fw.write(classroom[j].str.substring(10, 14)+"\n");
						}
						
						fw.flush();
						fw.close();
					}
				}
				catch (Exception e){
					result = false;
					e.printStackTrace();
				}
			}
			
			
			return result;
		}
		
		private void readMessageArray(JsonReader reader) throws IOException
		{
			reader.beginArray();
			while (reader.hasNext())
			{
				readMessage(reader);
			}
			reader.endArray();
		}
		
		private void readMessage(JsonReader reader) throws IOException
		{
			reader.beginObject();
			while (reader.hasNext())
			{
				String tagName = reader.nextName();
				if ((tagName.equals("jsonp")) || (tagName.equals("data"))) readMessage(reader);
				else if (tagName.equals("classGroupList")) readMessageArray(reader);
				else if (tagName.equals("classRoomName"))
				{
					sclassroomname = reader.nextString();
				}
				else if (tagName.equals("timeList")) readMessageArray(reader);
				else if (tagName.equals("codeLesson"))
				{
					scodelesson = reader.nextString();
				}
				else if (tagName.equals("usedFlag"))
				{
					susedflag = reader.nextString();
				}
				else if (tagName.equals("time"))
				{
					sclasstime = reader.nextString();
					setClassTime(sclassroomname,scodelesson,susedflag);
				}
				else if (tagName.equals("classRoomId"))
				{
					sclassroomid = reader.nextString();
				}
				else reader.skipValue();

			}
			reader.endObject();
		}
		
		private void setClassTime(String name,String lesson,String flag)
		{
			boolean registerflag = false;
			int usedflag, codelesson = 0;
			if (flag.equals("true")) usedflag = 1; 
			else usedflag = 0;
			
			for (int i = 0; i < lesson.length(); i++)
				codelesson = codelesson*10 + (int)(lesson.charAt(i)-'0');
			
			if (codelesson <= 14)
			{
				for (int i = 1; i <= classroomsum; i++)
					if (name.equals(classroom[i].name))
					{
						classroom[i].setUsed(codelesson,usedflag);
						registerflag = true;
						break;
					}
				
				if (!registerflag)
				{
					classroomsum++;
					classroom[classroomsum] = new Classroom(name);
					classroom[classroomsum].setUsed(codelesson, usedflag);
				}
			}
		}
	
		@Override
		protected void onPostExecute(Boolean result){
			if (result == true){
				mSharedprefUtil.writeLong(mTimeUtil.date, 1L);
				
			}
			else{
				Toast.makeText(getApplicationContext(), "Fetching classroom info failed", Toast.LENGTH_LONG);
				
			}
		}
	}
	
	public class fetchEvent extends AsyncTask<Void, Void, Boolean>{

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			HttpResponse mHttpResponse = null;

			//String url = "http://lecture.oss-cn-hangzhou.aliyuncs.com/"+mTimeUtil.date;
			String url = "http://lecture.oss-cn-hangzhou.aliyuncs.com/2013-12-17";
			String Data = new String();
			Boolean result = true;
			try 
			{
				HttpGet mHttpGet = new HttpGet(url);
				mHttpResponse = new DefaultHttpClient().execute(mHttpGet);
				if (mHttpResponse.getStatusLine().getStatusCode() == 200)
				{
					Data = EntityUtils.toString(mHttpResponse.getEntity());
					System.out.println(Data);
					
					FileWriter fw;
					fw = new FileWriter(android.os.Environment.getExternalStorageDirectory()+"/clover/cache");
					BufferedWriter bw = new BufferedWriter(fw);
					
					bw.write(Data);
					bw.flush();
					bw.close();
					
				}
				else {
					result = false;
				}
			}
			catch (Exception e)
			{
				result = false;
				e.printStackTrace();
			}
			
			
			return result;
		}
		
		public void onPostExecute(Boolean result){
			if (result == true) {
				mSharedprefUtil.writeLong("eventDownloaded", 1L);
			}
		}
		
	}
	
	
	public void doInflate(){
		

		if ((mTimeUtil.dayofweek == 0) || (mTimeUtil.dayofweek == 6)) return;
		MainActivity.this.setContentView(R.layout.view_class_list);
		
		StdTableLoader mStdTableLoader = new StdTableLoader();
		ClassList = mStdTableLoader.getTable();
		
		for (int i = 0; i < ClassList.size(); i++){
			if (ClassList.get(i).type == 1) isFree[i] = false;
			else isFree[i] = true;
		}
		
		ListView mListView = (ListView) findViewById(R.id.classlist);
		TableAdapter mTableAdapter = new TableAdapter(MainActivity.this,ClassList);
		mListView.setAdapter(mTableAdapter);
		
		mListView.setOnItemClickListener(MainActivity.this);
		
		
		Long eventDownloaded = mSharedprefUtil.readLong("eventDownloaded");
		if (eventDownloaded == 0) {
			fetchEvent mfetchEvent = new fetchEvent();
			mfetchEvent.execute((Void) null);
		}
	}
	
	public void doMain(){
		MainActivity.this.setContentView(R.layout.activity_main);
				
		mSharedprefUtil.writeLong("logged_in", 1L);
		Long stdTableStatus = mSharedprefUtil.readLong("table_downloaded");
		if (stdTableStatus == 0){
			getStdTable mgetStdTable = new getStdTable();
			mgetStdTable.execute((Void)null);
		}
		else{
			doInflate();
		}
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		updated = getIntent().getBooleanExtra("updated", false);
		if (! updated) mGpsService = new GpsService(MainActivity.this);
		else {
			latitude = getIntent().getDoubleExtra("lat", 0);
			longitude = getIntent().getDoubleExtra("lon", 0);
		}
		mCustomReceiver = new CustomReceiver();
		
		
		File f= new File(android.os.Environment.getExternalStorageDirectory()+"/clover");
		
		if (f.exists()) {
			Log.d(TAG, SUCC);
		}
		else f.mkdir();
		
		try
		{
			InputStream is = getResources().openRawResource(R.raw.location);
			FileOutputStream fos = 
				new FileOutputStream(android.os.Environment.getExternalStorageDirectory()+"/clover/location.db");
			byte[] buffer = new byte[8192];
			int count = 0;
			while ((count = is.read(buffer)) >= 0)
			{
				fos.write(buffer, 0, count);
			}
			fos.close();
			is.close();
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		
		mSharedprefUtil = new SharedprefUtil("setting",getApplicationContext());
		
		Long classroomInfo = mSharedprefUtil.readLong(mTimeUtil.date);
		if (classroomInfo == 0){
			if (! isNetworkOn()){
				Toast.makeText(getApplicationContext(), "fetch classroominfo failed", Toast.LENGTH_LONG);
				
			}
			else {
				fetchInfo mfetchInfo = new fetchInfo();
				mfetchInfo.execute((Void)null);
			}
		}
		else{
			
		}
		
		
		
		Long loginStatus = mSharedprefUtil.readLong("logged_in");
		
		if (loginStatus == 0){
			Log.d(TAG, SUCC);
			/**
			 * set login view
			 * get usrname/psword
			 * check network
			 * do login via asynctask----->another process<--wait
			 * failed-->do again
			 */
			setContentView(R.layout.view_main_login);
			mpwdText = (EditText) findViewById(R.id.pwdtext);
			musrText = (EditText) findViewById(R.id.usrtext);
			mLoginButton = (Button) findViewById(R.id.loginbtn);
			
			
			
			mLoginButton.setOnClickListener(
				new View.OnClickListener() {
				
					@Override
					public void onClick(View view) {
						
						if (!isNetworkOn()) {
							Toast.makeText(getApplicationContext(), 
									"Please check your network connection", Toast.LENGTH_SHORT).show();
							return;
						}
						
						InputMethodManager  mInputMethodManager = (InputMethodManager)
								MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
						mInputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); 
	        
						LoginTask mLoginTask = new LoginTask();
						Boolean result = false;
						try {
							result = mLoginTask.execute((Void) null).get();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
						
						if (result == false) {
							mpwdText.requestFocus();
						}
					}
				});
		}
		else {

			doMain();
		}
		
		
	}
	
	public class CustomReceiver extends BroadcastReceiver {

    	public CustomReceiver() {
    		// TODO Auto-generated constructor stub

    	}

    	@Override
    	public void onReceive(Context context, Intent intent) {
    		// TODO Auto-generated method stub
    		if ("com.clover.LocationChangedBroadcast".equals(intent.getAction())){
    			updated = true;
    			latitude = intent.getDoubleExtra("lat", 0);
    			longitude = intent.getDoubleExtra("lon", 0);
    			
    			GPSUtil mGpsUtil = new GPSUtil();
    			ArrayList<Double> mList = mGpsUtil.getDis(longitude, latitude);
    			EventLoader mEventLoader = new EventLoader();
    			ArrayList<EventItem> lectureList = mEventLoader.loadEvent(1);
    			ArrayList<EventItem> actList = mEventLoader.loadEvent(0);
    			boolean lecture_notified = false;
    			boolean act_notified = false;
    			NotificationManager mNotificationManager = 
    					(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    			String nowtime = mTimeUtil.getTime();
    			
    			for (int i = 0; i < lectureList.size(); i++){
    				EventItem mItem = lectureList.get(i);
    				String stime = mItem.stime.substring(11,16);
    				if (mTimeUtil.calc(nowtime, stime) < 0.35){
    					for (int j = 0; j < 7; j++)
    					if (mItem.building.equals(c.CLASSBUILD[j])){
    						if (mList.get(j) < 50){
    							lecture_notified = true;
    							Intent lectureIntent = new Intent(MainActivity.this,ShowLecture.class);
    							PendingIntent mPendingIntent= 
    									PendingIntent.getActivity(MainActivity.this, 0, lectureIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    							Notification mNotification = new Notification.Builder(MainActivity.this).
    									setSmallIcon(R.drawable.ic_launcher).
    									setContentTitle("附近有讲座"). 
    									setContentText("点击查看").
    									setContentIntent(mPendingIntent).
    									setAutoCancel(true).
    									setDefaults(3).
    									build();
    							
    							mNotificationManager.notify(202, mNotification);
    							//notify lecture
    						}
    						break;
    					}
    				}
    				if (lecture_notified) break;
    			}
    			
    			for (int i = 0; i < actList.size(); i++){
    				EventItem mItem = actList.get(i);
    				String stime = mItem.stime.substring(11,16);
    				if (mTimeUtil.calc(nowtime, stime) < 0.35){
    					for (int j = 0; j < 7; j++)
    					if (mItem.building.equals(c.CLASSBUILD[j])){
    						if (mList.get(j) < 50){
    							act_notified = true;
    							Intent lectureIntent = new Intent(MainActivity.this,ShowAct.class);
    							PendingIntent mPendingIntent= 
    									PendingIntent.getActivity(MainActivity.this, 0, lectureIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    							Notification mNotification = new Notification.Builder(MainActivity.this).
    									setSmallIcon(R.drawable.ic_launcher).
    									setContentTitle("附近有活动"). 
    									setContentText("点击查看").
    									setContentIntent(mPendingIntent).
    									setAutoCancel(true).
    									setDefaults(3).
    									build();
    							
    							mNotificationManager.notify(203, mNotification);
    							
    							//notify act
    						}
    						break;
    					}
    				}
    				if (act_notified) break;
    			}
    			
    			//Toast.makeText(context, "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_SHORT).show();
    		}
    	}

    }
	
	public void onResume(){
    	super.onResume();
    	if (!updated) {
    		registerReceiver(mCustomReceiver, new IntentFilter("com.clover.LocationChangedBroadcast"));
    		mGpsService.getLocation();
    	}
    }
    
    
    public void onPause() {
    	super.onPause();
    	if (!updated){
    		unregisterReceiver(mCustomReceiver);
    		mGpsService.stopUsingGPS();
    	}
	}
	

	public boolean onOptionsItemSelected(MenuItem item)  
	{
		Intent intent = new Intent(MainActivity.this, SelectBuilding.class);
        startActivity(intent);
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if (isFree[arg2]) {
			Intent intent = new Intent(MainActivity.this, PlanEvent.class);
			intent.putExtra("updated", updated);
			intent.putExtra("lon", longitude);
			intent.putExtra("lat", latitude);
			intent.putExtra("start_unit", ClassList.get(arg2).startunit - 1);
			intent.putExtra("end_unit", ClassList.get(arg2).startunit + ClassList.get(arg2).units - 2);
			startActivity(intent);
		}
	}

}
