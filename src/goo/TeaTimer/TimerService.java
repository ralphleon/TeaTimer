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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

/**
 * Countdown timer service
 * @author Ralph Gootee (rgootee@gmail.com)
 *
 */
public class TimerService extends Service
{
	private final String DEBUG = getClass().getSimpleName();
	
	public static final int UPDATE_INTERVAL = 1000;
 	private static final int HELLO_ID = 1;
 	
	/** Vibrate time */
	private int mVibrateTime = 500;
	
 	NotificationManager mNM;
 	
	/** Timer object used for stopwatch logic**/
	private int mTime=0;
	
	/** The end time */
	private int mMax=0;

	/** increment for the timer */
	private Timer mTimer = null;

	@Override 
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override public void onCreate() {
		super.onCreate();
		
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
		Log.i(DEBUG, "Destroying the Timer Service...");		 
		super.onDestroy();
		
		if(mTimer != null) mTimer.cancel();
	}
	
	private void updateTimer()
	{
		mTime -= UPDATE_INTERVAL;
		
		if(mTime <= 0 )
		{
			Log.v(DEBUG,"Timer has finished! Showing notification & halting service ...");
			showFinishedNotification();
			stopSelf();
		}
		
	}
	
	public int getTime(){ return mTime;}

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
	  		
	  	Log.i(getClass().getSimpleName(), "Timer started for " + time2humanStr(time) + "!");	
	}
	
	public void showFinishedNotification()
	{
		Log.v(DEBUG,"Showing Notification");
	
	 	SharedPreferences settings = getSharedPreferences("GooTimer",0);
        boolean play = settings.getBoolean("PlaySound",true);
        
		CharSequence text = getText(R.string.Notification);
		CharSequence textLatest = "Timer for " + time2humanStr(mMax);
		
        Notification notification = new Notification(R.drawable.notification,
        		text,
                System.currentTimeMillis());

        notification.defaults = Notification.DEFAULT_VIBRATE;
        
        // Have a light
        notification.ledARGB = 0xff00ff00;
        notification.ledOnMS = 300;
        notification.ledOffMS = 1000;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        
        // Play a sound!
        if(play){
			Uri uri = Uri.parse("android.resource://goo.TeaTimer/" + R.raw.big_ben);
	      	notification.sound = uri;
        }
        
      	Intent intent = new Intent(this,TimerActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,  0,intent, 0);

        notification.setLatestEventInfo(this, text,
                       textLatest, contentIntent);

        mNM.notify(HELLO_ID, notification);
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
	
	/** Converts a millisecond time to a string time 
	 * @param time is the time in milliseconds
	 * @return the formated string
	 */
	static public String time2str(int ms)
	{	
		int [] time = time2Mhs(ms);

		if(time[0] == 0){
			return String.format("%02d:%02d",time[1],time[2]);
		}else{
			return String.format("%02d:%02d:%02d",time[0],time[1],time[2]);
		}
	}
	
	static public int [] time2Mhs(int time)
	{
		int seconds = (int) (time / 1000);
		int minutes = seconds / 60;
		int hour = minutes / 60;

		minutes = minutes % 60;
   		seconds = seconds % 60;
   		
		int [] timeVec = new int[3];
		timeVec[0] = hour;
		timeVec[1] = minutes;
		timeVec[2] = seconds;
	
		return timeVec;
	}
	
	static public String time2humanStr(int time)
	{
		int [] timeVec = time2Mhs(time);
		int hour = timeVec[0], minutes=timeVec[1];
		
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
}
