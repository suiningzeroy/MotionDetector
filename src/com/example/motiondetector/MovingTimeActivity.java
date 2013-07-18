package com.example.motiondetector;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MovingTimeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_moving_time);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.moving_time, menu);
		return true;
	}

}
