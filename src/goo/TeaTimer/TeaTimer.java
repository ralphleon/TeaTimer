package goo.TeaTimer;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class TeaTimer extends Activity {
		
	private enum State{ RUNNING, STOPPED };
	
	private State mCurrentState = State.STOPPED;
	
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
        		int set  = hourOfDay*60*60*1000 + minute*60*1000;
				onTimerStart(set);
			}
    };

	/** Handler for the message from the timer service */
	private Handler handler = new Handler() {
		
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
				// TODO update the image code here
				enterState(State.RUNNING);
				updateLabel(msg.arg1);
				updateImage(msg.arg1,msg.arg2);
			}
		}
    };
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	// TODO use this bundle
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		Button startButton = (Button)findViewById(R.id.stop);
        startButton.setOnClickListener(startListener);
        
        BotTimer.setHandler(handler);
        
        clearTime();
    }
    
	public void updateLabel(int time)
	{
		TextView label = (TextView)findViewById(R.id.label); 
		label.setText( BotTimer.time2str(time));
	}
	
	public void updateImage(int time,int max)
	{
		ImageView i = (ImageView)findViewById(R.id.imageView);	
		
		//final int w = i.getWidth();
		//final int h = i.getHeight();
		
		// Load the bitmap
		Bitmap cup  = BitmapFactory.decodeResource(getResources(), R.drawable.cup);
		int w = cup.getWidth();
		int h = cup.getHeight();
		
		Bitmap bitmap = Bitmap.createBitmap(w,h,Bitmap.Config.RGB_565);
		
		Paint paint = new Paint();
		
		float p = (max == 0) ? 1 : (time/(float)max);
		
		// Define the drawing rects
		RectF teaRect = new RectF(0,h*p,w,h);
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
                mTimeSetListener, 0, 0, true);
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
		Intent svc = new Intent(this, BotTimer.class);
		stopService(svc);
	}

	private void onTimerStart(int time)
	{
		enterState(State.RUNNING);
		Intent svc = new Intent(this, BotTimer.class);
	    svc.putExtra("Time",time);
		startService(svc);
	}

	private void clearTime()
	{
		updateLabel(0);
		updateImage(0, 0);
	}

}
