package com.payout.backoffice.exception;

public class ServiceNotFoundException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	public ServiceNotFoundException(Integer id) {
		super("Service d'id " + id + " introuvable");
	}
}
