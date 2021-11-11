package com.payout.backoffice.exception;

public class SimboxNotFoundException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public SimboxNotFoundException(Integer id) {
		super("Simbox d'id " + id + " introuvable");
	}
}
