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

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * The main activity which shows the timer and allows the user to set the time
 * @author Ralph Gootee (rgootee@gmail.com)
 */
public class TimerActivity extends Activity {
		
	/** debug string */
	private final String DEBUG_STR = "TimerActivity";
	
	private enum State{ RUNNING, STOPPED };
	
	private State mCurrentState = State.STOPPED;
	
	/** Last time update from handler */
	private int mLastTime = 0;
	
	/** The maximum time */
	private int mMax = 0;
	
	   private ServiceConnection mConnection = new ServiceConnection() {
	        public void onServiceConnected(ComponentName className, IBinder service) {
	        	Log.v(DEBUG_STR,"Service Connected!");
	     
	        	((TimerService.TimerBinder)service).setHandler(mHandler);
	        }

	        public void onServiceDisconnected(ComponentName className) {
	        	Log.e(DEBUG_STR,"Error! Service Disconnected!");
	        }
	    };
	
	/** Listener for the button press */
	private OnClickListener startListener = new OnClickListener()
    {
        public void onClick(View v)
        {
			Button b = (Button)v;
			
			//TODO start using the string xml file
			if(b.getText() == getText(R.string.Stop)){	
				onTimerStop();	
			}else{
				showDialog(0);			
		
			}
        }
	};
	
	/** the call-back received when the user "sets" the time in the dialog */
	private TimePickerDialog.OnTimeSetListener mTimeSetListener =
    	new TimePickerDialog.OnTimeSetListener() {
        	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        		mMax = hourOfDay*60*60*1000 + minute*60*1000;
        		
				onTimerStart(mMax);
			}
    };

	/** Handler for the message from the timer service */
	private Handler mHandler = new Handler() {
		
		@Override
        public void handleMessage(Message msg) {
			
			if(msg.arg1 == 0){
				onTimerStop();
				
				// Show a finished toast since the view was in focus!
				Context context = getApplicationContext();
				CharSequence text = "Timer Finished!";
				Toast.makeText(context, text,Toast.LENGTH_SHORT).show();
			}
			else{
				mLastTime = msg.arg1;
				
				enterState(State.RUNNING);
				onUpdateTime();
			}
		}
    };
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		Button startButton = (Button)findViewById(R.id.stop);
        startButton.setOnClickListener(startListener);
        
        clearTime();
    }
    
    /** {@inheritDoc} */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
    	Log.v(DEBUG_STR,"Saved the application state. ");
    
    	savedInstanceState.putString("MyString", "Welcome back to Android");
    	
    	savedInstanceState.putInt("LastTime", mLastTime);
    	savedInstanceState.putInt("Max",mMax);
    	
    	super.onSaveInstanceState(savedInstanceState);
    }

    /** {@inheritDoc} */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) 
    {
    	super.onRestoreInstanceState(savedInstanceState);
    	
    	mLastTime = savedInstanceState.getInt("LastTime");
    	mMax = savedInstanceState.getInt("Max");
    	
    	Log.v(DEBUG_STR,"Restored the application state, t=" + mLastTime);
    	
    	onUpdateTime();
    }
    
    /**
     * Updates the time 
     */
    public void onUpdateTime()
    {
    	updateLabel(mLastTime);
		updateImage(mLastTime,mMax);  	
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
	/**
	 * Updates the image to be in sync with the current time
	 * @param time in milliseconds
	 * @param max the original time set in milliseconds
	 */
	public void updateImage(int time,int max)
	{
		// buffer 
		int topBuffer = 13;
		int bottomBuffer = 15;
		
		ImageView i = (ImageView)findViewById(R.id.imageView);	
		
		// Load the bitmap
		Bitmap cup  = BitmapFactory.decodeResource(getResources(), R.drawable.cup);
		int w = cup.getWidth();
		int h = cup.getHeight();
		
		Bitmap bitmap = Bitmap.createBitmap(w,h,Bitmap.Config.RGB_565);
		
		Paint paint = new Paint();
		
		float p = (max == 0) ? 0 : (time/(float)max);
		
		// Define the drawing rects
		RectF teaRect = new RectF(0,(h-topBuffer)*p+bottomBuffer,w,h+bottomBuffer);
		RectF fillRect = new RectF(0,0,w,h);
		
		Canvas canvas = new Canvas(bitmap);
		
		// Fill the entire bg the correct color
		canvas.drawColor(Color.rgb(24,24,24));
		
		// Unused part of the cup
		paint.setColor(R.color.tea_bg);
		canvas.drawRect(fillRect, paint);
		
		// The filled part of the cup
		paint.setColor(getResources().getColor(R.color.tea_fill));
		canvas.drawRect(teaRect,paint);
		canvas.drawBitmap(cup, 0, 0, paint);
		
		// Switch out the bitmap
		i.setImageBitmap(bitmap);
	
		
	}
	
	/** {@inheritDoc} */
	@Override
	protected Dialog onCreateDialog(int id) {
    	switch (id) {
    	case 0:
        	return new AbsTimePickerDialog(this,
                mTimeSetListener, 0, 0);
    	}
    	return null;
	}

	/** 
	 * This only refers to the visual state of the application, used to manage
	 * the view coming back into focus.
	 * 
	 * @param state the visual state that is being entered
	 */
	private void enterState(State state)
	{
		if(mCurrentState != state){
		
			if(state == State.RUNNING){
				Button b = (Button)findViewById(R.id.stop);
				b.setText(R.string.Stop);	
			}
			else if(state == State.STOPPED){
				Button b = (Button)findViewById(R.id.stop);
				b.setText(R.string.Start);
				clearTime();
			}
			mCurrentState = state;
		}
	}
	
	private void onTimerStop()
	{
		enterState(State.STOPPED);		
		Intent svc = new Intent(this, TimerService.class);
		unbindService(mConnection);
		stopService(svc);
	}

	private void onTimerStart(int time)
	{
		Log.v(DEBUG_STR,"Starting the timer...");
		
		enterState(State.RUNNING);
		Intent svc = new Intent(this, TimerService.class);
	    svc.putExtra("Time",time);
		startService(svc);
		
		svc.removeExtra("Time");
		bindService(svc, mConnection,Context.BIND_AUTO_CREATE);
	}

	private void clearTime()
	{
		updateLabel(0);
		updateImage(0, 0);
	}

}
