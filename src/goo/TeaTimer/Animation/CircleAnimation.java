package goo.TeaTimer.Animation;
import goo.TeaTimer.R;
import goo.TeaTimer.TimerUtils;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.preference.PreferenceManager;

class CircleAnimation implements TimerAnimation.TimerDrawing
{
	private final int START_ANGLE = 270;
	
	private int mRadius = 75,mInnerRadius=30,mSecondRadius=90,mMsRadius=mSecondRadius+5;
	private final int mTickerRadius = mSecondRadius + 10;
	
	private Paint mCirclePaint,mInnerPaint,mArcPaint,mMsPaint,mTickerPaint;
	
	/** Paint for the seconds arc */
	private Paint mSecondPaint, mSecondBgPaint;
	
	private boolean showMs = false;
	boolean mMsFlipper = false;
	private int [] mLastTime;
	
	/** Rects for the arcs */
	private RectF mArcRect,mSecondRect,mMsRect;

	private Context mContext;
	
	public CircleAnimation(Context context)
	{
		mContext = context;
	
		// Create the rects
		mSecondRect = new RectF();
		mArcRect = new RectF();
		mMsRect = new RectF();
		
		configure();
	}
	
	public void configure()
	{	
		Resources resources = mContext.getResources();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		
		// LOAD The correct theme
		int theme = Integer.parseInt(prefs.getString("Theme", "0"));
		
		int [] colors;
		
		switch(theme){
			case 0:
				colors = new int[] { resources.getColor(R.color.themeA1),
						 resources.getColor(R.color.themeA2),
						 resources.getColor(R.color.themeA3),
						 resources.getColor(R.color.themeA4),
						 resources.getColor(R.color.themeA5)
				};
			break;
			
			case 1:
				colors = new int[] { resources.getColor(R.color.themeB1),
						 resources.getColor(R.color.themeB2),
						 resources.getColor(R.color.themeB3),
						 resources.getColor(R.color.themeB4),
						 resources.getColor(R.color.themeB5)
				};
				break;
				
			case 2:
			default:
				colors = new int[] { resources.getColor(R.color.themeC1),
						 resources.getColor(R.color.themeC2),
						 resources.getColor(R.color.themeC3),
						 resources.getColor(R.color.themeC4),
						 resources.getColor(R.color.themeC5)
				};
				break;
				
		}
		
		mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mCirclePaint.setColor(colors[0]);
	
		mInnerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mInnerPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		
		// Paint for the seconds line 
		mSecondPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mSecondPaint.setColor(colors[3]);
		
		mSecondBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mSecondBgPaint.setColor(colors[1]);
		
		// Paint for the miliseconds
		mMsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mMsPaint.setColor(colors[4]);
		
		int end = colors[2];
		
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
	}
	
	
	/**
	 * Updates the image to be in sync with the current time
	 * @param time in milliseconds
	 * @param max the original time set in milliseconds
	 */
	public void updateImage(Canvas canvas, int time, int max) {
	
		float p = (max == 0) ? 0 : (time/(float)max);
		int [] timeVec = TimerUtils.time2Mhs(time);
		if(mLastTime == null) mLastTime = timeVec;
		if(mLastTime[2] != timeVec[2]) mMsFlipper = !mMsFlipper;
		
		float pSecond = (max == 0) ? 1 : (timeVec[2] + timeVec[3]/1000.0f )/60.0f; 		
		float thetaSecond = pSecond*360;
		
		float w2 =  canvas.getClipBounds().width()/2.0f;
		float h2 = canvas.getClipBounds().height()/2.0f;
		
		mSecondRect.set(w2-mSecondRadius, h2-mSecondRadius, w2+mSecondRadius, h2+mSecondRadius);
		mArcRect.set(w2-mRadius, h2-mRadius, w2+mRadius, h2+mRadius);
		
		// Ms Arc
		if(showMs){
			float pMs = (float)((timeVec[3]/1000.00));
			float thetaMs = pMs*360;

			mMsRect.set(w2-mMsRadius, h2-mMsRadius, w2+mMsRadius, h2+mMsRadius);		
			canvas.drawCircle(w2,h2,mMsRadius, (mMsFlipper) ? mCirclePaint : mMsPaint );
			canvas.drawArc(mMsRect, START_ANGLE, thetaMs, true, (mMsFlipper) ? mMsPaint: mCirclePaint);
		}
		
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
		
		mLastTime = timeVec;
	}
}