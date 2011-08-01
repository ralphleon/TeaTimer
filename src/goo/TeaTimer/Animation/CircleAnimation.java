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
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.preference.PreferenceManager;

class CircleAnimation implements TimerAnimation.TimerDrawing
{
	private static final float MAX_SIZE = 100;

	private final int START_ANGLE = 270;
	
	private float mRadius = 75,mInnerRadius=30,mSecondRadius=90,mMsRadius=mSecondRadius+5;
	private float scale;
	
	private Paint mCirclePaint,mInnerPaint,mArcPaint,mMsPaint,mTickerPaint;
	
	RadialGradient mInnerGradient;
	
	/** Paint for the seconds arc */
	private Paint mSecondPaint, mSecondBgPaint;
	
	int mInnerColor = 0;
	
	private boolean showMs = false;
	boolean mMsFlipper = false;
	private int [] mLastTime;
	
	/** Rects for the arcs */
	private RectF mArcRect,mSecondRect,mMsRect;

	private Context mContext;

	private int mWidth;

	private int mHeight;

	private float mSecondGap;

	private float mMsGap;
	
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
		
		mInnerColor = colors[2];
	
		mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mArcPaint.setStyle(Paint.Style.FILL);
		
		mTickerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTickerPaint.setColor(0xFFFFFFFF);
		
		scale = resources.getDisplayMetrics().density;
		
		if(mWidth != 0 && mHeight != 0) sizeChange(mWidth,mHeight);
	}
	
	public void sizeChange(int w, int h){
			
		mWidth = w;
		mHeight = h;
		
		mMsRadius = Math.min(Math.min(w/2.0f,h/2.0f),MAX_SIZE*scale);
		mMsGap = mMsRadius * .95f;
		mSecondRadius = mMsRadius * .97f;
		mSecondGap = mMsRadius *.85f;
		mRadius = mMsRadius * .85f;
		mInnerRadius=30*scale;
		
		int offset = 75;
		
		int r = Color.red(mInnerColor) - offset;
		int g = Color.green(mInnerColor) - offset;
		int b = Color.blue(mInnerColor) - offset;
		
		int start = Color.rgb(r, g, b);
		
		Shader shader = new RadialGradient(0, 0, 	mRadius, 
													start, 										
													mInnerColor, 
													Shader.TileMode.CLAMP);
		mArcPaint.setShader(shader);
	}
	
	/**
	 * Updates the image to be in sync with the current time
	 * @param time in milliseconds
	 * @param max the original time set in milliseconds
	 */
	public void updateImage(Canvas canvas, int time, int max) {
	
		canvas.save();
		
		float p = (max == 0) ? 0 : (time/(float)max);
		int [] timeVec = TimerUtils.time2Mhs(time);
		if(mLastTime == null) mLastTime = timeVec;
		if(mLastTime[2] != timeVec[2]) mMsFlipper = !mMsFlipper;
		
		float pSecond = (max == 0) ? 1 : (timeVec[2] + timeVec[3]/1000.0f )/60.0f; 		
		float thetaSecond = pSecond*360;
		
		
		if(mWidth != canvas.getClipBounds().width() || mHeight != canvas.getClipBounds().height())
			sizeChange(canvas.getClipBounds().width(),canvas.getClipBounds().height());
	
		canvas.translate(mWidth/2.0f, mHeight/2.0f);
		
		mSecondRect.set(-mSecondRadius, -mSecondRadius, mSecondRadius, mSecondRadius);
		mArcRect.set(-mRadius, -mRadius, mRadius, mRadius);
			
		// Ms Arc
		if(showMs){
			float pMs = (float)((timeVec[3]/1000.00));
			float thetaMs = pMs*360;

			mMsRect.set(-mMsRadius, -mMsRadius, mMsRadius, mMsRadius);		
			canvas.drawCircle(0,0,mMsRadius, (mMsFlipper) ? mCirclePaint : mMsPaint );
			canvas.drawArc(mMsRect, START_ANGLE, thetaMs, true, (mMsFlipper) ? mMsPaint: mCirclePaint);
		}
		// We want to draw a very thin border
		else{
			canvas.drawCircle(0,0,mMsRadius, mMsPaint );
		}
	
		// Gap between the ms and seconds
		canvas.drawCircle(0,0,mMsGap,mInnerPaint);
				
		//Second arc
		canvas.drawCircle(0,0,mSecondRadius,mSecondBgPaint);
		canvas.drawArc(mSecondRect, START_ANGLE, thetaSecond, true, mSecondPaint);
		
		// Gap between the seconds and the inner radius
		canvas.drawCircle(0,0,mSecondGap,mInnerPaint);
		
		// Background fill
		canvas.drawCircle(0,0,mRadius,mCirclePaint);
		
		// Main arc
		canvas.drawArc(mArcRect,START_ANGLE,360*(1-p),true,mArcPaint);
		
		// Inner paint
		canvas.drawCircle(0,0,mInnerRadius,mInnerPaint);
		
		mLastTime = timeVec;
		
		canvas.restore();
	}
}