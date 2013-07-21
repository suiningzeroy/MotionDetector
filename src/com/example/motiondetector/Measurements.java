package com.example.motiondetector;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Measurments")
public class Measurements {
	@DatabaseField
	private int _id;
	private String date;
	private int measureTime;
	private String value;
	private boolean isMove;

}
