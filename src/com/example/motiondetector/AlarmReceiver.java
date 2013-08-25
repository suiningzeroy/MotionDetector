package com.example.motiondetector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

	private static final String LOGGING_TAG = "AlarmReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(LOGGING_TAG,"onReceive");
		Intent service = new Intent(context, MotionDetectorService.class);
		context.startService(service);
	}

}
