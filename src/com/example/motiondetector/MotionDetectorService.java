package com.example.motiondetector;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.example.motiondetector.service.IMotionService;
import com.j256.ormlite.android.apptools.OpenHelperManager;

public class MotionDetectorService extends Service {
	private static final String TAG = "MotionDetectorService";
	private static final String YES = "YES";
	private static final String NO = "NO";
	private static final int FIVE_MINUTES = 5;
	private static final int MEASURE_DURATION = 1000*5;
	private static final float ACCELERATION_OF_MOVE = 0.5f;
	private MotionDetectorOrmLiteHelper ormLiteHelper = null;
	
	private SensorManager sensorManager;
	private final double calibration = SensorManager.STANDARD_GRAVITY;
	private double currentAcceleration;
	private double maxAcceleration;
	private final SimpleDateFormat dateFormat =new SimpleDateFormat("yyyyMMDD");  
	private final SimpleDateFormat timeFormate =new SimpleDateFormat("hh:mm:ss");  
	private String measureTime;
	private String measureDate;
	
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
		
		measureAcceleratometer(MEASURE_DURATION);
		return Service.START_STICKY;
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		sensorManager.unregisterListener(sensorEventListener);
		
		if (ormLiteHelper != null) {
				OpenHelperManager.releaseHelper();
				ormLiteHelper = null;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return new IMotionService.Stub() {
			@Override
			public double getPercentageOfMovingTimeOfADay(String date)
					throws RemoteException {
				// TODO Auto-generated method stub
				return getMovingPercentage(date);
			}

			@Override
			public int getMovingTimeOfADay(String date) throws RemoteException {
				// TODO Auto-generated method stub
				return getMovingTime(date);
			}
		};
	}
	
	private void measureAcceleratometer(int delayMillis){
		Handler handler = new Handler();
		Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		Runnable autoStop = new Runnable(){
			@Override
			public void run() {
				sensorManager.unregisterListener(sensorEventListener);
				writeDataToDB();
			}
		};
		
		sensorManager.registerListener(sensorEventListener,
				accelerometer,
				SensorManager.SENSOR_DELAY_FASTEST);
		handler.postDelayed(autoStop, delayMillis);
	}
	
	private String isMoving(double acceleration){
		return acceleration > ACCELERATION_OF_MOVE ? YES : NO;
	}
	
	private void writeDataToDB(){
		getCurrentDate();
		Measurement measure = new Measurement();
		measure.setDate(measureDate);
		measure.setMeasureTime(measureTime);
		measure.setIsMove(isMoving(maxAcceleration));
		measure.setValue(maxAcceleration);
		insert(measure);
	}
	
	private MotionDetectorOrmLiteHelper getHelper() {
		if (ormLiteHelper == null) {
			ormLiteHelper =
				OpenHelperManager.getHelper(this, MotionDetectorOrmLiteHelper.class);
		}
		return ormLiteHelper;
	}
	
	private void insert(Measurement measurement){
		try {
			getHelper().getDao().create(measurement);
		}catch (SQLException e) {
			Log.d("MotionDetectorService", "writing accelerometer reading to database failed");
			e.printStackTrace();
		}
	}
	
	private int QueryForCounts(String colomnName1, String arg1,String colomnName2, String arg2) {
		int counts = 0;
		try{
			 counts = getHelper().getDao().queryBuilder().where().eq(colomnName1, arg1)
						.and().eq(colomnName2,arg2)
						.query().size();
			
		}catch (SQLException e){
			e.printStackTrace();
		}
		
		return counts;
	}
	
	private int getMovingTime(String measureDate){
		int moveCounts = QueryForCounts("date",measureDate, "isMove", YES);
		return moveCounts*FIVE_MINUTES;
	}
	
	private double getMovingPercentage(String measureDate){
		int moveCounts = QueryForCounts("date",measureDate, "isMove", YES);
		int stillCounts = QueryForCounts("date",measureDate, "isMove", NO);
		
		double percentage = moveCounts / (moveCounts + stillCounts);
		
		return percentage;
	}

	private void getCurrentDate(){
		measureDate = dateFormat.format(new java.util.Date()); 
		measureTime = timeFormate.format(new java.util.Date());
	}

}
