package com.example.motiondetector.service;

interface IMotionService{
	int getMovingTimeOfADay(in String date);
	double getPercentageOfMovingTimeOfADay(in String date);
}