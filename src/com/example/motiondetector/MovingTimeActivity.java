package com.example.motiondetector;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class MovingTimeActivity extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_moving_time);
		final TextView movingTime = (TextView) findViewById(R.id.movingTime);
		final TextView percentage = (TextView) findViewById(R.id.percentage);
	}



}
