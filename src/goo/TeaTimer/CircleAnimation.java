package goo.TeaTimer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

class CircleAnimation implements TimerAnimation.TimerDrawing
{
	Context mContext = null;
	// buffer 
	private final int WIDTH = 180;
	private final int HEIGHT = 180;
	private final int STROKE = 0;
	private int mRadius = 85,mInnerRadius=30;

	private Paint mCirclePaint,mInnerPaint,mArcPaint;
	private Resources mResources;
	
	public CircleAnimation(Resources r)
	{
		mResources = r;
		
		mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mCirclePaint.setColor(Color.rgb(0,0,0));
	
		mInnerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mInnerPaint.setColor(Color.rgb(24,24,24));
		
		mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mArcPaint.setColor(r.getColor(R.color.tea_fill));
		mArcPaint.setStyle(Paint.Style.FILL);
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
		
		float w2 = WIDTH/2.0f;
		float h2 = HEIGHT/2.0f;
		
		Canvas canvas = new Canvas(bitmap);
		
		canvas.drawCircle(w2,h2,mRadius,mCirclePaint);
		
		RectF rect = new RectF(w2-mRadius, h2-mRadius, w2+mRadius, h2+mRadius);
		canvas.drawArc(rect,270,360*(1-p),true,mArcPaint);
		
		canvas.drawCircle(w2,h2,mInnerRadius,mInnerPaint);
		
		return bitmap;
		
	}
}