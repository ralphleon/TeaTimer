/* @file TimerActivity.java
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

import goo.TeaTimer.Animation.TimerAnimation;
import goo.TeaTimer.widget.NNumberPickerDialog;
import goo.TeaTimer.widget.NumberPicker;
import goo.TeaTimer.widget.NNumberPickerDialog.OnNNumberPickedListener;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The main activity which shows the timer and allows the user to set the time
 * @author Ralph Gootee (rgootee@gmail.com)
 */
public class TimerActivity extends Activity implements OnClickListener,OnNNumberPickedListener
{
	/** All possible timer states */
	private final static int RUNNING=0, STOPPED=1, PAUSED=2;
	
	/** Should the logs be shown */
	private final static boolean LOG = true;
	
	/** Menu item ids */
	private final static int PREF=0;
	
	/** Macros for our dialogs */
	private final static int NUM_PICKER_DIALOG = 0, ALERT_DIALOG = 1;
	/** debug string */
	private final String TAG = getClass().getSimpleName();
	
	/** Update rate of the internal timer */
	private final int TIMER_TIC = 100;
	
	/** The timer's current state */
	private int mCurrentState = -1;
	
	/** The maximum time */
	private int mLastTime = 0;
	
	/** The current timer time */
	private int mTime = 0;
	
	/** Internal increment class for the timer */
	private Timer mTimer = null;

	/** Handler for the message from the timer service */
	private Handler mHandler = new Handler() {
		
		@Override
        public void handleMessage(Message msg) {
			
			// The timer is finished
			if(msg.arg1 <= 0){
				
				if(mTimer != null){
					if(LOG) Log.v(TAG,"rcvd a <0 msg = " + msg.arg1);
					
					Context context = getApplicationContext();
					CharSequence text = getResources().getText(R.string.Notification);
					Toast.makeText(context, text,Toast.LENGTH_SHORT).show();
					
					timerStop();

					
				}
				
			// Update the time
			}else{
				mTime = msg.arg1;
				
				//enterState(RUNNING);
				onUpdateTime();
			}
		}
    };

	/** To save having to traverse the view tree */
	private ImageButton mPauseButton, mCancelButton;
	private Button mSetButton;
	private TimerAnimation mTimerAnimation;
	private TextView mTimerLabel;
	
	private Bitmap mPlayBitmap,mPauseBitmap;

	private AlarmManager mAlarmMgr;

	private PendingIntent mPendingIntent;

	private AudioManager mAudioMgr;

	private SharedPreferences mSettings;
    
	/** Called when the activity is first created.
     *	{ @inheritDoc} 
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {    	
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		mCancelButton = (ImageButton)findViewById(R.id.cancelButton);
        mCancelButton.setOnClickListener(this);
        
		mSetButton = (Button)findViewById(R.id.setButton);
        mSetButton.setOnClickListener(this);
       
        mPauseButton = (ImageButton)findViewById(R.id.pauseButton);
        mPauseButton.setOnClickListener(this);
 
        mPauseBitmap = BitmapFactory.decodeResource(
        		getResources(), R.drawable.pause);
        
        mPlayBitmap = BitmapFactory.decodeResource(
        		getResources(), R.drawable.play);
   
		mTimerLabel = (TextView)findViewById(R.id.label); 

		mTimerAnimation = (TimerAnimation)findViewById(R.id.imageView);

        enterState(STOPPED);
      
        // Store some useful values
        mSettings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        mAlarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        mAudioMgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
    }
    

    /** { @inheritDoc} */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	MenuItem item = menu.add(0, PREF, 0, getResources().getString(R.string.prefs));
    	item.setIcon(android.R.drawable.ic_menu_preferences);
    	
    	return super.onCreateOptionsMenu(menu);
    }
    

    /** { @inheritDoc} */
	@Override 
	public boolean onOptionsItemSelected(MenuItem item) 
	{  
		switch(item.getItemId()){
		
			case PREF:
				startActivity(new Intent(this, TimerPrefActivity.class));	
				break;
			default:
				return false;
		}
		
		return true;
	}
    

	/** { @inheritDoc} */
    @Override 
    public void onPause()
    {
    	super.onPause();
    	
    	// Save our settings
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt("LastTime", mLastTime);
        editor.putInt("CurrentTime",mTime);
        editor.putInt("DrawingIndex",mTimerAnimation.getIndex());
        editor.putInt("State", mCurrentState);
        
        switch(mCurrentState){
        
        	case RUNNING:
        	{
	        	if(mTimer != null){
	        		mTimer.cancel();
	        		editor.putLong("TimeStamp", new Date().getTime() + mTime);
	        	}	
        	}break;
        	
        	case STOPPED:
        	case PAUSED:
        	{
        		editor.putLong("TimeStamp", 1);
        	}break;
        }
        
        editor.commit();
    }
   

    /** {@inheritDoc} */
    @Override 
    public void onResume()
    {
      	super.onResume();
	    		
    	// check the timestamp from the last update and start the timer.
    	// assumes the data has already been loaded?   
        mLastTime = mSettings.getInt("LastTime",0);    
        
        mTimerAnimation.setIndex(mSettings.getInt("DrawingIndex",0));
        int state = mSettings.getInt("State",0);
        
        switch(state)
        {
        	case RUNNING:
        	{
        		long timeStamp = mSettings.getLong("TimeStamp", -1);
                
        		Date now = new Date();
        		Date then = new Date(timeStamp);
            	
            	// We still have a timer running!
            	if(then.after(now)){
            		int delta = (int)(then.getTime() - now.getTime());		
            		timerStart(delta,false);
            		mCurrentState = RUNNING;
            	// All finished
            	}else{
            		clearTime();
            		enterState(STOPPED);
            	}
        	}break;
        	
        	case STOPPED:
        	{
        		enterState(STOPPED);
        	}break;
        	
        	case PAUSED:
        	{
        		mTime = mSettings.getInt("CurrentTime",0);
        		onUpdateTime();
        		enterState(PAUSED);
        	}break;
        	
        }
	}
   
 
    /**
     * Updates the time 
     */
	public void onUpdateTime(){
		
    	updateLabel(mTime);
    	mTimerAnimation.updateImage(mTime,mLastTime);  	
    }
	
	
    /**
     * Updates the text label with the given time
     * @param time in milliseconds
     */
	public void updateLabel(int time){
		
		String str = TimerUtils.time2str(time);
		int size = TimerUtils.textSize(str);
		mTimerLabel.setTextSize(size);
		mTimerLabel.setText(str);
	}

	
	/** {@inheritDoc} */
	@Override
	protected Dialog onCreateDialog(int id) 
	{
		Dialog d = null;
		
		switch(id){
		
			case NUM_PICKER_DIALOG:
			{
				int [] timeVec = TimerUtils.time2Mhs(mLastTime);
				int [] init = {timeVec[0],timeVec[1],timeVec[2]};
				int [] inc = {1,1,1};
				int [] start = {0,0,0};
				int [] end = {23,59,59};
				String [] sep = {":",".",""};
				
				NumberPicker.Formatter  [] format = {	NumberPicker.TWO_DIGIT_FORMATTER,
														NumberPicker.TWO_DIGIT_FORMATTER,
														NumberPicker.TWO_DIGIT_FORMATTER};
				
				d = new NNumberPickerDialog(	this, this, getResources().getString(R.string.InputTitle), 
												init, inc, start, end, sep,format);
			}break;
			
			case ALERT_DIALOG:
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(getResources().getText(R.string.warning_text))
				       .setCancelable(false)
				       .setPositiveButton(getResources().getText(R.string.warning_button), null)
				       .setTitle(getResources().getText(R.string.warning_title));
				       
				d = builder.create();
				
			}break;	
		}
		
		return d;
	}
	
	
	/** {@inheritDoc} */
	@Override
	protected void onPrepareDialog(int id,Dialog d)
	{
		switch(id){
		
			case NUM_PICKER_DIALOG:
			{
				int [] timeVec = TimerUtils.time2Mhs(mLastTime);
				int [] init = {timeVec[0],timeVec[1],timeVec[2]};
				
				NNumberPickerDialog dialog = (NNumberPickerDialog)d;
				dialog.setInitialValues(init);
			}		
		}
		
		super.onPrepareDialog(id, d);
	}
	
	
	/** 
	 * Callback for the number picker dialog
	 */
	public void onNumbersPicked(int[] number)
	{
		int hour = number[0];
		int min = number[1];
		int sec = number[2];
		
		mLastTime = hour*60*60*1000 + min*60*1000 + sec*1000;
		
		// Check to make sure the phone isn't set to silent
		boolean silent = (mAudioMgr.getRingerMode() == AudioManager.RINGER_MODE_SILENT);
		String noise = mSettings.getString("NotificationUri","");
		boolean vibrate = mSettings.getBoolean("Vibrate",true);
        boolean nag = mSettings.getBoolean("NagSilent",true);
       
        // If the conditions are _just_ right show a nag screen
		if(nag && silent && (noise != "" || vibrate) ){
			showDialog(ALERT_DIALOG);
		}
		
		timerStart(mLastTime,true);
	}


	/** 
	 * This only refers to the visual state of the application, used to manage
	 * the view coming back into focus.
	 * 
	 * @param state the visual state that is being entered
	 */
	private void enterState(int state){
		
		if(mCurrentState != state){
			
			mCurrentState = state;		
			if(LOG) Log.v(TAG,"Set current state = " + mCurrentState);
			
			switch(state)
			{
				case RUNNING:
				{
					mSetButton.setVisibility(View.GONE);
					mCancelButton.setVisibility(View.VISIBLE);
					mPauseButton.setVisibility(View.VISIBLE);
					mPauseButton.setImageBitmap(mPauseBitmap);
				}break;
		
				case STOPPED:
				{	
					mPauseButton.setVisibility(View.GONE);
					mCancelButton.setVisibility(View.GONE);
					mSetButton.setVisibility(View.VISIBLE);	
					clearTime();
				
				}break;
		
				case PAUSED:
				{
					mSetButton.setVisibility(View.GONE);
					mPauseButton.setVisibility(View.VISIBLE);
					mCancelButton.setVisibility(View.VISIBLE);
					mPauseButton.setImageBitmap(mPlayBitmap);
				}break;	
			}
		}
	}
	
	/**
	 * Cancels the alarm portion of the timer
	 */
	private void stopAlarmTimer(){
		if(LOG) Log.v(TAG,"Stopping the alarm timer ...");		
		mAlarmMgr.cancel(mPendingIntent);
	}
	
	/**
	 * Stops the timer
	 */
	private void timerStop()
	{		
		if(LOG) Log.v(TAG,"Timer stopped");
		
		clearTime();
		
		// Stop our timer service
		enterState(STOPPED);		
		mTimer.cancel();
		
		// Remove the wakelock
		//PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		//pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
	}
	
	/**
	 * Starts the timer at the given time
	 * @param time with which to count down
	 * @param service whether or not to start the service as well
	 */
	private void timerStart(int time,boolean service)
	{
		if(LOG) Log.v(TAG,"Starting the timer...");
		
		// Star external service
		enterState(RUNNING);
		
		if(service){
		    if(LOG) Log.v(TAG,"Starting the timer service ...");
		    Intent intent = new Intent( getApplicationContext(), TimerReceiver.class);
		    intent.putExtra("SetTime",mLastTime);
		    mPendingIntent = PendingIntent.getBroadcast( getApplicationContext(), 0 , intent, PendingIntent.FLAG_CANCEL_CURRENT);
		    mAlarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + time, mPendingIntent);	    
		}
		
		// Internal thread to properly update the GUI
		mTimer = new Timer();	
		mTime = time;
		mTimer.scheduleAtFixedRate( new TimerTask(){
	        	public void run() {
	          		timerTic();
	        	}
	      	},
	      	0,
	      	TIMER_TIC);
	}
	
	/** Resume the time after being paused */
	private void resumeTimer() 
	{
		if(LOG) Log.v(TAG,"Resuming the timer...");
			
		timerStart(mTime,true);
		enterState(RUNNING);
	}
	
	/** Pause the timer and stop the timer service */
	private void pauseTimer()
	{
		if(LOG) Log.v(TAG,"Pausing the timer...");
		
		mTimer.cancel();
		mTimer = null;
		
		stopAlarmTimer();
		
		enterState(PAUSED);
	}

	/** Called whenever the internal timer is updated */
	protected void timerTic() 
	{
		mTime -= TIMER_TIC;
		
		if(mHandler != null){
			Message msg = new Message();
			msg.arg1 = mTime;			
			mHandler.sendMessage(msg);
		}
	}
	
	/** Clears the time, sets the image and label to zero */
	private void clearTime()
	{
		mTime = 0;
		onUpdateTime();
	}

	/** {@inheritDoc} */
	public void onClick(View v) 
	{
		switch(v.getId()){
		
			case R.id.setButton:
			{
				showDialog(NUM_PICKER_DIALOG);		
			}break;
			
			case R.id.pauseButton:
			{
				switch(mCurrentState){
					case RUNNING:
						pauseTimer();
						break;
					case PAUSED:
						resumeTimer();
						break;
				}		
			}break;
			
			case R.id.cancelButton:
			{
				// We need to be careful to not cancel timers
				// that are not running (e.g. if we're paused)
				switch(mCurrentState){
					case RUNNING:
						timerStop();
						stopAlarmTimer();
						break;
					case PAUSED:
						clearTime();
						enterState(STOPPED);
						break;
				}	
			}break;
		}
	}
		
}
