package com.payout.transfert.dao;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection="resources")
public class Resource{
	
	@Id
	private String id;
	
	private String name;
	
	private String phone;
	
	@Field(name="operator_code")
	private String operatorCode;
	
	@Field(name="last_balance")
	private Integer lastBalance;
	
	private Integer comission;
	
	private String operator_api_url;
	
	private String email;
	
	private String password;
	
	private LocalDateTime updatedAt = LocalDateTime.now();
	
	private HashMap<String,String> extrasData;
	
	private String cacheToken;
	
	public String getOperatorCode() {
		return operatorCode;
	}

	public void setOperatorCode(String operatorCode) {
		this.operatorCode = operatorCode;
	}

	public Integer getLastBalance() {
		return lastBalance;
	}

	public void setLastBalance(Integer lastBalance) {
		this.lastBalance = lastBalance;
	}

	public String getOperator_api_url() {
		return operator_api_url;
	}

	public void setOperator_api_url(String operator_api_url) {
		this.operator_api_url = operator_api_url;
	}

	public Integer getComission() {
		return comission;
	}

	public void setComission(Integer comission) {
		this.comission = comission;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public HashMap<String,String> getExtrasData() {
		return extrasData;
	}

	public void setExtrasData(HashMap<String,String> extrasData) {
		this.extrasData = extrasData;
	}

	public String getCacheToken() {
		return cacheToken;
	}

	public void setCacheToken(String cacheToken) {
		this.cacheToken = cacheToken;
	}

}
