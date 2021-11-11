/**
 * @author Jean-Marc Dje Bi
 * @since 16-08-2021
 * @updated 18-08-2021
 * @version 1.0.1
 */
package com.payout.backoffice.dao;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Classe representant une resource
 */
@Entity
@Table(name="resources")
public class Resource {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	private String name = null;
	
	private String type = null;
	
	private String email = null;
	
	private String password = null;
	
	private String token = null;
	
	private Integer slot = null;
	
	private String phone = null;
	
	/**
	 * Url de test de la resource (pour les api de preference)
	 */
	private String pingUrl = null;
	
	/**
	 * Ensemble de donnees supplementaires specifique a l'api
	 */
	@Column(columnDefinition="TEXT")
	private String extrasData = null;
	
	
	private boolean ignoreBalance = false; 
	
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "country_id")
	private Country country = null;
	
	private Integer balance = 0;
	
	private Integer comission = null;
	
	@Column(name="secret_code")
	private String secretCode = null;
	
	@Column(name="syntax_balance")
	private String syntaxBalance = null;
	
	@Column(name="syntax_deposit")
	private String syntaxDeposit = null;
	
	@Column(name="syntax_last_transactions")
	private String syntaxLastTransactions = null;
	
	@Column(name="syntax_commission")
	private String syntaxCommission = null;
	
	private boolean pending = false;
	
	private Boolean status = true;
	
	private boolean active = false;
	
	@Column(name="next_sms")
	private String nextSms = null;
	
	@Column(name="access_token")
	private String accessToken = null;
	
	@Column(name="start_last_balance")
	private Integer startLastBalance = 0;
	
	//@Column(name="api_url")
	private String apiUrl = null;

	@JsonIgnoreProperties("resources")
	@ManyToOne
	@JoinColumn(name="operator_id") 
	private Operator operator;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSlot() {
		return slot;
	}

	public void setSlot(Integer slot) {
		this.slot = slot;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getNextSms() {
		return nextSms;
	}

	public void setNextSms(String nextSms) {
		this.nextSms = nextSms;
	}

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	/*
	public boolean getPending() {
		return pending;
	}

	public void setPending(boolean pending) {
		this.pending = pending;
	}
	*/

	public String getSyntaxBalance() {
		return syntaxBalance;
	}

	public void setSyntaxBalance(String syntaxBalance) {
		this.syntaxBalance = syntaxBalance;
	}

	public String getSyntaxLastTransactions() {
		return syntaxLastTransactions;
	}

	public void setSyntaxLastTransactions(String syntaxLastTransactions) {
		this.syntaxLastTransactions = syntaxLastTransactions;
	}

	public String getSyntaxCommission() {
		return syntaxCommission;
	}

	public void setSyntaxCommission(String syntaxCommission) {
		this.syntaxCommission = syntaxCommission;
	}

	public Integer getComission() {
		return comission;
	}

	public void setComission(Integer comission) {
		this.comission = comission;
	}

	public String getSecretCode() {
		return secretCode;
	}

	public void setSecretCode(String secretCode) {
		this.secretCode = secretCode;
	}

	public Integer getBalance() {
		return balance;
	}

	public void setBalance(Integer balance) {
		this.balance = balance;
	}

	public String getSyntaxDeposit() {
		return syntaxDeposit;
	}

	public void setSyntaxDeposit(String syntaxDeposit) {
		this.syntaxDeposit = syntaxDeposit;
	}
	
	public String toString() {
		return "Resource [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", type=" + type + ", accessToken=" + accessToken + ", token=" + token + "]";
	}

	public Integer getStartLastBalance() {
		return startLastBalance;
	}

	public void setStartLastBalance(Integer startLastBalance) {
		this.startLastBalance = startLastBalance;
	}

	public boolean isPending() {
		return pending;
	}

	public void setPending(boolean pending) {
		this.pending = pending;
	}

	public Operator getOperator() {
		return operator;
	}

	
	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public String getApiUrl() {
		return apiUrl;
	}

	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	public String getPingUrl() {
		return pingUrl;
	}

	public void setPingUrl(String pingUrl) {
		this.pingUrl = pingUrl;
	}

	public String getExtrasData() {
		return extrasData;
	}

	public void setExtrasData(String extrasData) {
		this.extrasData = extrasData;
	}

	public boolean getIgnoreBalance() {
		return ignoreBalance;
	}

	public void setIgnoreBalance(boolean ignoreBalance) {
		this.ignoreBalance = ignoreBalance;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}
	
}
