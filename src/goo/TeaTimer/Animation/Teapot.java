package goo.TeaTimer.Animation;

import goo.TeaTimer.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.PorterDuff.Mode;

class Teapot implements TimerAnimation.TimerDrawing
{	
	private Bitmap mCupBitmap;

	private int mWidth = 0;
	private int mHeight = 0;
	
	private Paint mProgressPaint = null;

	private Bitmap mBitmap = null;

	public Teapot(Context context)
	{
		Resources resources = context.getResources();
		
		mProgressPaint = new Paint();
		mProgressPaint.setColor(Color.GRAY);
		mProgressPaint.setAlpha(135);
		mProgressPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		
		mCupBitmap = BitmapFactory.decodeResource(resources, R.drawable.teapot);	
		mHeight = mCupBitmap.getHeight();
		mWidth = mCupBitmap.getWidth();

		mBitmap = Bitmap.createBitmap(mWidth,mHeight,Bitmap.Config.ARGB_8888);
	}

	/**
	 * Updates the image to be in sync with the current time
	 * @param time in milliseconds
	 * @param max the original time set in milliseconds
	 */
	public void updateImage(Canvas canvas, int time, int max) {
	
		canvas.save();
		float w = canvas.getClipBounds().width();
		float h = canvas.getClipBounds().height();
		
		canvas.translate(w/2.0f - mWidth/2.0f,
						 h/2.0f - mHeight/2.0f);
		
		canvas.drawBitmap(mCupBitmap, 0, 0,null);
		
		float p = (max != 0) ? (time/(float)max) : 0;
		
		if(p == 0) p = 1;
		
		RectF fill = new RectF(0,mHeight*(p),mWidth,mHeight);
		canvas.drawRect(fill,mProgressPaint);	
		
		canvas.restore();
	}

	public void configure() {
		// TODO Auto-generated method stub
		
	}
}