package goo.TeaTimer;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

public class TimerReceiver extends BroadcastReceiver 
{
	private final static String TAG = TimerReceiver.class.getSimpleName();
    private final static String CANCEL_NOTIFICATION = "CANCEL_NOTIFICATION";
	
	@Override
	public void onReceive(Context context, Intent pintent) 
    {
        NotificationManager mNM = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Cancel notification and return...
        if (CANCEL_NOTIFICATION.equals(pintent.getAction())) {
            Log.v(TAG,"Cancelling notification...");
            mNM.cancel(0);
            return;
        }

        // ...or display a new one

		Log.v(TAG,"Showing notification...");
		
		int setTime = pintent.getIntExtra("SetTime",0);
		String setTimeStr = TimerUtils.time2humanStr(setTime);
		
		// Load the settings
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        boolean led = settings.getBoolean("LED",true);
        boolean vibrate = settings.getBoolean("Vibrate",true);
        String notificationUri = settings.getString("NotificationUri", "android.resource://goo.TeaTimer/" + R.raw.big_ben);
        	
		CharSequence text = context.getText(R.string.Notification);
		CharSequence textLatest = context.getText(R.string.timer_for) + setTimeStr;
		
        Notification notification = new Notification(R.drawable.notification,
        		text,
                System.currentTimeMillis());

        // Vibrate
        if(vibrate){
        	notification.defaults = Notification.DEFAULT_VIBRATE;    	
        }
        
        // Have a light
        if(led){
	        notification.ledARGB = 0xff00ff00;
	        notification.ledOnMS = 300;
	        notification.ledOffMS = 1000;
	        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        }
        
        // Play a sound!
        if(notificationUri != ""){
			Uri uri = Uri.parse(notificationUri);
	      	notification.sound = uri;
        }
        
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
      	Intent intent = new Intent(context,TimerActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,  0,intent, Intent.FLAG_ACTIVITY_NEW_TASK);

        notification.setLatestEventInfo(context, text,
                       textLatest, contentIntent);

        // Create intent for cancelling the notification
        Context appContext = context.getApplicationContext();
        intent = new Intent(appContext, TimerReceiver.class);
        intent.setAction(CANCEL_NOTIFICATION);

        // Cancel the pending cancellation and create a new one
        PendingIntent pendingCancelIntent =
            PendingIntent.getBroadcast(appContext, 0, intent,
                                       PendingIntent.FLAG_CANCEL_CURRENT);

        // Schedule the cancellation
        if (settings.getBoolean("AutoClear", false)) {
            AlarmManager alarmMgr = (AlarmManager)context
                .getSystemService(Context.ALARM_SERVICE);
            alarmMgr.set(AlarmManager.ELAPSED_REALTIME,
                         SystemClock.elapsedRealtime() + 30000,
                         pendingCancelIntent);
        }

        // Show notification
        mNM.notify(0, notification);
	}

}