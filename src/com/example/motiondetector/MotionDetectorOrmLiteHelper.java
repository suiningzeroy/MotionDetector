package com.example.motiondetector;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class MotionDetectorOrmLiteHelper extends OrmLiteSqliteOpenHelper {
	private Dao<Measurements, Integer> measurementDao = null;
	public static final String DB_NAME = "Measurements.db";
	public static final int DB_VERSION = 1;

	public MotionDetectorOrmLiteHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource arg1) {
		// TODO Auto-generated method stub
		try {
			Log.i(Measurements.class.getName(), "onCreating Bone db");
			TableUtils.createTable(connectionSource, Measurements.class);
		} catch (SQLException e) {
			Log.e(MotionDetectorOrmLiteHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		try {
			Log.i(MotionDetectorOrmLiteHelper.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, Measurements.class, true);
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
			TableUtils.dropTable(connectionSource, Measurements.class, true);
		} catch (SQLException e) {
			Log.e(MotionDetectorOrmLiteHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}
	
	public Dao<Measurements, Integer> getDao() throws SQLException {
		if (measurementDao == null) {
			measurementDao = getDao(Measurements.class);
		}
		return measurementDao;
	}

}
