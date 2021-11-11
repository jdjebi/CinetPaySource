/**
 * 
 * @author Jean-Marc Dje Bi
 * @since 27-07-2021
 * @version 1
 */

package com.payout.psg.transactions;

import java.util.Date;

/**
 * Classe representant le statut de reception d'une transaction.
 *
 */
public class TransactionReceiveStatus {
	
	public static String ACCEPTED = "TRX_ACCEPTED";
	public static String REFUSED = "TRX_REFUSED";
	public static String PROBLEM = "TRX_PROBLEM";

	/**
	 * Identifiant de la transaction
	 */
	private String transactionId;
	
	/**
	 * Statut de la reception: Accepte ou refuse
	 */	
	private String status;
	
	/**
	 * Date d'analyse de la transaction
	 */	
	private Date checkAt = new Date();
	
	/**
	 * Date de derminitation du statut
	 */	
	private Date statusSetAt;
	
	/**
	 * Commentaire sur le statut
	 */	
	private String comment;
	
	public TransactionReceiveStatus(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public void setAccept() {
		this.status = TransactionReceiveStatus.ACCEPTED;
		setStatusSetAt(new Date());
	}
	
	public void setRefuse() {
		this.status = TransactionReceiveStatus.REFUSED;
		setStatusSetAt(new Date());
	}

	public Date getCheckAt() {
		return checkAt;
	}

	public void setCheckAt(Date checkAt) {
		this.checkAt = checkAt;
	}

	public Date getStatusSetAt() {
		return statusSetAt;
	}

	public void setStatusSetAt(Date statusSetAt) {
		this.statusSetAt = statusSetAt;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setProblem() {
		this.status = TransactionReceiveStatus.PROBLEM;
		setStatusSetAt(new Date());
		
	}


}
