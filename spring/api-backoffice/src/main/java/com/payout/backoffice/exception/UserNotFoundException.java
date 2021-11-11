package com.payout.backoffice.exception;

public class UserNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public UserNotFoundException(Integer id) {
		super("Utilisateur d'id " + id + " introuvable");
	}
	
}
