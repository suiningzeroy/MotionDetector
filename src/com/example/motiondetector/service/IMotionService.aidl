package com.example.motiondetector.service;

interface IMotionService{
	float getMovingTimeOfADay(in String date);
	float getPercentageOfMovingTimeOfADay(in String date);
}