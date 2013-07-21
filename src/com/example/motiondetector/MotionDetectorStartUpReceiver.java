package com.example.motiondetector;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MotionDetectorStartupReceiver extends BroadcastReceiver {

	private static final int FIVE_MINUTES = 5*60*1000;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		AlarmManager mgr = 
				(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent i = new Intent(context, AlarmReceiver.class);
			PendingIntent sender = PendingIntent.getBroadcast(context, 0, 
					i, PendingIntent.FLAG_CANCEL_CURRENT);
			Calendar now = Calendar.getInstance();
			now.add(Calendar.MINUTE, 2);
			mgr.setRepeating(AlarmManager.RTC_WAKEUP, 
					now.getTimeInMillis(), FIVE_MINUTES, sender);

	}

}
