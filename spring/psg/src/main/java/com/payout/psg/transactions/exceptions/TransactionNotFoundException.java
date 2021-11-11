package com.payout.psg.transactions.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class TransactionNotFoundException extends RuntimeException{
	
	 public TransactionNotFoundException(String id) {
		 
	        super("Transaction id " + id + " not found");
	        
	 }
}
