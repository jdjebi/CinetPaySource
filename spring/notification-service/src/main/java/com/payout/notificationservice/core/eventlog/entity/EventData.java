package com.payout.notificationservice.core.eventlog.entity;

import java.util.HashMap;

public class EventData {
	
	private HashMap<String,Object> map = new HashMap<String,Object>();

	public HashMap<String,Object> getMap() {
		return map;
	}

	public void setMap(HashMap<String,Object> map) {
		this.map = map;
	}
	
}
