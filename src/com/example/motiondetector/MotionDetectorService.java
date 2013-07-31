package com.example.motiondetector;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import android.os.PowerManager;
import android.os.RemoteException;
import android.util.Log;

import com.example.motiondetector.service.IMotionService;
import com.j256.ormlite.android.apptools.OpenHelperManager;

public class MotionDetectorService extends Service {
	
	private static final int NUM_READINGS_TO_SKIP = 5;
	private static final String LOGGING_TAG = "MotionDetectorService";
	private static final boolean YES = true;
	private static final boolean NO = false;
	private static final float MILLIS_IN_ONE_MINUTE = 60 * 1000f;
	private static final int MEASURE_DURATION_IN_MILLIS = 5 * 1000;
	private static final float ACCELERATION_DEVIATION_WHEN_MOVING = 1.5f;
	private MotionDetectorOrmLiteHelper ormLiteHelper = null;
	
	private SensorManager sensorManager;
	private double maxDeviation = 0d;
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);  
	private long measureTime;
	private String measureDate;
	private static PowerManager.WakeLock wakeLock = null;
	private static final String LOCK_TAG = "com.example.motiondetector";
	
	private static final String COLUMN_DATE = "date";
	private static final String COLUMN_ISMOVING = "is_moving";

	public static synchronized void acquireLock(Context ctx){
		if (wakeLock == null){
			PowerManager pMgr = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
			wakeLock = pMgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_TAG);
			wakeLock.setReferenceCounted(true);
		}
		wakeLock.acquire();
	}
	
	public static synchronized void releaseLock(){
		if (wakeLock != null){
			wakeLock.release();
		}
	}

	private final SensorEventListener accelListener = new SensorEventListener() {
		private int sensorReadingCount = 0;
		public void onAccuracyChanged(Sensor sensor, int accuracy) { }
		
		public void onSensorChanged(SensorEvent event) {
			sensorReadingCount++;
			if(sensorReadingCount < NUM_READINGS_TO_SKIP)
				return;
			double x = event.values[0];
			double y = event.values[1];
			double z = event.values[2];
			double absValue = Math.sqrt( x * x + y * y + z * z);
			double currentDeviation = Math.abs(absValue - SensorManager.STANDARD_GRAVITY);
			if (currentDeviation > maxDeviation)
				maxDeviation = currentDeviation;
		}
	};
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		doAccelerometerMeasuring(MEASURE_DURATION_IN_MILLIS);
		return Service.START_STICKY;
	}
	
	private void doAccelerometerMeasuring(int delayMillis){
		acquireLock(this);
		Handler handler = new Handler();
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		Runnable autoStop = new Runnable(){
			@Override
			public void run() {
				sensorManager.unregisterListener(accelListener);
				writeDataToDB();
				releaseLock();
			}
		};

		sensorManager.registerListener(accelListener, accelerometer,
				SensorManager.SENSOR_DELAY_FASTEST);
		handler.postDelayed(autoStop, delayMillis);
		Date now = new Date();
		measureTime = now.getTime();
		measureDate = dateFormat.format(now);
	}
	
	private void writeDataToDB(){		
		Measurement measure = new Measurement();
		measure.setDate(measureDate);
		measure.setMeasureTime(measureTime);
		measure.setIsMoving(isMoving(maxDeviation));
		measure.setValue(maxDeviation);
		Log.i("Write to Database", "measurement =" + measure.getDate() +"|"+ measure.getIsMoving() 
				+"|"+ measure.getMeasureTime()+"|"+ measure.getValue());
		insertMeasurementToDb(measure);
	}

		
	@Override
	public void onDestroy() {
		//make sure the wakelock is released.
		releaseLock();
		if (ormLiteHelper != null) {
			OpenHelperManager.releaseHelper();
			ormLiteHelper = null;
		}
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intentNotUsed) {
		return new IMotionService.Stub() {
			@Override
			public double getPercentageOfMovingTimeOfADay(String date)
					throws RemoteException {
				return getMovingPercentage(date);
			}

			@Override
			public float getMovingTimeOfADay(String date) throws RemoteException {
				return getMovingTime(date);
			}

			@Override
			public int getAllCounts() throws RemoteException {
				return QueryForAllCounts();
			}
		};
	}
	
	
	private boolean isMoving(double accelDeviation){
		return accelDeviation > ACCELERATION_DEVIATION_WHEN_MOVING ? YES : NO;
	}
	
	
	private void insertMeasurementToDb(Measurement measurement){
		try {
			getHelper().getDao().create(measurement);
		}catch (SQLException e) {
			Log.d(LOGGING_TAG, "writing measurement to database failed");
			e.printStackTrace();
		}
	}

	private MotionDetectorOrmLiteHelper getHelper() {
		if (ormLiteHelper == null) {
			ormLiteHelper =
				OpenHelperManager.getHelper(this, MotionDetectorOrmLiteHelper.class);
		}
		return ormLiteHelper;
	}

	private float getMovingTime(String measureDate){
		int moveCounts = QueryForCounts(COLUMN_DATE, measureDate, COLUMN_ISMOVING, YES);
		return moveCounts * (float) MotionDetectorStartUpReceiver.SAMPLING_INTERVAL_IN_MILLIS 
				/ (float) MILLIS_IN_ONE_MINUTE ;
	}
	
	private double getMovingPercentage(String measureDate){
		
		double movingPercentage = 0;
		int moveCounts = QueryForCounts(COLUMN_DATE, measureDate, COLUMN_ISMOVING, YES);
		int stillCounts = QueryForCounts(COLUMN_DATE, measureDate, COLUMN_ISMOVING, NO);
		boolean nonZeroMeasurement = ((moveCounts + stillCounts) > 0 );
		
		Log.d(LOGGING_TAG,"There exists measurement matching the query date:  " + Boolean.toString(nonZeroMeasurement));
		
		if (nonZeroMeasurement){
			movingPercentage = (double)moveCounts / (double) (moveCounts + stillCounts) ;
		} else{
			movingPercentage = -1;
		}
		
		Log.d(LOGGING_TAG,"percentage | percentage = " + movingPercentage );
		return movingPercentage;
	}
	
	private int QueryForCounts(String colomnName1, String arg1, String colomnName2, boolean arg2) {
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
		int countAll = 0;
		try{
			countAll = getHelper().getDao().queryForAll().size();
		}catch (SQLException e){
			e.printStackTrace();
		}
		
		return countAll;
	}
	
}
