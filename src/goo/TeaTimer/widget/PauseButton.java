package goo.TeaTimer.widget;

import goo.TeaTimer.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Pause button, supporting a pause state and play state. More of a Pause-Play button
 * 
 * @author Ralph Gootee (rgootee@gmail.com)
 *
 */
public class PauseButton extends View
{
	final int PADDING = 5,PAUSE_MID_DELTA=5,PAUSE_HEIGHT=9,PAUSE_STROKE=6,ICON_SIZE=28;
	
	Paint mBgPaint,mHighlightPaint,mPausePaint;
	
	Bitmap mPauseBitmap, mPlayBitmap;
	
	boolean mDown = false;

	private boolean mPlay = false;
	
	/** Default Constructor */
	public PauseButton(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		
		mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBgPaint.setColor(context.getResources().getColor(R.color.dark_gray));
		
		mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mHighlightPaint.setColor(context.getResources().getColor(R.color.light_gray));
	
		mPausePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPausePaint.setColor(context.getResources().getColor(android.R.color.background_dark));
		mPausePaint.setStyle(Paint.Style.STROKE);
		mPausePaint.setStrokeWidth(PAUSE_STROKE);
		
		mPauseBitmap = BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_media_pause);
		mPauseBitmap = Bitmap.createScaledBitmap(mPauseBitmap, ICON_SIZE, ICON_SIZE, true);
		
		mPlayBitmap = BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_media_play);
		mPlayBitmap = Bitmap.createScaledBitmap(mPlayBitmap, ICON_SIZE, ICON_SIZE, true);
		
		setPlay(false);
		
		setClickable(true);
	}
	
	/**
	 * Represents the visual mode of the pause button
	 * @param set If set to true the pause button will show a "play" icon.
	 */
	public void setPlay(boolean set)
	{ 
		mPlay = set;
		
		Log.v("play","set " + set);
		invalidate();
	}
	
	/** @inheritDoc */
	public void onDraw(Canvas canvas)
	{
		RectF rect = new RectF(PADDING,PADDING,getWidth()-PADDING,getHeight()-PADDING);
		float midX = getWidth()/2.0f;
		float midY = getHeight()/2.0f;
		
		if(mDown){
			canvas.drawOval(rect,mHighlightPaint);
		}else{
			canvas.drawOval(rect, mBgPaint);		
		}

		if(!mPlay)
		{
			canvas.drawBitmap(mPauseBitmap,midX-mPauseBitmap.getWidth()/2.0f,midY-mPauseBitmap.getHeight()/2.0f,null);
		}else{
			canvas.drawBitmap(mPlayBitmap,midX-mPlayBitmap.getWidth()/2.0f,midY-mPlayBitmap.getHeight()/2.0f,null);			
		}
		
			//canvas.translate(midX,midY);
		//canvas.drawLine(-PAUSE_MID_DELTA,-PAUSE_HEIGHT,-PAUSE_MID_DELTA,PAUSE_HEIGHT,mPausePaint );
		//canvas.drawLine(PAUSE_MID_DELTA,-PAUSE_HEIGHT,PAUSE_MID_DELTA,PAUSE_HEIGHT,mPausePaint );
	}
	
	/** @inheritDoc */
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		super.onTouchEvent(event);
		
		switch(event.getAction()){
		
			case MotionEvent.ACTION_DOWN:
			{
				mDown = true;
			}
			break;
			
			case MotionEvent.ACTION_UP:
			{
				mDown = false;	
			}
			break;
			
			default:
			{
				return false;
			}
		}
		
		invalidate();
		return true;
	}
	
}