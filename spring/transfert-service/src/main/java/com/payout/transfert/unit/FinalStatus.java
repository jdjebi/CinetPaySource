/**
 * @author Jean-Marc Dje Bi
 * @since 20-08-2021
 * @version 1
 */
package com.payout.transfert.unit;

import java.util.HashMap;

/**
 * Structure de donnees universelle du status final d'une transaction
 */
public class FinalStatus {

	/**
	 * Statut final de la transaction
	 * - PAUSE: 
	 * Probleme lors de l'envoie de la transaction ou d'un probleme technique du cote de l'operateur
	 * - SUCCESS: 
	 * Le traitement de la transaction a reussie
	 * - FAILURE: 
	 * Echec du traitement de la transaction cause soit par une erreur inconnue de cote de l'operateur soit par
	 * un probleme dans le structure de la transaction (Ex: Montant de la transaction inferieur au minimum)
	 */
	private String status;
	
	/**
	 * Commentaire indicatif concernant l'etat d'une transaction
	 */
	private String comment;
	
	/**
	 * Donnees associes a la reponse
	 */
	private HashMap<String, Object> data = new HashMap<String, Object>();
	
	/**
	 * Alias de l'operateur
	 */
	private String operatorAlias = null;
	
	/**
	 * Alias de la resource utilise
	 */
	private String operatorResource = null;
	
	/**
	 * Statut de la resource
	 */
	private Boolean resourceStatus = null;
	
	/**
	 * Message associe a la reponse de l'operateur
	 */
	private String operatorComment = null;
	
	/**
	 * Indicateur du type d'evenement kafka a produire
	 */
	private String kafkaEventTag = null;
	
	/**
	 * Solde de l'operateur apres traitement
	 */
	private Integer currentBalance = null;
	
	/**
	 * Identification de la transaction de cote de l'operateur
	 */
	private String operatorTransactionId = null;
	
	/**
	 * Donnees specifiques associees a la reponse de l'operateur
	 */
	private HashMap<String, Object> operatorData = new HashMap<String, Object>();
	
	/**
	 * Contient toutes les donnees liees a la reponse
	 */
	private HashMap<String, Object> bigdata = new HashMap<String, Object>();

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public HashMap<String, Object> getData() {
		return data;
	}

	public void setData(HashMap<String, Object> data) {
		this.data = data;
	}

	public String getOperatorComment() {
		return operatorComment;
	}

	public void setOperatorComment(String operatorComment) {
		this.operatorComment = operatorComment;
	}

	public HashMap<String, Object> getOperatorData() {
		return operatorData;
	}

	public void setOperatorData(HashMap<String, Object> operatorData) {
		this.operatorData = operatorData;
	}

	public String getOperatorAlias() {
		return operatorAlias;
	}

	public void setOperatorAlias(String operatorAlias) {
		this.operatorAlias = operatorAlias;
	}

	public String getOperatorResource() {
		return operatorResource;
	}

	public void setOperatorResource(String operatorResource) {
		this.operatorResource = operatorResource;
	}

	public Boolean getResourceStatus() {
		return resourceStatus;
	}

	public void setResourceStatus(Boolean resourceStatus) {
		this.resourceStatus = resourceStatus;
	}

	public String getKafkaEventTag() {
		return kafkaEventTag;
	}

	public void setKafkaEventTag(String kafkaEventTag) {
		this.kafkaEventTag = kafkaEventTag;
	}

	public HashMap<String, Object> getBigdata() {
		return bigdata;
	}

	public void setBigdata(HashMap<String, Object> bigdata) {
		this.bigdata = bigdata;
	}

	public Integer getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(Integer currentBalance) {
		this.currentBalance = currentBalance;
	}

	public String getOperatorTransactionId() {
		return operatorTransactionId;
	}

	public void setOperatorTransactionId(String operatorTransactionId) {
		this.operatorTransactionId = operatorTransactionId;
	}
}
