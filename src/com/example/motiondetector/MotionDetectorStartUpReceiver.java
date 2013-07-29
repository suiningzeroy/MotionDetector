package com.example.motiondetector;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MotionDetectorStartupReceiver extends BroadcastReceiver {

	private static final int THIRTY_SECONDS = 30*1000;
	private static final String LOGGING_TAG = "MotionDetectorStartupReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.d(LOGGING_TAG,"onReceive");
		AlarmManager mgr = 
				(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent i = new Intent(context, AlarmReceiver.class);
			PendingIntent sender = PendingIntent.getBroadcast(context, 0, 
					i, PendingIntent.FLAG_CANCEL_CURRENT);
			Calendar now = Calendar.getInstance();
			now.add(Calendar.MINUTE, 2);
			mgr.setRepeating(AlarmManager.RTC_WAKEUP, 
					now.getTimeInMillis(), THIRTY_SECONDS, sender);
			Log.d(LOGGING_TAG,"setAlarm");
	}

}
