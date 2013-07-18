package com.example.motiondetector;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;

public class MotionDetectorService extends Service {
	private static final String TAG = "MotionDetectorService";
	private static final int MEASURE_DURATION = 1000*5;
	private static final float ACCELERATION_OF_MOVE = 0.5f;
	
	private SensorManager sensorManager;
	private final double calibration = SensorManager.STANDARD_GRAVITY;
	private double currentAcceleration;
	private double maxAcceleration;
	
	private final SensorEventListener sensorEventListener = new SensorEventListener() {
		
		public void onAccuracyChanged(Sensor sensor, int accuracy) { }
		
		public void onSensorChanged(SensorEvent event) {
			double x = event.values[0];
			double y = event.values[1];
			double z = event.values[2];
			double a = Math.round(Math.sqrt(Math.pow(x, 2) +
						Math.pow(y, 2) +
						Math.pow(z, 2)));
			currentAcceleration = Math.abs((float)(a-calibration));
			if (currentAcceleration > maxAcceleration)
				maxAcceleration = currentAcceleration;
		}
	};
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		
		return Service.START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void measureAcceleratometer(int delayMIllis){
		Handler handler = new Handler();
		Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		Runnable autoStop = new Runnable(){
			@Override
			public void run() {
				sensorManager.unregisterListener(sensorEventListener);
				writeDataInDB();
			}
		};
		
		sensorManager.registerListener(sensorEventListener,
				accelerometer,
				SensorManager.SENSOR_DELAY_FASTEST);
		handler.postDelayed(autoStop, delayMIllis);
	}
	
	private boolean isMoving(float acceleration){
		return acceleration > ACCELERATION_OF_MOVE ? true : false;
	}
	
	private void writeDataInDB(){
		
	}

}
