/**
 * @author Jean-Marc Dje Bi
 * @since 21-07-2021
 * @version 1
 */
package com.payout.dispatcher.transactions;

import java.util.Date;
import java.util.HashMap;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Classe representant une transaction
 */
@Document(collection="transactions")
public class Transaction {
	
	/** Identifiant */
	@Id
	private String id = null;
	
	/** Identifiant de la transaction du cote de CinetPay*/
	@Field(name="transaction_id")
	private String transactionId;
	
	/** Identifiant du client proprietaire de la transaction du cote de CinetPay */
	@Field(name="client_transaction_id")
	private String clientTransactionId;
	
	/** Lot de la transaction */
	private String batchnumber;
	
	/** Code representant un operateur */
	private String operator;
	
	/** Numero de destination */
	private String phone;
	
	/** Prefixe */
	private String prefix = null;
	
	/** Montant */
	private Integer amount;
	
	/** Devise */
	private String currency = null;
	
	/** Pays du numero de destination */
	private String country = null;
	
	/** URL de notification */
	@Field(name="notify_url")
	private String notifyUrl;		
		
	/** Statut CinetPay */
	private String status = null;
	
	/** Statut du traitement interne de la transaction */
	@Field(name="system_status")
	private String systemStatus = null;
	
	@Field(name="created_at")
	private Date createdAt = new Date();
	
	@Field(name="reveived_at")
	private Date receivedAt = new Date();
	
	@Field(name="finished_at")
	private Date finishedAt;
	
	/**
	 * Message indicant la raison de la pause de la transaction
	 */
	@Field(name="pause_comment")
	private String pauseComment = null;
	
	/**
	 * Message de l'operateur pendant le transfert de la transaction
	 */
	private String operatorComment = null;
	
	/**
	 * Message final sur le traitement de la transaction
	 */
	private String comment = null;
	
	/**
	 * Identifiant de la transaction du cote de l'operateur
	 */
	private String operatorTransactionId = null;
	
	/**
	 * Donnees associees a la reponse de resource l'operateur
	 */
	private HashMap<String,Object> operatorDebugResponse;
	
	/**
	 * Nom de la resource utilisee
	 */
	private String resourceUsed = null;

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getClientTransactionId() {
		return clientTransactionId;
	}

	public void setClientTransactionId(String clientTransactionId) {
		this.clientTransactionId = clientTransactionId;
	}

	public String getBatchnumber() {
		return batchnumber;
	}

	public void setBatchnumber(String batchnumber) {
		this.batchnumber = batchnumber;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSystemStatus() {
		return systemStatus;
	}

	public void setSystemStatus(String systemStatus) {
		this.systemStatus = systemStatus;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getReceivedAt() {
		return receivedAt;
	}

	public void setReceivedAt(Date receivedAt) {
		this.receivedAt = receivedAt;
	}

	public Date getFinishedAt() {
		return finishedAt;
	}

	public void setFinishedAt(Date finishedAt) {
		this.finishedAt = finishedAt;
	}

	public String getPauseComment() {
		return pauseComment;
	}

	public void setPauseComment(String pauseComment) {
		this.pauseComment = pauseComment;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public String getOperatorComment() {
		return operatorComment;
	}

	public void setOperatorComment(String operatorComment) {
		this.operatorComment = operatorComment;
	}

	public String getOperatorTransactionId() {
		return operatorTransactionId;
	}

	public void setOperatorTransactionId(String operatorTransactionId) {
		this.operatorTransactionId = operatorTransactionId;
	}

	public String getResourceUsed() {
		return resourceUsed;
	}

	public void setResourceUsed(String resourceUsed) {
		this.resourceUsed = resourceUsed;
	}

	public HashMap<String,Object> getOperatorDebugResponse() {
		return operatorDebugResponse;
	}

	public void setOperatorDebugResponse(HashMap<String,Object> operatorDebugResponse) {
		this.operatorDebugResponse = operatorDebugResponse;
	}

}
