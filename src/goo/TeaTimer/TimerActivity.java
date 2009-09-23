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
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The main activity which shows the timer and allows the user to set the time
 * @author Ralph Gootee (rgootee@gmail.com)
 */
public class TimerActivity extends Activity implements OnClickListener,OnNNumberPickedListener
{
	/** All possible timer states */
	private enum State{ RUNNING, STOPPED, PAUSED };
		
	/** debug string */
	private final String DEBUG_STR = getClass().getSimpleName();
	
	/** Update rate of the internal timer */
	private final int TIMER_TIC = 500;
	
	/** The timer's current state */
	private State mCurrentState = State.STOPPED;
	
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
			
			if(msg.arg1 <= 0){
				onTimerStop();
				
				// Show a finished toast since the view was in focus!
				Context context = getApplicationContext();
				CharSequence text = "Timer Finished!";
				Toast.makeText(context, text,Toast.LENGTH_SHORT).show();
			}
			else{
				mTime = msg.arg1;
				
				enterState(State.RUNNING);
				onUpdateTime();
			}
		}
    };

	private ImageButton mPauseButton;

	private Bitmap mClearBitmap, mTimeBitmap,mPlayBitmap,mPauseBitmap;
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	//Log.v(DEBUG_STR,"Activity has been created...");
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		Button startButton = (Button)findViewById(R.id.stop);
        startButton.setOnClickListener(this);
       
        mPauseButton = (ImageButton)findViewById(R.id.pauseButton);
        mPauseButton.setOnClickListener(this);
        
        mPauseBitmap = BitmapFactory.decodeResource(
        		getResources(), R.drawable.ic_media_pause);
        
        mPlayBitmap = BitmapFactory.decodeResource(
        		getResources(), R.drawable.ic_media_play);
        
        mTimeBitmap = BitmapFactory.decodeResource(
        		getResources(), R.drawable.ic_dialog_time);
         
        mClearBitmap = BitmapFactory.decodeResource(
        		getResources(), R.drawable.ic_delete);
        
        mPauseButton.setImageBitmap(mPauseBitmap);
        
        enterState(State.STOPPED);
        
        clearTime();
    }
    
    /** { @inheritDoc} */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
  
    	MenuItem item = menu.add(0, 0, 0, "Preferences");
    	item.setIcon(android.R.drawable.ic_menu_preferences);  
    	return super.onCreateOptionsMenu(menu);
    }
    
    /** when menu button option selected */
	@Override public boolean onOptionsItemSelected(MenuItem item) {
	  
		// Only one item
		startActivity(new Intent(this, TimerPrefActivity.class));
		
		return true;
	}
    
    @Override 
    public void onPause()
    {
    	super.onPause();
    	
    	TimerAnimation i = (TimerAnimation)findViewById(R.id.imageView);
    
    	// Save our settings
    	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("LastTime", mLastTime);
        editor.putInt("DrawingIndex",i.getIndex());
        
        // Cancel our thread
    	if(mTimer != null){
    		mTimer.cancel();
    		editor.putLong("TimeStamp", new Date().getTime() + mTime);
    	}else{
    		editor.putBoolean("Running",false);
    		editor.putLong("TimeStamp", -1);
    	}
    	
        editor.commit();
        
    }
    
    @Override 
    public void onResume()
    {
    	TimerAnimation i = (TimerAnimation)findViewById(R.id.imageView);
    	
    	super.onResume();
    	
    	// check the timestamp from the last update and start the timer.
    	// assumes the data has already been loaded?
    	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    	   
        mLastTime = settings.getInt("LastTime",0);
        
        i.setIndex(settings.getInt("DrawingIndex",0));
        
        long timeStamp = settings.getLong("TimeStamp", -1);
        
        if(timeStamp != -1){
        	
        	Date now = new Date();
        	Date then = new Date(timeStamp);
        	
        	// We stil have a timer running!
        	if(then.after(now)){
        		int delta = (int)(then.getTime() - now.getTime());		
        		onTimerStart(delta,false);
        	
        	// All finished
        	}else{
        		clearTime();
        		enterState(State.STOPPED);
        	}
        }
    }
    
    /**
     * Updates the time 
     */
	public void onUpdateTime()
    {
    	updateLabel(mTime);
		
    	TimerAnimation i = (TimerAnimation)findViewById(R.id.imageView);
    	i.updateImage(mTime,mLastTime);  	
    }
	
    /**
     * Updates the text label with the given time
     * @param time in milliseconds
     */
	public void updateLabel(int time)
	{
		TextView label = (TextView)findViewById(R.id.label); 
		
		String str = TimerService.time2str(time);
		int size = TimerService.textSize(str);
		label.setTextSize(size);
		label.setText(str);
	}

	
	/** {@inheritDoc} */
	@Override
	protected Dialog onCreateDialog(int id) 
	{
		int [] timeVec = TimerService.time2Mhs(mLastTime);
		int [] init = {timeVec[0],timeVec[1],timeVec[2]};
		int [] inc = {1,1,1};
		int [] start = {0,0,0};
		int [] end = {23,59,59};
		String [] sep = {":",".",""};
		
		Log.v("Tea","create: " + init[0] +","+init[1]+","+init[2]);
		
		NumberPicker.Formatter  [] format = {	NumberPicker.TWO_DIGIT_FORMATTER,
												NumberPicker.TWO_DIGIT_FORMATTER,
												NumberPicker.TWO_DIGIT_FORMATTER};
		
		return new NNumberPickerDialog(	this, this, getResources().getString(R.string.InputTitle), 
										init, inc, start, end, sep,format);
	}
	
	@Override
	protected void onPrepareDialog(int x,Dialog d)
	{
		int [] timeVec = TimerService.time2Mhs(mLastTime);
		int [] init = {timeVec[0],timeVec[1],timeVec[2]};
		
		NNumberPickerDialog dialog = (NNumberPickerDialog)d;
		dialog.setInitialValues(init);

		super.onPrepareDialog(x, d);
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
		
		onTimerStart(mLastTime,true);
	}

	/** 
	 * This only refers to the visual state of the application, used to manage
	 * the view coming back into focus.
	 * 
	 * @param state the visual state that is being entered
	 */
	private void enterState(State state)
	{
		ImageButton pause = (ImageButton)findViewById(R.id.pauseButton);
		
		switch(state)
		{
			case RUNNING:
			{
				Button b = (Button)findViewById(R.id.stop);
				b.setText(R.string.Stop);	
				b.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_delete, 0, 0, 0);
				
				pause.setVisibility(View.VISIBLE);
				pause.setImageBitmap(mPauseBitmap);
			}break;
		
			case STOPPED:
			{	
				Button b = (Button)findViewById(R.id.stop);
				pause.setVisibility(View.GONE);
				
				b.setText(R.string.Start);
				b.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dialog_time, 0, 0, 0);
				clearTime();
				
			}break;
		
			case PAUSED:
			{
				pause.setImageBitmap(mPlayBitmap);
			}break;	
		}
			
		mCurrentState = state;
	}
	
	private void onTimerStop()
	{
		// Stop our timer service
		enterState(State.STOPPED);		
		Intent svc = new Intent(this, TimerService.class);
		stopService(svc);
		
		// Stop our local timer
		mTimer.cancel();
	}

	private void onTimerStart(int time,boolean service)
	{
		Log.v(DEBUG_STR,"Starting the timer...");
		
		// Star external service
		enterState(State.RUNNING);
		
		if(service){
			Intent svc = new Intent(this, TimerService.class);
		    svc.putExtra("Time",time);
		    svc.putExtra("OriginalTime", mLastTime);
			startService(svc);
		}
		
		// Internal thread to properly update the GUI
		mTimer = new Timer();	
		mTime = time;
		mTimer.scheduleAtFixedRate( new TimerTask() {
	        		public void run() {
	          			onTimerTic();
	        		}
	      		},
	      		0,
	      		TIMER_TIC);
	}

	/** Called whenever the internal timer is updated */
	protected void onTimerTic() 
	{
		mTime -= TIMER_TIC;
		
		if(mHandler != null){
			Message msg = new Message();
			msg.arg1 = mTime;			
			mHandler.sendMessage(msg);
		}
	}

	private void clearTime()
	{
		mTime = 0;
		onUpdateTime();
	}

	public void onClick(View v) 
	{
		// User pressed the start/stop button
		if(v instanceof Button){
		
			switch(mCurrentState){
				case RUNNING:
					onTimerStop();
					break;
				case STOPPED:
					showDialog(0);
					break;
				case PAUSED:
					enterState(State.STOPPED);		
					break;
			}
		}
		
		// User pressed the pause button
		else if(v instanceof ImageButton)
		{
			switch(mCurrentState){
				case RUNNING:
					pauseTimer();
					break;
				case PAUSED:
					resumeTimer();
					break;
			}
		}	
	}

	private void resumeTimer() 
	{
		onTimerStart(mTime,true);
		enterState(State.RUNNING);
	}
	
	private void pauseTimer()
	{
		mTimer.cancel();
		mTimer = null;
		
		Intent svc = new Intent(this, TimerService.class);
		stopService(svc);
		
		enterState(State.PAUSED);
	}
}
