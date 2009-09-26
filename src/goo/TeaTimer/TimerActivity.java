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
		
	/** debug string */
	private final String DEBUG_STR = getClass().getSimpleName();
	
	/** Update rate of the internal timer */
	private final int TIMER_TIC = 500;
	
	/** The timer's current state */
	private int mCurrentState = STOPPED;
	
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
				
				if(mTimer != null){
					onTimerStop();
				
					// Show a finished toast since the view was in focus!
					Context context = getApplicationContext();
					CharSequence text = "Timer Finished!";
					Toast.makeText(context, text,Toast.LENGTH_SHORT).show();
				}
			}else{
				mTime = msg.arg1;
				
				enterState(RUNNING);
				onUpdateTime();
			}
		}
    };

	private ImageButton mPauseButton;

	private Bitmap mPlayBitmap,mPauseBitmap;
    
	/** Called when the activity is first created.
     *	{ @inheritDoc} 
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {    
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		ImageButton cancelButton = (ImageButton)findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(this);
        
		Button setButton = (Button)findViewById(R.id.setButton);
        setButton.setOnClickListener(this);
       
        mPauseButton = (ImageButton)findViewById(R.id.pauseButton);
        mPauseButton.setOnClickListener(this);
        
        mPauseBitmap = BitmapFactory.decodeResource(
        		getResources(), R.drawable.pause);
        
        mPlayBitmap = BitmapFactory.decodeResource(
        		getResources(), R.drawable.play);
   
        enterState(STOPPED);
        
        clearTime();
    }
    
    /** { @inheritDoc} */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	MenuItem item = menu.add(0, 0, 0, "Preferences");
    	item.setIcon(android.R.drawable.ic_menu_preferences);  
    	return super.onCreateOptionsMenu(menu);
    }
    
    /** { @inheritDoc} */
	@Override 
	public boolean onOptionsItemSelected(MenuItem item) 
	{  
		// Only one item
		startActivity(new Intent(this, TimerPrefActivity.class));	
		return true;
	}
    
	/** { @inheritDoc} */
    @Override 
    public void onPause()
    {
    	super.onPause();
    	
    	TimerAnimation i = (TimerAnimation)findViewById(R.id.imageView);
    
    	// Save our settings
    	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("LastTime", mLastTime);
        editor.putInt("CurrentTime",mTime);
        editor.putInt("DrawingIndex",i.getIndex());
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
    	TimerAnimation i = (TimerAnimation)findViewById(R.id.imageView);
    	
    	super.onResume();
    	
    	// check the timestamp from the last update and start the timer.
    	// assumes the data has already been loaded?
    	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    	   
        mLastTime = settings.getInt("LastTime",0);
        
        i.setIndex(settings.getInt("DrawingIndex",0));
        
        int state = settings.getInt("State",0);
        
        switch(state)
        {
        	case RUNNING:
        	{
        		long timeStamp = settings.getLong("TimeStamp", -1);
                
        		Date now = new Date();
        		Date then = new Date(timeStamp);
            	
            	// We stil have a timer running!
            	if(then.after(now)){
            		int delta = (int)(then.getTime() - now.getTime());		
            		onTimerStart(delta,false);
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
        		mTime = settings.getInt("CurrentTime",0);
        		onUpdateTime();
        		enterState(PAUSED);
        	}break;
        	
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
	
	/** {@inheritDoc} */
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
	private void enterState(int state)
	{
		ImageButton pause = (ImageButton)findViewById(R.id.pauseButton);
		ImageButton cancel = (ImageButton)findViewById(R.id.cancelButton);
		Button set = (Button)findViewById(R.id.setButton);
		
		switch(state)
		{
			case RUNNING:
			{
				set.setVisibility(View.GONE);
				cancel.setVisibility(View.VISIBLE);
				pause.setVisibility(View.VISIBLE);
				pause.setImageBitmap(mPauseBitmap);
			}break;
		
			case STOPPED:
			{	
				pause.setVisibility(View.GONE);
				cancel.setVisibility(View.GONE);
				set.setVisibility(View.VISIBLE);	
				clearTime();
				
			}break;
		
			case PAUSED:
			{
				set.setVisibility(View.GONE);
				pause.setVisibility(View.VISIBLE);
				cancel.setVisibility(View.VISIBLE);
				pause.setImageBitmap(mPlayBitmap);
			}break;	
		}
			
		mCurrentState = state;
	}
	
	/**
	 * Stops the timer
	 */
	private void onTimerStop()
	{
		// Stop our timer service
		enterState(STOPPED);		
		Intent svc = new Intent(this, TimerService.class);
		stopService(svc);
		
		// Stop our local timer
		mTimer.cancel();
	}
	
	/**
	 * Starts the timer at the given time
	 * @param time with which to count down
	 * @param service whether or not to start the service as well
	 */
	private void onTimerStart(int time,boolean service)
	{
		Log.v(DEBUG_STR,"Starting the timer...");
		
		// Star external service
		enterState(RUNNING);
		
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
	
	/** Resume the time after being paused */
	private void resumeTimer() 
	{
		onTimerStart(mTime,true);
		enterState(RUNNING);
	}
	
	/** Pause the timer and stop the timer service */
	private void pauseTimer()
	{
		mTimer.cancel();
		mTimer = null;
		
		Intent svc = new Intent(this, TimerService.class);
		stopService(svc);
		
		enterState(PAUSED);
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
				showDialog(0);		
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
				onTimerStop();	
			}break;
		}
	}
			
}
