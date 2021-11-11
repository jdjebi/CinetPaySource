package com.payout.backoffice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.payout.backoffice.dao.Currency;
import com.payout.backoffice.exception.CurrencyNotFoundException;
import com.payout.backoffice.repository.CurrencyRepository;

@CrossOrigin
@RestController
public class CurrencyController {

	@Autowired
	private CurrencyRepository currencyRepository;
	
	@PostMapping("/currencies")
	public Currency createOperator(@RequestBody Currency currency) {
		return currencyRepository.save(currency);
	}
	
	@GetMapping("/currencies")
	public Iterable<Currency> getCurrency() {
		return currencyRepository.findAll();
	}
	
	@GetMapping("/currencies/{id}")
	public Currency getCurrency(@PathVariable Integer id) {
		return currencyRepository.findById(id).orElseThrow(() -> new CurrencyNotFoundException(id));
	}
	
	@PutMapping("/currencies/{id}")
	public Currency updateCountry(@RequestBody Currency newCurrency, @PathVariable Integer id) {
		return currencyRepository.findById(id)
			      .map(currency -> {			    	  
			    	  currency.setName(newCurrency.getName());
			        return currencyRepository.save(newCurrency);
			      })
			      .orElseGet(() -> {
			    	  newCurrency.setId(id);
			    	  return currencyRepository.save(newCurrency);
			      });
	}
	
	@DeleteMapping("/currencies/{id}")
	public void deleteCountry(@PathVariable Integer id) {		
		currencyRepository.deleteById(id);
	}
}
