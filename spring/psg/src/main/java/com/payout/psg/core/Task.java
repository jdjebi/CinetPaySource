package com.payout.psg.core;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
	/*
	 * Version: 1 
	 */
	
	private String action;
	private String date = null;
	private String data = null;
	
	public Task(String action) {
		this.action = action;
		
		this.date = this.getCurrentTime();
		
	}
	
	public Task(String action, String data) {
		this.action = action;
		this.data = data;
		this.date = this.getCurrentTime();
	}
	
	public String getCurrentTime() {
		LocalDateTime time = LocalDateTime.now();
		DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
		return time.format(timeFormat);
	}
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}
