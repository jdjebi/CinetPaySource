/**
 * @author Jean-Marc Dje Bi
 * @since 16-08-2021
 * @version 1
 */
package com.payout.backoffice.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Classe representant une simbox
 */
@Entity
public class Simbox {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	private String name;
	
	@Column(columnDefinition="TEXT")
	private String description;
	
	@Column(name="total_port")
	private Integer totalPort;
	
	@Column(name="serial_number")
	private String serialNumber;
	
	@Column(name="internet_ip")
	private String internetIp;
	
	@Column(name="local_ip")
	private String localIp;
	
	private String url;
	
	private String password;
	
	private boolean active = false;
	
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getTotalPort() {
		return totalPort;
	}

	public void setTotalPort(Integer totalPort) {
		this.totalPort = totalPort;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getInternetIp() {
		return internetIp;
	}

	public void setInternetIp(String internetIp) {
		this.internetIp = internetIp;
	}

	public String getLocalIp() {
		return localIp;
	}

	public void setLocalIp(String localIp) {
		this.localIp = localIp;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
}
