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
	private String measureTime;
	@DatabaseField(canBeNull = false)
	private double value;
	@DatabaseField(canBeNull = false)
	private boolean isMove;
	
	public Measurement() {}
	
	public String getDate(){
		return this.date;
	}
	
	public void setDate(String measureDate){
		this.date =measureDate;
	}
	
	public String getMeasureTime(){
		return this.measureTime;
	}
	
	public void setMeasureTime(String measureTime){
		this.measureTime =measureTime;
	}
	public double getValue(){
		return this.value;
	}
	
	public void setValue(double measureValue){
		this.value = measureValue;
	}
	
	public boolean getIsMove(){
		return this.isMove;
	}
	public void setIsMove(boolean isMove){
		this.isMove = isMove;
	}
	
}
