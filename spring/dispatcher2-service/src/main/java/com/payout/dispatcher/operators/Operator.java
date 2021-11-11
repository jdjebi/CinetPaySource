package com.payout.dispatcher.operators;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;


import org.springframework.data.annotation.Id;

@Document(collection="operators")
public class Operator{

	@Id
	private String id = null;

	private String name;
	
	private String resourcesCode;
	
	private String urlSim;
	
	private String urlApi;
	
	private String topicSim;
	
	private String topicApi;
	
	private Api api;
	
	private Integer balance;
	
	private List<Sim> sims;
	
	private LocalDateTime updatedAt = LocalDateTime.now();

	public String getId(){
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getResourcesCode() {
		return resourcesCode;
	}

	public void setResourcesCode(String resourcesCode) {
		this.resourcesCode = resourcesCode;
	}

	public String getUrlSim() {
		return urlSim;
	}

	public void setUrlSim(String urlSim) {
		this.urlSim = urlSim;
	}

	public String getUrlApi() {
		return urlApi;
	}

	public void setUrlApi(String urlApi) {
		this.urlApi = urlApi;
	}

	public Api getApi() {
		return api;
	}

	public List<Sim> getSims() {
		return sims;
	}

	public void setSims(List<Sim> sims) {
		this.sims = sims;
	}

	public Integer getBalance() {
		return balance;
	}

	public void setBalance(Integer balance) {
		this.balance = balance;
	}

	public String getTopicSim() {
		return topicSim;
	}

	public void setTopicSim(String topicSim) {
		this.topicSim = topicSim;
	}

	public String getTopicApi() {
		return topicApi;
	}

	public void setTopicApi(String topicApi) {
		this.topicApi = topicApi;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	
}
