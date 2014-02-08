package com.clover4.beta;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

import com.clover4.beta.utils.*;

import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.R.integer;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
	
	
	private EditText mpwdText;
	private EditText musrText;
	private Button mLoginButton;
	private String mpassword;
	private String musrname;
	private SharedprefUtil mSharedprefUtil;
	private ProgressDialog mProgressDialog;
	private boolean isFree[]= new boolean[16];
	
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

		final String[] CLASSBUILD = {"HGD", "HGX", "H2", "H3", "H4", "H5", "H6"}; 
		final String[] CLASSTIMER = {"morning", "midday", "night"};
		final int NUMOFCLASSBUILD= 7;
		final int NUMOFCLASSTIMER = 3;
		
		private Classroom[] classroom = new Classroom[300];
		private int classroomsum = 0;
		private String sclassroomname = "", scodelesson = "", susedflag = "", sclasstime = "", sclassroomid = "";
		
		@Override
		protected Boolean doInBackground(Void... params) 
		{
			// TODO Auto-generated method stub
			Boolean result = true;
			for (int i = 1; i < NUMOFCLASSBUILD; i++)
				for (int j = 0; j < NUMOFCLASSTIMER; j++){
					
					HttpResponse mHttpResponse = null;
					String url = "http://61.129.42.58:9083/sid/queryClassroomService/vid/buildDetail?year=2013&month=11&day=13&timeFlag="+CLASSTIMER[j]+"&idBuilding="+CLASSBUILD[i]+"&returnType=android";
					String jsonData = new String();

					try 
					{
						HttpGet mHttpGet = new HttpGet(url);
						mHttpResponse = new DefaultHttpClient().execute(mHttpGet);
						if (mHttpResponse.getStatusLine().getStatusCode() == 200)
						{
							jsonData = EntityUtils.toString(mHttpResponse.getEntity());
							System.out.println(CLASSBUILD[i]+" at "+CLASSTIMER[j]+" is parsing");
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
					
					for (int j = 0; j < NUMOFCLASSBUILD; j++){
						if (classroom[i].name.startsWith(CLASSBUILD[j])){
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
							String createTable = "CREATE TABLE "+CLASSBUILD[classroom[i].which-1]+
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
						
						mDB.insert(CLASSBUILD[classroom[i].which-1], null, mContentValues);
						
					}
					end[N] = classroomsum;
					
					for (int i = 0; i <= N; i++){
						fw = new FileWriter(android.os.Environment.getExternalStorageDirectory()
						+"/clover/"+CLASSBUILD[i]+".txt");
						
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
				mSharedprefUtil.writeLong("isInfoUptoDate", 1L);
				setNotification();
			}
			else{
				Toast.makeText(getApplicationContext(), "Fetching classroom info failed", Toast.LENGTH_LONG);
				
			}
		}
	}
	
	public void setNotification(){
		
	}
	
	public void doInflate(){
		TimeUtil mTimeUtil = new TimeUtil();

		if ((mTimeUtil.dayofweek == 0) || (mTimeUtil.dayofweek == 6)) return;
		MainActivity.this.setContentView(R.layout.class_list);
		
		StdTableLoader mStdTableLoader = new StdTableLoader();
		ArrayList<ClassTableItem> ClassList = mStdTableLoader.getTable();
		
		for (int i = 0; i < ClassList.size(); i++){
			if (ClassList.get(i).type == 1) isFree[i] = false;
			else isFree[i] = true;
		}
		
		ListView mListView = (ListView) findViewById(R.id.classlist);
		TableAdapter mTableAdapter = new TableAdapter(MainActivity.this,ClassList);
		mListView.setAdapter(mTableAdapter);
		
		mListView.setOnItemClickListener(MainActivity.this);
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
		

		
		
		File f= new File(android.os.Environment.getExternalStorageDirectory()+"/clover");
		
		if (f.exists()) {
			Log.d(TAG, SUCC);
		}
		else f.mkdir();
		
		
		mSharedprefUtil = new SharedprefUtil("setting",getApplicationContext());
		
		Long classroomInfo = mSharedprefUtil.readLong("isInfoUptoDate");
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
			setNotification();
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
			setContentView(R.layout.main_login);
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
			//setContentView(R.layout.activity_main);
			doMain();
		}
		/**
		 * make app dir
		 * 
		 */
		
		
		
		/**
		 * set notification
		 */
//		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//		Intent openIntent = new Intent(this,ShowNearest.class);
//		
//		PendingIntent mPendingIntent= PendingIntent.getActivity(this, 0, openIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//		Notification mNotification = new Notification.Builder(this).
//				setSmallIcon(R.drawable.ic_launcher).
//				setOngoing(true).
//				setContentTitle("Cl0ver4"). 
//				setContentText("show nearest spare classroom").
//				setContentIntent(mPendingIntent).
//				build();
//		
//		mNotificationManager.notify(202, mNotification);
		
		
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
//		if (isFree[arg2]) {
//			Intent intent = new Intent(MainActivity.this, Editevent.class);
//			startActivity(intent);
//		}
	}

}
