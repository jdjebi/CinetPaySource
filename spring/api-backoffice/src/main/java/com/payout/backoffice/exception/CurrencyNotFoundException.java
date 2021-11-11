package com.payout.backoffice.exception;

public class CurrencyNotFoundException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	public CurrencyNotFoundException(Integer id) {
		super("Devise d'id " + id + " introuvable");
	}
}
