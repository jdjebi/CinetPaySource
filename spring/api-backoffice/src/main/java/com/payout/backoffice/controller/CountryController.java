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

import com.payout.backoffice.dao.Country;
import com.payout.backoffice.dao.Currency;
import com.payout.backoffice.exception.CountryNotFoundException;
import com.payout.backoffice.repository.CountryRepository;
import com.payout.backoffice.repository.CurrencyRepository;

@CrossOrigin
@RestController
public class CountryController {

	@Autowired
	private CountryRepository countryRepository;
	
	@Autowired
	private CurrencyRepository currencyRepository;
	
	@PostMapping("/countries")
	public Country createOperator(@RequestBody Country newCountry) {
		
		Country country = countryRepository.save(newCountry);
		
		Currency currency = currencyRepository.findById(country.getCurrency().getId()).get();
		
		country.setCurrency(currency);
		
		return country;
	}
	
	@GetMapping("/countries")
	public Iterable<Country> getCountry() {
			
		Iterable<Country> countries = countryRepository.findAll();
		
		for(Country country: countries) {
			if(country.getCurrency() == null) {
				country.setCurrency(new Currency());
			}
		}
		
		return countries;
	}
	
	@GetMapping("/countries/{id}")
	public Country getCountry(@PathVariable Integer id) {
		return countryRepository.findById(id).orElseThrow(() -> new CountryNotFoundException(id));
	}
	
	@PutMapping("/countries/{id}")
	public Country updateCountry(@RequestBody Country newCountry, @PathVariable Integer id) {
		return countryRepository.findById(id)
			      .map(country -> {			    	  
			    	  country.setName(newCountry.getName());
			    	  
			    	  country.setCurrency(newCountry.getCurrency());
			    	  
			    	  
			    	  country.setCode(newCountry.getCode());
			        return countryRepository.save(country);
			      })
			      .orElseGet(() -> {
			    	  newCountry.setId(id);
			    	  return countryRepository.save(newCountry);
			      });
	}
	
	@DeleteMapping("/countries/{id}")
	public void deleteCountry(@PathVariable Integer id) {		
		countryRepository.deleteById(id);
	}
}
