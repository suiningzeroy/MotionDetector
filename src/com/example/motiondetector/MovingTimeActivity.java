package com.example.motiondetector;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import com.example.motiondetector.service.IMotionService;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MovingTimeActivity extends Activity {
	
	private static final String LOGGING_TAG = "MovingTime";
	private final SimpleDateFormat dateFormat =new SimpleDateFormat("yyyyMMdd",Locale.CHINA);  
	private final SimpleDateFormat mDateFormat =new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
	private boolean bound = false;
	private float movingTime;
	private int allCounts;
	private double percentageOfMovingTime;
	private static String selectedDate ;
	private IMotionService motionService;
	private TextView percentageView;
	private TextView movingTimeView;
	private TextView allCountsView;
	private EditText inputDate;
	private Button startQuery;
	private Button changeQueryDate;

	private ServiceConnection connection = new ServiceConnection(){

		public void onServiceConnected(ComponentName className, IBinder service) {
			motionService = IMotionService.Stub.asInterface(service);
			Log.d(LOGGING_TAG,"Connected to service");
			try {
				movingTime = (float) motionService.getMovingTimeOfADay(selectedDate);
				percentageOfMovingTime = (double) motionService.getPercentageOfMovingTimeOfADay(selectedDate);
				allCounts = (int) motionService.getAllCounts();
				
			} catch (RemoteException e) {
				Log.e(LOGGING_TAG, "Exception retrieving moving time from service",e);
			}
			
			//Log.d(LOGGING_TAG,"movingTime | percentageOfMovingTime = " + movingTime + " | " + (float)percentageOfMovingTime);
			refreshUI();
		}
		
		public void onServiceDisconnected(ComponentName className) {
			
		}
		
	};

	@Override
	public void onStart(){
		super.onStart();
		/*if (!bound){
			bound = bindService(
					new Intent(MovingTimeActivity.this, MotionDetectorService.class), 
					connection, Context.BIND_AUTO_CREATE);
			Log.d(LOGGING_TAG, "Bound to service in onStart: " + bound);
		
		}
		if (!bound){
			Log.e(LOGGING_TAG, "Failed to bind to service");
			throw new RuntimeException("Failed to find to service");
		}*/
	}
	
	@Override
	public void onPause(){
		super.onPause();
		unbindServiceIfBinded();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_moving_time);
		movingTimeView = (TextView) findViewById(R.id.movingTime);
		percentageView = (TextView) findViewById(R.id.percentage);
		allCountsView = (TextView) findViewById(R.id.allCounts);
		inputDate = (EditText) findViewById(R.id.inputDate);
		selectedDate = dateFormat.format(new java.util.Date()); 
		startQuery = (Button) findViewById(R.id.startQuery);
		startQuery.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
					if (!bound){
						bound = bindService(
								new Intent(MovingTimeActivity.this, MotionDetectorService.class), 
								connection, Context.BIND_AUTO_CREATE);
						Log.d(LOGGING_TAG, "Bound to service in onStart: " + bound);
					}
					if (!bound){
						Log.e(LOGGING_TAG, "Failed to bind to service");
						throw new RuntimeException("Failed to find to service");
					}
			}
			
		});
		
		changeQueryDate = (Button) findViewById(R.id.changeDate);
		changeQueryDate.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				showDatePickerDialog(v);
				}
			});
		
		Timer updateTimer = new Timer("motionDetectorUpdate");
		updateTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				refreshUI();
			}
		}, 0, 500);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// disconnect from the service
		unbindServiceIfBinded();
	}

	private void refreshUI(){
		runOnUiThread(new Runnable() {
			public void run() {
				movingTimeView.setText("You had been moving for " + Float.toString(movingTime) + " minutes"
										+ " on " + selectedDate + ".");
				percentageView.setText("The percentage of moving time on " + selectedDate + " : " + Double.toString(percentageOfMovingTime*100) + "%.");
				allCountsView.setText("You total have " + Integer.toString(allCounts) + " measurements."); 
				inputDate.setText(selectedDate);
			}
		});
		
		unbindServiceIfBinded();
	}
		
	private void unbindServiceIfBinded(){
		if (bound){
			bound = false;
			unbindService(connection);
			Log.d(LOGGING_TAG,"unbindService in onPause");
		}
	}
	
	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getFragmentManager(), "datePicker");
	}
	
	public static class DatePickerFragment  extends DialogFragment 
				implements DatePickerDialog.OnDateSetListener  {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current time as the default values for the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			 
			 return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			selectedDate = getSelectedDateString(year,monthOfYear,dayOfMonth);
						
		}
		private String getSelectedDateString(int year, int monthOfYear,
			int dayOfMonth){
			String dateString;
			if (Integer.toString(monthOfYear + 1).length() == 1)
				dateString = Integer.toString(year) + "0" +Integer.toString(monthOfYear+1) + Integer.toString(dayOfMonth);
				else
					dateString = Integer.toString(year) + Integer.toString(monthOfYear+1) + Integer.toString(dayOfMonth);
				
				return dateString; 
		}
		
	}

}
