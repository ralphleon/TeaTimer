package com.goo.TeaTimer.Animation;

import java.util.Vector;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class TimerAnimation extends ImageView implements OnClickListener
{		
	Vector<TimerDrawing> mDrawings = null;
	int mIndex = 0;
	int mLastTime =0,mLastMax=0;
	
	public interface TimerDrawing
	{
		/**
		 * Updates the image to be in sync with the current time
		 * @param time in milliseconds
		 * @param max the original time set in milliseconds
		 */
		public Bitmap updateImage(int time,int max);	
	}
	
	public TimerAnimation(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		Resources r = getResources();
		
		mDrawings = new Vector<TimerDrawing>();
		mDrawings.add(new CircleAnimation(r));
		mDrawings.add(new TrashCupAnimation(r));
		mDrawings.add(new BarAnimation(r));
		
		setOnClickListener(this);
	}

	public void setIndex(int i){ mIndex = i; redraw();}
	public int getIndex(){ return mIndex;}
	
	public void updateImage(int time,int max)
	{
		mLastTime = time;
		mLastMax = max;
		
		setImageBitmap(mDrawings.get(mIndex).updateImage(time,max));
	}

	public void onClick(View v) 
	{	
		Log.v("Timer", "click");
		mIndex++;
		mIndex %= mDrawings.size();
		
		redraw();
	}
	
	private void redraw()
	{
		setImageBitmap(mDrawings.get(mIndex).updateImage(mLastTime,mLastMax));				
	}
	
}