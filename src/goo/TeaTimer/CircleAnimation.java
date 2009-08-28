package goo.TeaTimer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

class CircleAnimation implements TimerAnimation.TimerDrawing
{
	Context mContext = null;
	// buffer 
	private final int WIDTH = 150;
	private final int HEIGHT = 150;
	private Paint mCirclePaint,mCircleFill;
	
	public CircleAnimation()
	{
	
		mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mCirclePaint.setStrokeWidth(3);
		mCirclePaint.setColor(Color.rgb(100,100,100));
		mCirclePaint.setStyle(Paint.Style.STROKE );

		mCircleFill = new Paint(Paint.ANTI_ALIAS_FLAG);
		mCircleFill.setColor(Color.rgb(100,200,100));
		mCircleFill.setStyle(Paint.Style.FILL);
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
		
		Canvas canvas = new Canvas(bitmap);
		canvas.drawCircle(WIDTH/2.0f,HEIGHT/2.0f,50,mCirclePaint);
		
		return bitmap;
		
	}
}