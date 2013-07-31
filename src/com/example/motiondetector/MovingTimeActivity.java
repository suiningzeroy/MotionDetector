package com.example.motiondetector;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.motiondetector.service.IMotionService;

public class MovingTimeActivity extends Activity {
	
	private static final String LOGGING_TAG = "MovingTime";
	private boolean bound = false;
	private float movingTimeInMinutes;
	private int databaseEntryCounts;
	private double movingPercentage;
	private static String selectedDate ;
	private IMotionService motionService;
	private TextView percentageView;
	private TextView movingTimeView;
	private TextView allCountsView;
	private EditText inputDate;
	private Button queryButton;
	private Button changeQueryDate;
	private static final DecimalFormat df = new DecimalFormat("#.##");
	private static final String DATE_PICKER_FRAGMENT_ID = "datePicker"; 

	private ServiceConnection connection = new ServiceConnection(){

		public void onServiceConnected(ComponentName className, IBinder service) {
			motionService = IMotionService.Stub.asInterface(service);
			Log.d(LOGGING_TAG,"Connected to service");
			try {
				movingTimeInMinutes = (float) motionService.getMovingTimeOfADay(selectedDate);
				movingPercentage = (double) motionService.getPercentageOfMovingTimeOfADay(selectedDate) * 100;
				databaseEntryCounts = (int) motionService.getAllCounts();
				
			} catch (RemoteException e) {
				Log.e(LOGGING_TAG, "Exception retrieving moving time from service",e);
			}
			
			refreshUI();
		}
		
		public void onServiceDisconnected(ComponentName className) {
			
		}
		
	};

	private void refreshUI(){
		runOnUiThread(new Runnable() {
			public void run() {
				movingTimeView.setText("You had been moving for " + df.format(movingTimeInMinutes) + " minutes"
										+ " on " + selectedDate + ".");
				if(movingPercentage >= 0)
					percentageView.setText("The percentage of moving time on " + selectedDate + " is : " + df.format(movingPercentage) + "%.");
				else
					percentageView.setText("No record on " + selectedDate + " thus no moving time percentage.");

				allCountsView.setText("You have " + Integer.toString(databaseEntryCounts) + " measurements in total since installation."); 
				inputDate.setText(selectedDate);
			}
		});
		unbindServiceIfBound();
	}

	@Override
	public void onPause(){
		super.onPause();
		unbindServiceIfBound();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_moving_time);
		movingTimeView = (TextView) findViewById(R.id.movingTime);
		percentageView = (TextView) findViewById(R.id.percentage);
		allCountsView = (TextView) findViewById(R.id.allCounts);
		inputDate = (EditText) findViewById(R.id.inputDate);
		
		selectedDate = MotionDetectorService.dateFormat.format(new java.util.Date()); 
		
		queryButton = (Button) findViewById(R.id.startQuery);
		queryButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
					if (!bound){
						bound = bindService(new Intent(MovingTimeActivity.this, MotionDetectorService.class), 
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
		unbindServiceIfBound();
		super.onDestroy();
	}

		
	private void unbindServiceIfBound(){
		if (bound){
			bound = false;
			unbindService(connection);
			Log.d(LOGGING_TAG,"unbindService");
		}
	}
	
	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getFragmentManager(), DATE_PICKER_FRAGMENT_ID);
	}
	
	private static class DatePickerFragment  extends DialogFragment 
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
			selectedDate = getSelectedDateString(year,monthOfYear,dayOfMonth);
		}
		
		private String getSelectedDateString(int year, int monthOfYear, int dayOfMonth){
			Calendar cl = Calendar.getInstance();
			cl.set(Calendar.YEAR, year);
			cl.set(Calendar.MONTH, monthOfYear);
			cl.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			cl.set(Calendar.HOUR_OF_DAY, 0);
				
			return MotionDetectorService.dateFormat.format(new Date(cl.getTimeInMillis())); 
		}
		
	}

}
