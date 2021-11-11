/**
 * @author Jean-Marc Dje Bi
 * @since 16-08-2021
 * @version 1.0.1
 */

package com.payout.psg.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="operators")
public class Operator {
	

	@Id
	private String id;
	
	private String alias;
	
	private String ressourcesCode;
		
	private Date createdAt = new Date();

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
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
}
