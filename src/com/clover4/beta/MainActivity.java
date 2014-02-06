package com.clover4.beta;

import java.io.File;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


import com.clover4.utils.SharedprefHelper;



import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	
	private final String TAG = "TEST";
	private final String SUCC = "success message";
	
	//Log.d(TAG, SUCC);
	
	
	private EditText mpwdText;
	private EditText musrText;
	private Button mLoginButton;
	private String mpassword;
	private String musrname;
	private SharedprefHelper mSharedprefHelper;
	
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
				//String LoginURL = "http://61.129.42.58:9083/school/login?user=13307130195&psw=6gjpq3a";
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
			    	result =false;
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
	
	public boolean isNetworkOn() {
		ConnectivityManager connectivity = (ConnectivityManager) 
				MainActivity.this.getSystemService(MainActivity.this.CONNECTIVITY_SERVICE);
		if (connectivity.getActiveNetworkInfo() == null) return false;
		if (! connectivity.getActiveNetworkInfo().isConnected()) return false;
		return true;
	}
	
	public class getStdTable extends AsyncTask<Void, Void, Boolean>{

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub

			try {
				HttpPost localHttpPost = new HttpPost("http://uis1.fudan.edu.cn/amserver/UI/Login");
			
				DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
			    localDefaultHttpClient.getParams().setParameter("http.protocol.allow-circular-redirects", Boolean.valueOf(true));
			    ArrayList localArrayList = new ArrayList();
			    localArrayList.add(new BasicNameValuePair("IDToken1", "13307130195"));
			    localArrayList.add(new BasicNameValuePair("IDToken2", "6gjpq3a"));
			    localHttpPost.setEntity(new UrlEncodedFormEntity(localArrayList, "UTF-8"));
			    
			    System.out.println("start");
			    
			    localDefaultHttpClient.execute(localHttpPost);
			
			    localHttpPost.abort();
			    
			    System.out.println("logged in");
			    
			    HttpGet localHttpGet = new HttpGet("http://xk.fudan.edu.cn/fdeams/stdCourseTable.action?xq=2013201402");
			    HttpResponse localHttpResponse = localDefaultHttpClient.execute(localHttpGet);
			    if (localHttpResponse.getStatusLine().getStatusCode() == 200)
			    {
			  
			      String str = EntityUtils.toString(localHttpResponse.getEntity());
			      System.out.println(str.replaceAll("\r", ""));

			    }
			    localHttpGet.abort();

			} catch (Exception e) {
				// TODO: handle exception
			}
	    
		
			return null;
	
		}
	
		protected void onPostExecute(Boolean result){
		
		}

	
}
	
	public void doMain(){
		setContentView(R.layout.activity_main);
		mSharedprefHelper.writeLong("logged_in", 1L);
		Long stdTableStatus = mSharedprefHelper.readLong("table_downloaded");
		if (stdTableStatus == 0){
			getStdTable mgetStdTable = new getStdTable();
			mgetStdTable.execute((Void)null);
		}
		else{
			
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
		
		mSharedprefHelper = new SharedprefHelper("setting",getApplicationContext());
		Long loginStatus = mSharedprefHelper.readLong("logged_in");
		
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
