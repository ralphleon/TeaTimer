package goo.TeaTimer.Animation;
import goo.TeaTimer.TimerUtils;

import goo.TeaTimer.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;

class CircleAnimation implements TimerAnimation.TimerDrawing
{
	Context mContext = null;
	
	private final int START_ANGLE = 270;
	
	private final int mRadius = 75,mInnerRadius=30,mSecondRadius=90,mMsRadius=mSecondRadius+5;
	private final int mTickerRadius = mSecondRadius + 10;
	
	private Paint mCirclePaint,mInnerPaint,mArcPaint,mMsPaint,mTickerPaint;
	
	/** Paint for the seconds arc */
	private Paint mSecondPaint, mSecondBgPaint;
	
	/** Rects for the arcs */
	private RectF mArcRect,mSecondRect,mMsRect;
	
	public CircleAnimation(Resources resources)
	{
		mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mCirclePaint.setColor(resources.getColor(R.color.theme1));
	
		mInnerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mInnerPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		
		// Paint for the seconds line 
		mSecondPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mSecondPaint.setColor(resources.getColor(R.color.theme4));
		
		mSecondBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mSecondBgPaint.setColor(resources.getColor(R.color.theme2));
		
		// Paint for the miliseconds
		mMsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mMsPaint.setColor(resources.getColor(R.color.theme5));
		
		int end = resources.getColor(R.color.theme3);
		
		int offset = 50;
		
		int r = Color.red(end) - offset;
		int g = Color.green(end) - offset;
		int b = Color.blue(end) - offset;
		
		int start = Color.rgb(r, g, b);
		
		/*RadialGradient gradient = new RadialGradient(	WIDTH/2.0f, HEIGHT/2.0f, mRadius, 
														start, 										
														end, 
														Shader.TileMode.CLAMP);
		*/
		mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mArcPaint.setStyle(Paint.Style.FILL);
		mArcPaint.setColor(end);
			
		mTickerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTickerPaint.setColor(0xFFFFFFFF);
		
		// Create the rects
		mSecondRect = new RectF();
		mArcRect = new RectF();
		mMsRect = new RectF();
	}
	
	/**
	 * Updates the image to be in sync with the current time
	 * @param time in milliseconds
	 * @param max the original time set in milliseconds
	 */
	public void updateImage(Canvas canvas, int time, int max) {
	
		float p = (max == 0) ? 0 : (time/(float)max);
		int [] timeVec = TimerUtils.time2Mhs(time);
		
		float pSecond = (max == 0) ? 1 : (timeVec[2] + timeVec[3]/1000.0f )/60.0f; 
		float pMs = (float)((timeVec[3]/1000.00));

		float thetaSecond = pSecond*360, thetaMs = pMs*360;

		float w2 =  canvas.getClipBounds().width()/2.0f;
		float h2 = canvas.getClipBounds().height()/2.0f;
		
		mSecondRect.set(w2-mSecondRadius, h2-mSecondRadius, w2+mSecondRadius, h2+mSecondRadius);
		mMsRect.set(w2-mMsRadius, h2-mMsRadius, w2+mMsRadius, h2+mMsRadius);		
		mArcRect.set(w2-mRadius, h2-mRadius, w2+mRadius, h2+mRadius);
		
		// Ms Arc
		//canvas.drawCircle(w2,h2,mSecondRadius+1, );
		canvas.drawArc(mMsRect, START_ANGLE, thetaMs, true, mMsPaint);
		
		//Second arc
		canvas.drawCircle(w2,h2,mSecondRadius,mSecondBgPaint);
		canvas.drawArc(mSecondRect, START_ANGLE, thetaSecond, true, mSecondPaint);
		
		// My line
		/*canvas.drawLine(	w2,h2,
							w2+(float)(mTickerRadius*Math.cos(thetaSecond)),
							h2+(float)(mTickerRadius*Math.sin(thetaSecond)),
							mTickerPaint);*/
		
		// Background fill
		canvas.drawCircle(w2,h2,mRadius,mCirclePaint);
				
		// Main arc
		canvas.drawArc(mArcRect,START_ANGLE,360*(1-p),true,mArcPaint);
		
		// Inner paint
		canvas.drawCircle(w2,h2,mInnerRadius,mInnerPaint);
	}
}