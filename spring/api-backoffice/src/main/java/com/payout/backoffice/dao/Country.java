/**
 * @author Jean-Marc Dje Bi
 * @since 16-08-2021
 * @version 1
 */
package com.payout.backoffice.dao;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;


/**
 * Classe representant un pays
 */
@Entity
@Table(name="countries")
public class Country {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	private String name = null;
	
	private String code = null;
		
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "currency_id")
	private Currency currency = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	
	public String toString() {
		return "Country [id=" + id + ", code=" + code + "]";
	}
	
}
