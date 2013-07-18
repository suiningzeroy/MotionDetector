package com.example.motiondetector;

import com.example.motiondetector.service.IMotionService;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MovingTimeActivity extends Activity {
	
	private static final String LOGGING_TAG = "MovingTime";
	
	private boolean bound = false;
	private float movingTime;
	private float percentageOfMovingTime;
	private String selectedDate;
	private IMotionService motionService;
	private TextView percentageView;
	private TextView movingTimeView;

	private ServiceConnection connection = new ServiceConnection(){

		public void onServiceConnected(ComponentName className, IBinder service) {
			motionService = IMotionService.Stub.asInterface(service);
			Log.d(LOGGING_TAG,"Connected to service");
			try {
				movingTime = (float) motionService.getMovingTimeOfADay(selectedDate);
				percentageOfMovingTime = (float) motionService.getPercentageOfMovingTimeOfADay(selectedDate);
				
			} catch (RemoteException e) {
				Log.e(LOGGING_TAG, "Exception retrieving moving time from service",e);
			}
		}
		
		public void onServiceDisconnected(ComponentName className) {
			
		}
		
	};

	@Override
	public void onStart(){
		super.onStart();
        // create initial list
		if (!bound){
			bound = bindService(
					new Intent(MovingTimeActivity.this, MotionDetectorService.class), 
					connection, Context.BIND_AUTO_CREATE);
			Log.d(LOGGING_TAG, "Bound to service: " + bound);
		}
		if (!bound){
			Log.e(LOGGING_TAG, "Failed to bind to service");
			throw new RuntimeException("Failed to find to service");
		}
	}
	
	@Override
	public void onPause(){
		super.onPause();
		if (bound){
			bound = false;
			unbindService(connection);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_moving_time);
		movingTimeView = (TextView) findViewById(R.id.movingTime);
		percentageView = (TextView) findViewById(R.id.percentage);
		
		refreshUI();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// disconnect from the stock service
		unbindService(connection);
	}

	private void refreshUI(){
		movingTimeView.setText(Float.toString(movingTime) + "second");
		percentageView.setText(Float.toString(percentageOfMovingTime) + "%.");
	}

}
