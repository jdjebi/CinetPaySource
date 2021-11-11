package com.payout.psg.model;

import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection="transactions")
public class TransactionMongo extends TransactionAbstract{
	
	@Id
	private String id = null;
	private String remoteId = null;
	private String country = null;
	private String paymentmethod = null;
	private String batchnumber = null;
	private int gametrx_id;
	private String phone = null;
	private Integer amount = null;
	private String status = null;
	
	private String operator = null;
	
	private String ressourcesCode = null;
	
	@Field(name="created_at")
	private Date createdAt = new Date();
	
	@Field(name="finished_at")
	private Date finishedAt;
	
	@Field(name="pause_comment")
	private String pauseComment = null;
	
	public TransactionMongo() {
		
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemoteId() {
		return remoteId;
	}

	public void setRemoteId(String remoteId) {
		this.remoteId = remoteId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPaymentmethod() {
		return paymentmethod;
	}

	public void setPaymentmethod(String paymentmethod) {
		this.paymentmethod = paymentmethod;
	}

	public int getGametrx_id() {
		return gametrx_id;
	}

	public void setGametrx_id(int gametrx_id) {
		this.gametrx_id = gametrx_id;
	}

	public String getBatchnumber() {
		return batchnumber;
	}

	public void setBatchnumber(String batchnumber) {
		this.batchnumber = batchnumber;
	}
	
	public String toString() {
		return "Trx:" + this.id + "@" + this.operator + "[" + this.amount + "]";
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getRessourcesCode() {
		return ressourcesCode;
	}

	public void setRessourcesCode(String ressourcesCode) {
		this.ressourcesCode = ressourcesCode;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getPauseComment() {
		return pauseComment;
	}

	public void setPauseComment(String pauseComment) {
		this.pauseComment = pauseComment;
	}

	public Date getFinishedAt() {
		return finishedAt;
	}

	public void setFinishedAt(Date finishedAt) {
		this.finishedAt = finishedAt;
	}

}