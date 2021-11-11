/**
 * @author Jean-Marc
 * @since 12-08-2021
 * @version 1
 */

package com.payout.backoffice.webconf.transfert.entity;

import java.util.HashMap;

/**
 * Classe representant les donnees de configuration d'une resource pour un service de transfert
 */
public class ResourceTransfertService{
	
	/**
	 * Nom de la ressource
	 */
	private String name;
	
	/**
	 * Numero de telephone
	 */
	private String phone;
	
	/**
	 * Code de l'operateur
	 */
	private String operatorCode;
	
	/**
	 * Dernier solde
	 */
	private Integer lastBalance;
	
	/**
	 * Comission
	 */
	private Integer comission;
	
	
	/**
	 * Url de l'api de l'operateur
	 */
	private String operator_api_url;
	
	/**
	 * Adresse E-mail pour utiliser a la ressource
	 */
	private String email;
	
	/**
	 * Mot de passe pour utiliser la ressource
	 */
	private String password;
		
	/**
	 * Donnees supplementaires specifiques a la resource
	 */
	private HashMap<String,String> extrasData;

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
	
	public String toString() {
		return String.format("%s@%s(%s)",this.name,this.operatorCode,this.lastBalance);
	}

	public HashMap<String,String> getExtrasData() {
		return extrasData;
	}

	public void setExtrasData(HashMap<String,String> extrasData) {
		this.extrasData = extrasData;
	}

}
