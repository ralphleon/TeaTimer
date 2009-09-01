package com.goo.TeaTimer.Animation;

import java.util.Vector;

import com.goo.TeaTimer.R;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class TimerAnimation extends ImageView implements OnClickListener
{		
	Vector<TimerDrawing> mDrawings = null;
	int mIndex = 0;
	int mLastTime =0,mLastMax=0;
	
	Context mContext;
	
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
		mContext = context;
		
		mDrawings = new Vector<TimerDrawing>();
		mDrawings.add(new CircleAnimation(r));
		mDrawings.add(new TrashCupAnimation(r));
		//mDrawings.add(new BarAnimation(r));
		
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
		startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
		
		Log.v("Timer", "click");
		mIndex++;
		mIndex %= mDrawings.size();
		
		startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
		
		redraw();
	}
	
	private void redraw()
	{
		setImageBitmap(mDrawings.get(mIndex).updateImage(mLastTime,mLastMax));				
	}
	
}