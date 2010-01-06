package goo.TeaTimer.Animation;

import goo.TeaTimer.R;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Sample timer animation.
 * The trash cup naming comes from the fact that the cup reminds my friends
 * of a "trash can"
 * 
 * @author Ralph Gootee (rgootee@gmail.com)
 *
 */
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
	public void updateImage(Canvas canvas, int time, int max) {
	
		float p = (max == 0) ? 0 : (time/(float)max);
		
		canvas.save();
		
		float w = canvas.getClipBounds().width();
		float h = canvas.getClipBounds().height();
				
		canvas.translate(w/2.0f - mWidth/2.0f,
						 h/2.0f - mHeight/2.0f);

		// Define the drawing rects
		mTeaRect.set(0,(mHeight)*p,mWidth,mHeight);
		
		// Unused part of the cup
		canvas.drawRect(mTeaRect,mBgPaint);
		
		// The filled part of the cup
		canvas.drawRect(mTeaRect,mProgressPaint);
		canvas.drawBitmap(mCupBitmap, 0, 0, null);
		
		canvas.restore();
	}
}