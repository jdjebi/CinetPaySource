package com.payout.backoffice.exception;

public class OperatorNotFoundException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	public OperatorNotFoundException(Integer id) {
		super("Opérateur d'id " + id + " introuvable");
	}
}