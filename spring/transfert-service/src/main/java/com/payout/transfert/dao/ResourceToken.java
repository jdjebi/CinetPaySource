package com.payout.transfert.dao;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="resources_token")
public class ResourceToken {
	
	@Id
	private String id;

	private String operatorCode;
	
	private String token;
	
	private long savedAt = 0;
	
	private long expiredAt = 0;

	public String getOperatorCode() {
		return operatorCode;
	}

	public void setOperatorCode(String operatorCode) {
		this.operatorCode = operatorCode;
	}

	public long getSavedAt() {
		return savedAt;
	}

	public void setSavedAt(long savedAt) {
		this.savedAt = savedAt;
	}

	public long getExpiredAt() {
		return expiredAt;
	}

	public void setExpiredAt(long expiredAt) {
		this.expiredAt = expiredAt;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
