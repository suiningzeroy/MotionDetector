package com.example.motiondetector.service;

interface IMotionService{
	float getMovingTimeOfADay(in String date);
	int getAllCounts();
	double getPercentageOfMovingTimeOfADay(in String date);
}