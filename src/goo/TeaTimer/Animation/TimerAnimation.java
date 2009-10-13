package goo.TeaTimer.Animation;

import goo.TeaTimer.R;

import java.util.Vector;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class TimerAnimation extends View implements OnClickListener
{		
	Vector<TimerDrawing> mDrawings = null;
	int mIndex = 0;
	int mLastTime =0,mLastMax=0;
	
	Bitmap mBitmap = null;
	
	Context mContext;
	
	public interface TimerDrawing
	{
		/**
		 * Updates the image to be in sync with the current time
		 * @param time in milliseconds
		 * @param max the original time set in milliseconds
		 */
		public Bitmap updateImage(int time,int max);
		//public void draw(Canvas canvas);
	}
	
	public TimerAnimation(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		Resources r = getResources();
		mContext = context;
		
		mDrawings = new Vector<TimerDrawing>();
		mDrawings.add(new CircleAnimation(r));
		mDrawings.add(new TrashCupAnimation(r));
		mDrawings.add(new Teapot(r));
		
		setOnClickListener(this);
	}

	public void setIndex(int i)
	{
		if(i >= mDrawings.size()) i = 0;
		mIndex = i;
		invalidate();
	}
	
	public int getIndex(){ return mIndex;}
	
	public void updateImage(int time,int max)
	{
		mLastTime = time;
		mLastMax = max;
		
		invalidate();
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		// TODO eventually moving this to a view framework
		
		Bitmap mBitmap = mDrawings.get(mIndex).updateImage(mLastTime,mLastMax);
		
		canvas.drawBitmap(	mBitmap,getWidth()/2 - mBitmap.getWidth()/2,
							getHeight()/2 - mBitmap.getHeight()/2,null);
	}
	
	public void onClick(View v) 
	{	
		startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
		
		mIndex++;
		mIndex %= mDrawings.size();
		
		startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
	
		invalidate();
	}
	
	
}