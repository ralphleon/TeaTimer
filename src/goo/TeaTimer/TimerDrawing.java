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

class TimerDrawing extends ImageView
{
	Context mContext = null;
	
	public TimerDrawing(Context context, AttributeSet set)
	{
		super(context,set);
	
		mContext = context;
	}
	
	/**
	 * Updates the image to be in sync with the current time
	 * @param time in milliseconds
	 * @param max the original time set in milliseconds
	 */
	public void updateImage(int time,int max)
	{
		// buffer 
		int topBuffer = 13;
		int bottomBuffer = 15;
		
		ImageView i = this;
		
		// Load the bitmap
		Bitmap cup  = BitmapFactory.decodeResource(getResources(), R.drawable.cup);
		int w = cup.getWidth();
		int h = cup.getHeight();
		
		Bitmap bitmap = Bitmap.createBitmap(w,h,Bitmap.Config.RGB_565);
		
		Paint paint = new Paint();
		
		float p = (max == 0) ? 0 : (time/(float)max);
		
		// Define the drawing rects
		RectF teaRect = new RectF(0,(h-topBuffer)*p+bottomBuffer,w,h+bottomBuffer);
		RectF fillRect = new RectF(0,0,w,h);
		
		Canvas canvas = new Canvas(bitmap);
		
		// Fill the entire bg the correct color
		canvas.drawColor(Color.rgb(24,24,24));
		
		// Unused part of the cup
		paint.setColor(R.color.tea_bg);
		canvas.drawRect(fillRect, paint);
		
		// The filled part of the cup
		paint.setColor(getResources().getColor(R.color.tea_fill));
		canvas.drawRect(teaRect,paint);
		canvas.drawBitmap(cup, 0, 0, paint);
		
		// Switch out the bitmap
		i.setImageBitmap(bitmap);	
	}
}