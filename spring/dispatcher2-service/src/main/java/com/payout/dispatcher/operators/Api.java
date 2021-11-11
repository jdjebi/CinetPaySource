package com.payout.dispatcher.operators;

/**
 * @author Jean-Marc Dje Bi
 * @version 1.0.1
 * @since 19-08-2021
 */


/**
* Classe representant une ressource d'API
*/
public class Api {

	private Integer id;
	
	private String name;
	
	private String type;
	
	private String email;
	
	private String password;
	
	private String token;
	
	private Integer balance;
	
	private Integer comission;
	
	private boolean active;
	
	private String phone;
	
	private boolean ignoreBalance;

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getBalance() {
		return balance;
	}

	public void setBalance(Integer balance) {
		this.balance = balance;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Integer getComission() {
		return comission;
	}

	public void setComission(Integer comission) {
		this.comission = comission;
	}

	public boolean isIgnoreBalance() {
		return ignoreBalance;
	}

	public void setIgnoreBalance(boolean ignoreBalance) {
		this.ignoreBalance = ignoreBalance;
	}
}
