package com.payout.psg.model;

import java.util.HashMap;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="gateway_system")
public class GatewaySystem {
	
	@Id
	private Integer id;
		
	private boolean active = false;
	
	private HashMap<String,Object> configs = null;

	public HashMap<String,Object> getConfigs() {
		return configs;
	}

	public void setConfigs(HashMap<String,Object> configs) {
		this.configs = configs;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	
	
}
