/* @file TimerService.java
 * 
 * TeaTimer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version. More info: http://www.gnu.org/licenses/
 *  
 * Copyright 2009 Ralph Gootee <rgootee@gmail.com>
 *  
 */

package goo.TeaTimer;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**
 * Countdown timer service
 * @author Ralph Gootee (rgootee@gmail.com)
 *
 */
public class TimerService extends Service
{
	private final IBinder mBinder = new TimerBinder();
	 
	/* Binder to set the handler */
	public class TimerBinder extends Binder 
	{    
		public void setHandler(Handler handle){
			mHandler = handle;
		}
	}

	private static final int UPDATE_INTERVAL = 1000;
 	private static final int HELLO_ID = 1;
 
 	NotificationManager mNM;
 	
	/** Timer object used for stopwatch logic**/
	private int mTime=0;
	
	/** The end time */
	private int mMax=0;

	/** increment for the timer */
	private Timer mTimer = null;
	
	/** Handler for dealing with updates **/
	private Handler mHandler;

	@Override public IBinder onBind(Intent intent) {		  
		return mBinder;
	}

	@Override public void onCreate() {
		super.onCreate();
		
		mHandler = new Handler();
		mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
	}

	@Override public void onStart(Intent intent,int x)
	{
		super.onStart(intent,x);
		mMax = intent.getIntExtra("Time", 0);
		startTimer(mMax);
	}
	
	
	@Override public void onDestroy() 
	{
		Log.i(getClass().getSimpleName(), "Timer Service Stopped.");		 
		super.onDestroy();
		stopTimer();
	}
	
	private void updateTimer()
	{
		mTime -= UPDATE_INTERVAL;
		
		if(mTime <= 0 )
		{
			showFinishedNotification();
			mTime = 0;
			stopTimer();
			stopSelf();
		}
		
		Message msg = new Message();
		msg.arg1 = mTime;
		msg.arg2 = mMax;
		
		if(mHandler != null){
			mHandler.sendMessage(msg);
		}
	}
	
	public int getTime(){ return mTime;}

	public void stopTimer()
	{					
		mTimer.cancel();
		Log.i(getClass().getSimpleName(), "Timer halted");	
	}

	public void startTimer(int time)
	{		
		mTime = time;
		mTimer = new Timer();
		
		mTimer.scheduleAtFixedRate(
	      		new TimerTask() {
	        		public void run() {
	          			updateTimer();
	        		}
	      		},
	      		0,
	      		UPDATE_INTERVAL);
	  		
	  	Log.i(getClass().getSimpleName(), "Timer started!!!");	
	}
	
	public void showFinishedNotification()
	{
		CharSequence text = getText(R.string.Notification);
		CharSequence textLatest = "Timer for " + time2humanStr(mMax);
		
        Notification notification = new Notification(R.drawable.notification,
        		text,
                System.currentTimeMillis());

		// Play a sound!
		Uri uri = Uri.parse("android.resource://goo.TeaTimer/" + R.raw.big_ben);
      	notification.sound = uri;
      	
      	Intent intent = new Intent(this,TimerActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,  0,intent, 0);

        notification.setLatestEventInfo(this, text,
                       textLatest, contentIntent);

        mNM.notify(HELLO_ID, notification);
	}

	/** Converts a millisecond time to a string time 
	 * @param time is the time in milliseconds
	 * @return the formated string
	 */
	static public String time2str(int time)
	{	
		int seconds = (int) (time / 1000);
		int minutes = seconds / 60;
		int hour = minutes / 60;

		minutes = minutes % 60;
   		seconds = seconds % 60;

		if(hour == 0){
			return String.format("%02d:%02d",minutes, seconds);
		}else{
			return String.format("%02d:%02d:%02d",hour,minutes, seconds);
		}
	}
	
	/**
	 * Returns the suggested text size for the string. A hack.
	 * @param str the time string
	 * @return the suggested text size to accommodate the string
	 */
	static public int textSize(String str)
	{
		if(str.length() > 5){ 
			return 50;
		}else{
			return 70;
		}
		
		
	}
		
	static public String time2humanStr(int time)
	{
		int seconds = (int) (time / 1000);
		int minutes = seconds / 60;
		int hour = minutes / 60;

		minutes = minutes % 60;
   		seconds = seconds % 60;
   		
   		String r = new String();
   		
   		// Ugly string formating
   		if(hour != 0){	
   			r += hour + " hour";					
   			if(hour == 1) r+= "s";
   			r+= " and ";
   		}
   		
   		r += minutes + " min";
   		if(minutes != 1) r+= "s";
		
   		return r;
	}
	
	public String getTimeString()
	{	
		return time2str(mTime);
	}
}
