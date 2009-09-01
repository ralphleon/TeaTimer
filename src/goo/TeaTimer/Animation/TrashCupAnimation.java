package goo.TeaTimer.Animation;

import goo.TeaTimer.Animation.TimerAnimation.TimerDrawing;

import goo.TeaTimer.R;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

class TrashCupAnimation implements TimerAnimation.TimerDrawing
{
	// buffer 
	private final int TOP_BUFFER = 3;
	private final int BOTTOM_BUFFER = 5;

	private Bitmap mCupBitmap;

	private int mWidth;

	private int mHeight;
	
	private Paint mProgressPaint,mBgPaint;
	
	private RectF mTeaRect;
	
	public TrashCupAnimation(Resources resources)
	{
		// Load the bitmap
		mCupBitmap  = BitmapFactory.decodeResource(resources, R.drawable.cup);
		mWidth  = mCupBitmap.getWidth();
		mHeight = mCupBitmap.getHeight();
		
		mProgressPaint = new Paint();
		mProgressPaint.setColor(resources.getColor(R.color.tea_fill));
		
		mBgPaint = new Paint();
		mBgPaint.setColor(resources.getColor(R.color.dark_gray));
		
		mTeaRect = new RectF(0,0,0,0);
		
	}
	
	/**
	 * Updates the image to be in sync with the current time
	 * @param time in milliseconds
	 * @param max the original time set in milliseconds
	 */
	public Bitmap updateImage(int time,int max)
	{	
		Bitmap bitmap = Bitmap.createBitmap(mWidth,mHeight,Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		
		float p = (max == 0) ? 0 : (time/(float)max);
		
		// Define the drawing rects
		mTeaRect.set(0,(mHeight-TOP_BUFFER)*p+BOTTOM_BUFFER,mWidth,mHeight+BOTTOM_BUFFER);
		
		// Unused part of the cup
		canvas.drawPaint(mBgPaint);
		
		// The filled part of the cup
		canvas.drawRect(mTeaRect,mProgressPaint);
		canvas.drawBitmap(mCupBitmap, 0, 0, null);
		
		// Switch out the bitmap
		return bitmap;	
	}
}