package goo.TeaTimer.Animation;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import goo.TeaTimer.R;

class BarAnimation implements TimerAnimation.TimerDrawing
{
	private final int WIDTH = 250, HEIGHT = 75; 
	
	private final int STROKE_WIDTH = 10;
	private final int ROUND = 5;
	private final int BUFFER = 10;
	
	private Paint mEdgePaint,mInnerPaint;
	
	private RectF mEdgeRect,mInnerRect;
	
	public BarAnimation(Resources resources)
	{
		mEdgePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mEdgePaint.setColor(resources.getColor(R.color.tea_fill));
		mEdgePaint.setStyle(Paint.Style.STROKE);
		mEdgePaint.setStrokeWidth(STROKE_WIDTH);
		//mEdgePaint.setStrokeCap(Paint.Cap.ROUND);
		//mEdgePaint.setStrokeJoin(Paint.Join.ROUND);
		
		mInnerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mInnerPaint.setColor(resources.getColor(R.color.tea_fill));
		
		mEdgeRect = new RectF(0,0,WIDTH,HEIGHT);
		mInnerRect = new RectF(BUFFER,BUFFER,WIDTH-BUFFER,HEIGHT-BUFFER);
	}
	
	/**
	 * Updates the image to be in sync with the current time
	 * @param time in milliseconds
	 * @param max the original time set in milliseconds
	 */
	public Bitmap updateImage(int time,int max)
	{	
		float p = (max == 0) ? 1 : (time/(float)max);
		Log.v("Tea","p " + p + " time " + time + " max " + max);
		Bitmap bitmap = Bitmap.createBitmap(WIDTH,HEIGHT,Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		
		// Draw the border
		canvas.drawRoundRect(mEdgeRect, ROUND, ROUND, mEdgePaint);
		
		// Draw the progress
		mInnerRect.set(	BUFFER+.5f*STROKE_WIDTH/2.0f, BUFFER+.5f*STROKE_WIDTH/2.0f, 
						(WIDTH-(BUFFER+.5f*STROKE_WIDTH/2.0f))*(1-p), 
						(HEIGHT-(BUFFER+.5f*STROKE_WIDTH/2.0f))
					  );
		//mInnerRect.set( BUFFER, BUFFER, WIDTH*p,HEIGHT*p);
		
		canvas.drawRoundRect(mInnerRect, ROUND, ROUND, mInnerPaint);
		
		return bitmap;
		
	}
}