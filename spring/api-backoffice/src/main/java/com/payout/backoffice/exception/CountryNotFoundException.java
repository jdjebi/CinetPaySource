package com.payout.backoffice.exception;

public class CountryNotFoundException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	public CountryNotFoundException(Integer id) {
		super("Pays d'id " + id + " introuvable");
	}
}
