package com.example.motiondetector;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class MotionDetectorOrmLiteHelper extends OrmLiteSqliteOpenHelper {
	
	private Dao<Measurement, Integer> measurementDao = null;
	private RuntimeExceptionDao<Measurement, Integer> measureRuntimeDao = null;
	public static final String DB_NAME = "md_measurements";
	public static final int DB_VERSION = 1;

	public MotionDetectorOrmLiteHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource arg1) {
		try {
			Log.i(Measurement.class.getName(), "onCreating " + DB_NAME + " db");
			TableUtils.createTable(connectionSource, Measurement.class);
		} catch (SQLException e) {
			Log.e(MotionDetectorOrmLiteHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource arg1, int arg2,
			int arg3) {
		try {
			Log.i(MotionDetectorOrmLiteHelper.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, Measurement.class, true);
			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(MotionDetectorOrmLiteHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}

	}
	
	public void dropAllTable(){
		try {
			Log.i(MotionDetectorOrmLiteHelper.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, Measurement.class, true);
		} catch (SQLException e) {
			Log.e(MotionDetectorOrmLiteHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}
	
	public Dao<Measurement, Integer> getDao() throws SQLException {
		if (measurementDao == null) {
			measurementDao = getDao(Measurement.class);
		}
		return measurementDao;
	}
	
	public RuntimeExceptionDao<Measurement, Integer> getSimpleDataDao() {
		if (measureRuntimeDao == null) {
			measureRuntimeDao = getRuntimeExceptionDao(Measurement.class);
		}
		return measureRuntimeDao;
	}

	/**
	* Close the database connections and clear any cached DAOs.
	*/
	@Override
	public void close() {
		measureRuntimeDao = null;
		super.close();
	}

}
