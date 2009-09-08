package goo.TeaTimer.widget;

import goo.TeaTimer.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

class PauseButton extends View
{
	final int PADDING = 5,PAUSE_MID_DELTA=5,PAUSE_HEIGHT=9,PAUSE_STROKE=6;
	
	Paint mBgPaint,mHighlightPaint,mPausePaint;
	
	boolean mDown = false;
	
	/** Default Constructor */
	public PauseButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBgPaint.setColor(context.getResources().getColor(R.color.dark_gray));
		mBgPaint.setAlpha(150);
		
		mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mHighlightPaint.setColor(Color.RED);
		mHighlightPaint.setStyle(Paint.Style.STROKE);
		mHighlightPaint.setStrokeWidth(6);
	
		mPausePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPausePaint.setColor(context.getResources().getColor(android.R.color.background_dark));
		mPausePaint.setStyle(Paint.Style.STROKE);
		mPausePaint.setStrokeWidth(PAUSE_STROKE);
	}
	/** @inheritDoc */
	public void onDraw(Canvas canvas)
	{
		RectF rect = new RectF(PADDING,PADDING,getWidth()-PADDING,getHeight()-PADDING);
		float midX = getWidth()/2.0f;
		float midY = getHeight()/2.0f;
		
		if(mDown){
			canvas.drawOval(rect,mHighlightPaint);
		}
		
		canvas.drawOval(rect, mBgPaint);
		
		canvas.translate(midX,midY);
		canvas.drawLine(-PAUSE_MID_DELTA,-PAUSE_HEIGHT,-PAUSE_MID_DELTA,PAUSE_HEIGHT,mPausePaint );
		canvas.drawLine(PAUSE_MID_DELTA,-PAUSE_HEIGHT,PAUSE_MID_DELTA,PAUSE_HEIGHT,mPausePaint );
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		
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