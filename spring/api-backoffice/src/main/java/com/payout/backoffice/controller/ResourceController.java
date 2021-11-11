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

import com.payout.backoffice.dao.Operator;
import com.payout.backoffice.dao.Resource;
import com.payout.backoffice.eventlog.entity.Event;
import com.payout.backoffice.kafka.KafkaService;
import com.payout.backoffice.repository.OperatorRepository;
import com.payout.backoffice.repository.ResourceRepository;

@CrossOrigin
@RestController
public class ResourceController {
	
	@Autowired
	private ResourceRepository resourceRepository;
	
	@Autowired
	private OperatorRepository operatorRepository;
	
	@Autowired
	private KafkaService kafkaService;
	
	@PostMapping("/resources")
	public Resource createResource(@RequestBody Resource resource) {			
		Operator op = operatorRepository.findById(resource.getOperator().getId()).get();
		resource.setOperator(op);
		resource.setCountry(op.getCountry());
		
		Resource newResource = resourceRepository.save(resource);
		
		Event event = new Event("RESOURCE","ResourceAPICreated",newResource.getId().toString(),Event.EVENT_TYPE,Event.INFO);
		kafkaService.sendEvent(event);
		
		return resourceRepository.save(resource);
	}
	
	@GetMapping("/resources")
	public Iterable<Resource> getResources() {		
		return resourceRepository.findAll();
	}
	
	@PutMapping("/resources/{id}")
	public Resource updateResource(@RequestBody Resource newResource, @PathVariable Integer id) {
		return resourceRepository.findById(id)
			      .map(resource -> {	
			    	  
			    	  	resource.setName(newResource.getName());
			    	  	resource.setAccessToken(newResource.getAccessToken());
			    	  	resource.setActive(newResource.getActive());
			    	  	resource.setBalance(newResource.getBalance());
			    	  	resource.setComission(newResource.getComission());
			    	  	resource.setCountry(newResource.getCountry());
			    	  	resource.setEmail(newResource.getEmail());
			    	  	resource.setPassword(newResource.getPassword());
			    	  	//resource.setOperator(newResource.getOperator());
			    	  	resource.setType(newResource.getType());
			    	  	resource.setToken(newResource.getToken());
			    	  	resource.setStartLastBalance(newResource.getStartLastBalance());
			    	  	resource.setBalance(newResource.getBalance());
			    	  	resource.setEmail(newResource.getEmail());
			    	  	resource.setSyntaxBalance(newResource.getSyntaxBalance());
			    	  	resource.setSyntaxCommission(newResource.getSyntaxCommission());
			    	  	resource.setSyntaxDeposit(newResource.getSyntaxDeposit());
			    	  	resource.setSecretCode(newResource.getSecretCode());
			    	  	resource.setPhone(newResource.getPhone());
			    	  	resource.setApiUrl(newResource.getApiUrl());
			    	  	resource.setPingUrl(newResource.getPingUrl());
			    	  	resource.setExtrasData(newResource.getExtrasData());
			    	  	resource.setIgnoreBalance(newResource.getIgnoreBalance());
			    	  	
			    		return resourceRepository.save(resource);
			      })
			      .orElseGet(() -> {
			    	  newResource.setId(id);
			    	  return newResource;
			      });
	}
	
	@DeleteMapping("/resources/{id}")
	public void deleteResource(@PathVariable Integer id) {	
				
		Resource resource = resourceRepository.findById(id).orElseThrow();
		
		resource.getOperator().getResources().remove(resource);
		
		resourceRepository.delete(resource);
	}
	
}
