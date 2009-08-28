package goo.TeaTimer;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

class TimerAnimation extends ImageView
{		
	TimerDrawing mDrawing = null;
	
	public interface TimerDrawing
	{
		/**
		 * Updates the image to be in sync with the current time
		 * @param time in milliseconds
		 * @param max the original time set in milliseconds
		 */
		public Bitmap updateImage(int time,int max);	
	}
	
	public TimerAnimation(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mDrawing = new CircleAnimation();
	}

	public void updateImage(int time,int max)
	{
		setImageBitmap(mDrawing.updateImage(time,max));
	}
	

}