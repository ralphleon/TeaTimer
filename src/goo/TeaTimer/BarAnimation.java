package goo.TeaTimer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Log;

class BarAnimation implements TimerAnimation.TimerDrawing
{
	Context mContext = null;
	// buffer 
	private final int WIDTH = 180;
	private final int HEIGHT = 180;
	private final int STROKE = 0;
	
	private final int START_ANGLE = 270;
	
	private int mRadius = 75,mInnerRadius=30,mSecondRadius=90,mMsRadius=100;

	private Paint mCirclePaint,mInnerPaint,mArcPaint,mSecondPaint,mMsPaint;

	/** Rects for the arcs */
	private RectF mArcRect,mSecondRect,mMsRect;
	
	public BarAnimation(Resources resources)
	{
		mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mCirclePaint.setColor(Color.rgb(0,0,0));
	
		mInnerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mInnerPaint.setColor(Color.rgb(24,24,24));
		
		mSecondPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mSecondPaint.setColor(Color.rgb(60,60,60));
		
		mMsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mMsPaint.setColor(Color.rgb(90,90,90));
		
		int end = resources.getColor(R.color.tea_fill);
		
		int offset = 50;
		
		int r = Color.red(end) - offset;
		int g = Color.green(end) - offset;
		int b = Color.blue(end) - offset;
		
		int start = Color.rgb(r, g, b);
		
		RadialGradient gradient = new RadialGradient(	WIDTH/2.0f, HEIGHT/2.0f, mRadius, 
														start, 										
														end, 
														Shader.TileMode.CLAMP);
		
		mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mArcPaint.setStyle(Paint.Style.FILL);
		mArcPaint.setShader(gradient);
		
		float w2 = WIDTH/2.0f;
		float h2 = HEIGHT/2.0f;
		
		// Create the rects
		mMsRect = new RectF(w2-mMsRadius, h2-mMsRadius, w2+mMsRadius, h2+mMsRadius);	
		mSecondRect = new RectF(w2-mSecondRadius, h2-mSecondRadius, w2+mSecondRadius, h2+mSecondRadius);
		mArcRect = new RectF(w2-mRadius, h2-mRadius, w2+mRadius, h2+mRadius);
	}
	
	/**
	 * Updates the image to be in sync with the current time
	 * @param time in milliseconds
	 * @param max the original time set in milliseconds
	 */
	public Bitmap updateImage(int time,int max)
	{	
		Bitmap bitmap = Bitmap.createBitmap(WIDTH,HEIGHT,Bitmap.Config.ARGB_8888);
		
		float p = (max == 0) ? 0 : (time/(float)max);
		
		int [] timeVec = TimerService.time2Mhs(time);
		
		float pSecond = (max == 0) ? 1 : (float)((timeVec[2]/60.0)); 
		float pMs = (float)((timeVec[3]/1000.00));
		
		float w2 = WIDTH/2.0f;
		float h2 = HEIGHT/2.0f;
		
		Canvas canvas = new Canvas(bitmap);
		
		// Ms Arc
		//canvas.drawArc(mMsRect, pMs*360, SECOND_SWEEP, true, mMsPaint);
	
		//Second arc
		canvas.drawCircle(w2,h2,mSecondRadius,mMsPaint);
		canvas.drawArc(mSecondRect, START_ANGLE, pSecond*360, true, mSecondPaint);
		
		// Background fill
		canvas.drawCircle(w2,h2,mRadius,mCirclePaint);
				
		// Main arc
		canvas.drawArc(mArcRect,START_ANGLE,360*(1-p),true,mArcPaint);
		
		// Inner paint
		canvas.drawCircle(w2,h2,mInnerRadius,mInnerPaint);
		
		return bitmap;
		
	}
}