package com.example.motiondetector;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "motiondetector_measurements")
public class Measurement {
	
	@DatabaseField(generatedId = true)
	private int _id;
	@DatabaseField(canBeNull = false)
	private String date;
	@DatabaseField(canBeNull = false)
	private long measureTime;
	@DatabaseField(canBeNull = false)
	private double value;
	@DatabaseField(canBeNull = false)
	private boolean is_moving;
	
	public Measurement() {}
	
	public String getDate(){
		return this.date;
	}
	
	public void setDate(String measureDate){
		this.date =measureDate;
	}
	
	public long getMeasureTime(){
		return this.measureTime;
	}
	
	public void setMeasureTime(long measureTime){
		this.measureTime =measureTime;
	}
	public double getValue(){
		return this.value;
	}
	
	public void setValue(double measureValue){
		this.value = measureValue;
	}
	
	public boolean getIsMoving(){
		return this.is_moving;
	}
	public void setIsMoving(boolean isMove){
		this.is_moving = isMove;
	}
	
}
