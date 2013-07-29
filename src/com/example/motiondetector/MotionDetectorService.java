package com.example.motiondetector;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.Service;
import android.content.Context;
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
import com.j256.ormlite.stmt.DeleteBuilder;

public class MotionDetectorService extends Service {
	private static final String LOGGING_TAG = "MotionDetectorService";
	private static final boolean YES = true;
	private static final boolean NO = false;
	private static final float HALF_MINUTES = 0.5f;
	private static final int MEASURE_DURATION = 1000*5;
	private static final float ACCELERATION_OF_MOVE = 0.5f;
	private MotionDetectorOrmLiteHelper ormLiteHelper = null;
	
	private SensorManager sensorManager;
	private final double calibration = SensorManager.STANDARD_GRAVITY;
	private double currentAcceleration;
	private double maxAcceleration;
	private final SimpleDateFormat dateFormat =new SimpleDateFormat("yyyyMMdd",Locale.CHINA);  
	private final SimpleDateFormat timeFormate =new SimpleDateFormat("hh:mm:ss",Locale.CHINA);  
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
		//Log.i(LOGGING_TAG, "onCreate");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//Log.i(LOGGING_TAG, "onStartCommand");
		
		measureAccelerometerReading(MEASURE_DURATION);
		AlarmReceiver.releaseLock();
		return Service.START_STICKY;
	}
		
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		//Log.i(LOGGING_TAG, "onDestroy");
		
		if (ormLiteHelper != null) {
				OpenHelperManager.releaseHelper();
				ormLiteHelper = null;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		//Log.i(LOGGING_TAG, "onBind");
		return new IMotionService.Stub() {
			@Override
			public double getPercentageOfMovingTimeOfADay(String date)
					throws RemoteException {
				// TODO Auto-generated method stub
				return getMovingPercentage(date);
			}

			@Override
			public float getMovingTimeOfADay(String date) throws RemoteException {
				// TODO Auto-generated method stub
				return getMovingTime(date);
			}

			@Override
			public int getAllCounts() throws RemoteException {
				// TODO Auto-generated method stub
				return QueryForAllCounts();
			}
		};
	}
	
	private void measureAccelerometerReading(int delayMillis){
		Handler handler = new Handler();
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
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
	
	private boolean isMoving(double acceleration){
		return acceleration > ACCELERATION_OF_MOVE ? YES : NO;
	}
	
	private void writeDataToDB(){		
		//Log.i("autostop", "writeDataToDB start");
		getCurrentDate();
		Measurement measure = new Measurement();
		measure.setDate(measureDate);
		measure.setMeasureTime(measureTime);
		measure.setIsMove(isMoving(maxAcceleration/SensorManager.STANDARD_GRAVITY));
		measure.setValue(maxAcceleration/SensorManager.STANDARD_GRAVITY);
		Log.i("autostop", "measure =" + measure.getDate() +"|"+ measure.getIsMove() 
				+"|"+ measure.getMeasureTime()+"|"+ measure.getValue());
		insert(measure);
		maxAcceleration = 0;
	}
	
	private MotionDetectorOrmLiteHelper getHelper() {
		if (ormLiteHelper == null) {
			ormLiteHelper =
				OpenHelperManager.getHelper(this, MotionDetectorOrmLiteHelper.class);
		}
		return ormLiteHelper;
	}
	
	private int delete(String colomnName1, String arg1){
		try {
			DeleteBuilder<Measurement, Integer> deleteBuilder = getHelper().getDao().deleteBuilder();
			deleteBuilder.where().eq(colomnName1,arg1);
			return deleteBuilder.delete();
		}catch (SQLException e) {
			Log.d(LOGGING_TAG, "delete failed");
			e.printStackTrace();
		}

		return 0 ;	

	}
	
	private void insert(Measurement measurement){
		try {
			getHelper().getDao().create(measurement);
		}catch (SQLException e) {
			Log.d(LOGGING_TAG, "writing accelerometer reading to database failed");
			e.printStackTrace();
		}
	}
	
	private int QueryForCounts(String colomnName1, String arg1,String colomnName2, boolean arg2) {
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
	
	private int QueryForAllCounts(){
		int count1 = 0;
		try{
			count1 = getHelper().getDao().queryForAll().size();
		}catch (SQLException e){
			e.printStackTrace();
		}
		
		return count1;
	}
	
	private float getMovingTime(String measureDate){
		int moveCounts = QueryForCounts("date",measureDate, "isMove", YES);
		return moveCounts*HALF_MINUTES;
	}
	
	private  double getMovingPercentage(String measureDate){
		
		double percentage = 0;
		int moveCounts = QueryForCounts("date",measureDate, "isMove", YES);
		int stillCounts = QueryForCounts("date",measureDate, "isMove", NO);
		
		Log.d(LOGGING_TAG,"Boolean  " + Boolean.toString((moveCounts + stillCounts) != 0));
		
		if ((moveCounts + stillCounts) != 0 ){
			percentage = (double)moveCounts / (moveCounts + stillCounts) ;
		
			//Log.d(LOGGING_TAG,"percentage = " 
			//	+ (double) moveCounts / (moveCounts + stillCounts)+ " | " + percentage);
		}
		else{
			percentage = 0.01;
		}
		Log.d(LOGGING_TAG,"percentage | percentage = " 
				+ percentage );
		
		return percentage;
	}

	private void getCurrentDate(){
		measureDate = dateFormat.format(new java.util.Date()); 
		//Log.i("autostop", "measureDate =" + measureDate);
		measureTime = timeFormate.format(new java.util.Date());
	}

}
