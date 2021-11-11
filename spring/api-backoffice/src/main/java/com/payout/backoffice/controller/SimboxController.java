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

import com.payout.backoffice.dao.Simbox;
import com.payout.backoffice.exception.SimboxNotFoundException;
import com.payout.backoffice.repository.SimboxRepository;

@CrossOrigin
@RestController
public class SimboxController {
	
	@Autowired
	private SimboxRepository simboxRepository;

	@PostMapping("/simbox")
	public Simbox createSimbox(@RequestBody Simbox simbox) {		
		return simboxRepository.save(simbox);
	}
	
	@GetMapping("/simbox")
	public Iterable<Simbox> getSimboxes() {		
		return simboxRepository.findAll();
	}
	
	@GetMapping("/simbox/{id}")
	public Simbox getSimbox(@PathVariable Integer id) {
		Simbox simbox = simboxRepository.findById(id).orElseThrow(() -> new SimboxNotFoundException(id));
		return simbox;
	}
	
	@PutMapping("/simbox/{id}")
	public Simbox updateSimbox(@PathVariable Integer id, @RequestBody Simbox newSimbox) {
		return simboxRepository.findById(id)
			      .map(simbox -> {					    	  				 	  	
			    	  	simbox.setName(newSimbox.getName());
			    	  	simbox.setDescription(newSimbox.getDescription());
			    	  	simbox.setLocalIp(newSimbox.getLocalIp());
			    	  	simbox.setPassword(newSimbox.getPassword());
			    	  	simbox.setInternetIp(newSimbox.getInternetIp());
			    	  	simbox.setSerialNumber(newSimbox.getSerialNumber());
			    	  	simbox.setTotalPort(newSimbox.getTotalPort());
			    	  	simbox.setUrl(newSimbox.getUrl());
			    	  	simbox.setActive(newSimbox.getActive());
			    		return simboxRepository.save(simbox);
			      })
			      .orElseGet(() -> {
			    	  newSimbox.setId(id);
			    	  return newSimbox;
			      });
	}
	
	@DeleteMapping("/simbox/{id}")
	public Simbox deleteSimbox(@PathVariable Integer id) {
		Simbox simbox = simboxRepository.findById(id).orElseThrow(() -> new SimboxNotFoundException(id));
		return simbox;
	}
}
