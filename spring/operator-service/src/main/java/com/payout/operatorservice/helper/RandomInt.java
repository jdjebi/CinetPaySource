package com.payout.operatorservice.helper;

public class RandomInt {

	public static Integer getRandomNumber(int min, int max) {
	    return (int) ((Math.random() * (max - min)) + min);
	}
}
