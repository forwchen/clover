package com.clover4.beta;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
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
import android.app.Activity;
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
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemClickListener{

	
	private final String TAG = "TEST";
	private final String SUCC = "success message at--->";
	
	//Log.d(TAG, SUCC);
	private boolean updated = false;
	private double latitude = 0;
	private double longitude = 0;
	
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
	
	///SharedPrefernce key
	private final String IS_LOGGED_IN = "IS_LOGGED_IN";
	///SharedPrefernce key
	private final String IS_TABLE_DOWNLOADED = "IS_TABLE_DOWNLOADED";
	///SharedPrefernce key
	private final String CLASSROOM_INFO_DATE = "CLASSROOM_INFO_DATE";
	///SharedPrefernce key
	private final String EVENT_INFO_DATE = "EVENT_INFO_DATE";
	
	
	public boolean isNetworkOn() {
		ConnectivityManager connectivity = (ConnectivityManager) 
				MainActivity.this.getSystemService(MainActivity.this.CONNECTIVITY_SERVICE);
		if (connectivity.getActiveNetworkInfo() == null) return false;
		if (! connectivity.getActiveNetworkInfo().isConnected()) return false;
		return true;
	}
	
	/**
	 * 检查用户名密码是否合法
	 *
	 */
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
				String LoginURL = "http://61.129.42.58:9083/school/login?"+"user="+musrname+"&psw="+mpassword;
				
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
				Log.d(TAG, SUCC+"checking usr info");
				doMain();
			}
			else {
				Log.d(TAG, "wrong usrname or psword");
			}
		}
		
	}
	
	/**
	 * 登录并获取课程表信息
	 *
	 */
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
			    mArrayList.add(new BasicNameValuePair("IDToken1", musrname));
			    mArrayList.add(new BasicNameValuePair("IDToken2", mpassword));
			    mHttpPost.setEntity(new UrlEncodedFormEntity(mArrayList, "UTF-8"));
			    
			    mDefaultHttpClient.execute(mHttpPost);
			    
			    mHttpPost.abort();

			    HttpGet mHttpGet = new HttpGet("http://xk.fudan.edu.cn/fdeams/stdCourseTable.action?xq=2013201402");
			    HttpResponse mHttpResponse = mDefaultHttpClient.execute(mHttpGet);
			    if (mHttpResponse.getStatusLine().getStatusCode() == 200)
			    {
			    	result = EntityUtils.toString(mHttpResponse.getEntity());
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
				mSharedprefUtil.writeLong(IS_TABLE_DOWNLOADED, 1L);
				Log.d(TAG, SUCC+"logging in");
				StdTableUtil mStdTableUtil = new StdTableUtil(new StringBuffer(result));
				mStdTableUtil.storeTable();
				
				doInflate();
			}
			else{
				Log.d(TAG, "Failed downloading stdtable");
				Toast.makeText(getApplicationContext(), 
						"登录失败，请检查网络连接情况。\n 建议使用wifi连接", 
						Toast.LENGTH_LONG).show();
			}
		}

	
		@Override
		protected void onPreExecute(){
			mProgressDialog = new ProgressDialog(MainActivity.this);
			mProgressDialog.setMessage("正在登录...");
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
		}
	}

	/**
	 * 下载空教室信息
	 *
	 */
	public class fetchInfo extends AsyncTask<Void, Void, Boolean>{

		
		
		private Classroom[] classroom = new Classroom[300];
		private int classroomsum = 0;
		private String sclassroomname = "", 
					scodelesson = "", 
					susedflag = "", 
					sclasstime = "", 
					sclassroomid = "";
		
		
		@Override
		protected Boolean doInBackground(Void... params) 
		{
			// TODO Auto-generated method stub
			Boolean result = true;
			for (int i = 1; i < c.NUM_BUILDING; i++)
				for (int j = 0; j < c.NUM_TIME; j++){
					
					HttpResponse mHttpResponse = null;
					String url = "http://61.129.42.58:9083/sid/queryClassroomService/vid/buildDetail?"
							//+"year="+mTimeUtil.getyear()+"&month="+mTimeUtil.getmonth()+"&day="+mTimeUtil.getday()
							+"&timeFlag="+c.TIME[j]+"&idBuilding="+c.BUILDING[i]+"&returnType=android";
					
					String jsonData = new String();

					try 
					{
						HttpGet mHttpGet = new HttpGet(url);
						mHttpResponse = new DefaultHttpClient().execute(mHttpGet);
						if (mHttpResponse.getStatusLine().getStatusCode() == 200)
						{
							jsonData = EntityUtils.toString(mHttpResponse.getEntity());
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
					}
					classroom[i].toStr();
					
					for (int j = 0; j < c.NUM_BUILDING; j++){
						if (classroom[i].name.startsWith(c.BUILDING[j])){
							classroom[i].which = j + 1;
							break;
						}
					}
					
				}
				
				try {
					String dbPath = c.appdir+"/info.db";
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
							String createTable = "CREATE TABLE "+c.BUILDING[classroom[i].which-1]+
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
						
						mDB.insert(c.BUILDING[classroom[i].which-1], null, mContentValues);
						
					}
					end[N] = classroomsum;
					
					for (int i = 0; i <= N; i++){
						fw = new FileWriter(c.appdir+"/"+c.BUILDING[i]+".txt");
						
						for (int j = begin[i]; j <= end[i]; j++){
							fw.write(classroom[j].name+"\n");
							fw.write(classroom[j].used_str.substring(0, 5)+"\n");
							fw.write(classroom[j].used_str.substring(5, 10)+"\n");
							fw.write(classroom[j].used_str.substring(10, 14)+"\n");
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
				mSharedprefUtil.modifyString(CLASSROOM_INFO_DATE, mTimeUtil.getdate());
				Log.d(TAG, SUCC+"downloading classroom info");
			}
			else{
				Log.d(TAG, "Failed downloading classroom info");
				Toast.makeText(getApplicationContext(),
						"下载空教室信息失败，\n请检查网络连接。",
						Toast.LENGTH_LONG).show();
			}
		}
	}
	
	/**
	 * 下载讲座、活动信息
	 *
	 */
	public class fetchEvent extends AsyncTask<Void, Void, Boolean>{

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			HttpResponse mHttpResponse = null;

			String url = "http://lecture.oss-cn-hangzhou.aliyuncs.com/"+mTimeUtil.getdate();
			String Data = new String();
			Boolean result = true;
			try 
			{
				HttpGet mHttpGet = new HttpGet(url);
				mHttpResponse = new DefaultHttpClient().execute(mHttpGet);
				if (mHttpResponse.getStatusLine().getStatusCode() == 200)
				{
					Data = EntityUtils.toString(mHttpResponse.getEntity());
					
					FileWriter fw;
					fw = new FileWriter(c.appdir+"/cache");
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
				mSharedprefUtil.modifyString(EVENT_INFO_DATE, mTimeUtil.getdate());
				Log.d(TAG, SUCC+"downloading event info");
			}
			else {
				Log.d(TAG, "Failed downloading event info");
			}
		}
		
	}
	
	
	public void doInflate(){
		

		if ((mTimeUtil.getdayofweek() == 0) || (mTimeUtil.getdayofweek() == 6)) return;
		MainActivity.this.setContentView(R.layout.view_class_list);
		
		StdTableLoader mStdTableLoader = new StdTableLoader();
		ClassList = mStdTableLoader.getTable();
		String isfree = "0";
		for (int i = 0; i < ClassList.size(); i++){
			ClassTableItem mItem = ClassList.get(i);
			if (mItem.type == 1) {
				for (int j = 0; j < mItem.units; j++) isfree = isfree + "0";
				isFree[i] = false;
			}
			else {
				for (int j = 0; j < mItem.units; j++) isfree = isfree + "1";
				isFree[i] = true;
			}
		}
		isfree = isfree + "0";
		
		mSharedprefUtil.modifyString("ISFREE", isfree);
		
		Intent mIntent = new Intent(this,GpsService.class);
		startService(mIntent);
		
		ListView mListView = (ListView) findViewById(R.id.classlist);
		TableAdapter mTableAdapter = new TableAdapter(MainActivity.this,ClassList);
		mListView.setAdapter(mTableAdapter);
		
		mListView.setOnItemClickListener(MainActivity.this);
		
		
		String EventInfoDate = mSharedprefUtil.readString(EVENT_INFO_DATE);

		if (!EventInfoDate.equals(mTimeUtil.getdate())) {
			try {
				File mFile = new File(c.appdir+"/cache");
				if (mFile.exists()) mFile.delete();
				Log.d(TAG, "old cache file deleted");
			}
			catch (Exception e){
				e.printStackTrace();
			}
			fetchEvent mfetchEvent = new fetchEvent();
			mfetchEvent.execute((Void) null);
		}
	}
	
	public void doMain(){
		MainActivity.this.setContentView(R.layout.activity_main);
				
		mSharedprefUtil.writeLong(IS_LOGGED_IN, 1L);
		Long isTableDownloaded = mSharedprefUtil.readLong(IS_TABLE_DOWNLOADED);
		if (isTableDownloaded == 0){
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

		
		//创建应用程序目录
		File f= new File(c.appdir);
		
		if (f.exists()) {
			Log.d(TAG, SUCC+"creating app dir");
		}
		else f.mkdir();
		
		//将location放入程序目录
		try
		{
			InputStream is = getResources().openRawResource(R.raw.location);
			FileOutputStream fos = 
				new FileOutputStream(c.appdir+"/location.db");
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
		
		//开始下载空教室信息
		mSharedprefUtil = new SharedprefUtil("setting",getApplicationContext());
		
		String ClassroomInfoDate = mSharedprefUtil.readString(CLASSROOM_INFO_DATE);
		if (!ClassroomInfoDate.equals(mTimeUtil.getdate())){
			//删除过期文件
			try {
				File mFile = new File(c.appdir+"/HGD.txt");
				if (mFile.exists()) mFile.delete();
				mFile = new File(c.appdir+"/HGX.txt");
				if (mFile.exists()) mFile.delete();
				mFile = new File(c.appdir+"/H2.txt");
				if (mFile.exists()) mFile.delete();
				mFile = new File(c.appdir+"/H3.txt");
				if (mFile.exists()) mFile.delete();
				mFile = new File(c.appdir+"/H4.txt");
				if (mFile.exists()) mFile.delete();
				mFile = new File(c.appdir+"/H5.txt");
				if (mFile.exists()) mFile.delete();
				mFile = new File(c.appdir+"/H6.txt");
				if (mFile.exists()) mFile.delete();
				Log.d(TAG, "old file deleted");
			} catch (Exception e) {
				// TODO: handle exception
			}
			if (! isNetworkOn()){
				Toast.makeText(getApplicationContext(),
						"下载空教室信息失败，\n请检查网络连接。",
						Toast.LENGTH_LONG).show();
			}
			else {
				fetchInfo mfetchInfo = new fetchInfo();
				mfetchInfo.execute((Void)null);
			}
		}
		else{
			
		}
		
		
		//开始登录
		Long loginStatus = mSharedprefUtil.readLong(IS_LOGGED_IN);
		
		if (loginStatus == 0){
			
			setContentView(R.layout.view_main_login);
			mpwdText = (EditText) findViewById(R.id.pwdtext);
			musrText = (EditText) findViewById(R.id.usrtext);
			mLoginButton = (Button) findViewById(R.id.loginbtn);
			
			final String pwdHint = mpwdText.getHint().toString();
			mpwdText.setOnFocusChangeListener(new OnFocusChangeListener(){

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
					if (hasFocus){
						mpwdText.setHint(null);
					}
					else {
						mpwdText.setHint(pwdHint);
					}
				}
			});
			
			final String usrHint = musrText.getHint().toString();
			musrText.setOnFocusChangeListener(new OnFocusChangeListener(){

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
					if (hasFocus){
						musrText.setHint(null);
					}
					else {
						musrText.setHint(usrHint);
					}
				}
			});
			
			mLoginButton.setOnClickListener(
				new View.OnClickListener() {
				
					@Override
					public void onClick(View view) {
						
						if (!isNetworkOn()) {
							Toast.makeText(getApplicationContext(), 
									"请打开网络，建议使用wifi连接。",
									Toast.LENGTH_SHORT).show();
							return;
						}
						
						if (musrText.getText().toString().length() != 11){
							Toast.makeText(getApplicationContext(), "请检查您的学号是否正确", Toast.LENGTH_LONG).show();
							return;
						}
//						InputMethodManager  mInputMethodManager = (InputMethodManager)
//								MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
//						mInputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); 
	        
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
							Toast.makeText(getApplicationContext(), "请检查您的学号和密码是否正确", Toast.LENGTH_LONG).show();

						}
					}
				});
		}
		else {

			doMain();
		}
		
		
	}
	

	public void onResume(){
    	super.onResume();
    	
    }
    
    
    public void onPause() {
    	super.onPause();
    	
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
			intent.putExtra("start_unit", ClassList.get(arg2).startunit - 1);
			intent.putExtra("end_unit", ClassList.get(arg2).startunit + ClassList.get(arg2).units - 2);
			startActivity(intent);
		}
	}

}
