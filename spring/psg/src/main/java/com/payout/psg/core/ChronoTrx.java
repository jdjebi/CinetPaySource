package com.payout.psg.core;

public class ChronoTrx {
	
	private long start;
	private long end;
	private long time;
	
	public ChronoTrx(){
		
	}
	
	public void start() {
		this.start = System.currentTimeMillis();
	}
	
	public void stop() {
		this.end = System.currentTimeMillis();
		this.time = this.end - this.start;
	}
	
	public void stopAndDisplay(String message) {
		this.stop();
		this.display(message);
	}
	
	public long getTime() {
		return this.time;
	}
	
	public String getTimeStr() {
		return this.time + "ms";
	}
	
	public void display() {
		System.out.println(this.time);
	}
	
	public void display(String message) {
		String log = message + ": " + this.time +"ms";
		System.out.println(log);
	}
}
