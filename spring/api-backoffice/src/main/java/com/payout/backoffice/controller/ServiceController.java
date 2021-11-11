package com.payout.backoffice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payout.backoffice.dao.Operator;
import com.payout.backoffice.dao.Service;
import com.payout.backoffice.eventlog.entity.Event;
import com.payout.backoffice.exception.ServiceNotFoundException;
import com.payout.backoffice.kafka.KafkaService;
import com.payout.backoffice.repository.ServiceRepository;

@CrossOrigin
@RestController
public class ServiceController {

	@Autowired
	private ServiceRepository serviceRepository;
	
	@Autowired
	private KafkaService kafkaService;
	
	@PostMapping("/services")
	public Service createService(@RequestBody Service service) {	
		
		service.setRole("TRANSFERT-SERVICE");
		service.setCode("PSP");
		service.setPriority(11);
		service.setActive(true);
		
		return serviceRepository.save(service);
	}
	
	@GetMapping("/services")
	public Iterable<Service> getService(@RequestParam(required=false) String type, @RequestParam(required=false, name="with_no_operator") boolean withNoOperator) {	
		
		if(type == null) {
			return serviceRepository.findAll(Sort.by(Sort.Direction.ASC, "priority"));

		}else {
			
			if(withNoOperator == true) {
				
				return serviceRepository.findByTypeAndOperatorIsNullOrderByPriority(type);
				
			}else {
				return serviceRepository.findByTypeOrderByPriority(type);
			}
		}
					
	}
	
	@GetMapping("/services/{id}")
	public Service getSimbox(@PathVariable Integer id) {
		Service service = serviceRepository.findById(id).orElseThrow(() -> new ServiceNotFoundException(id));
		return service;
	}
	
	@DeleteMapping("/services/{id}")
	public void deleteService(@PathVariable Integer id) throws Exception {	
		Service service = serviceRepository.getById(id);
		
		if(service == null) {
			throw new Exception("Service d'id " + id.toString() + " inconnue");
		}
		
		Operator operator = service.getOperator();
		
		if(operator != null) {
			if(service.getType().equals("API-SERVICE")) {
				operator.setApiService(null);
			}else if(service.getType().equals("SIM-SERVICE")) {
				operator.setSimService(null);
			}else {
				throw new Exception("Le type du service n'est pas pris en charge");
			}
			
			kafkaService.pushEvent("OPERATOR","OperatorServiceDissociated", operator.getId().toString(),Event.INFO);
		}
		
		serviceRepository.delete(service);
	}
	
	@PutMapping("/services/{id}")
	public Service updateOperators(@RequestBody Service newService, @PathVariable Integer id) {
						
		Service serviceTmp = serviceRepository.findById(id)
			      .map(service -> {						    	  	
			    	  	service.setName(newService.getName());
			    	  	service.setMinName(newService.getMinName());
			    	  	service.setLongName(newService.getLongName());
			    	  	service.setCode(newService.getCode());
			    	  	service.setActive(newService.getActive());
			    	  	service.setDescription(newService.getDescription());
			    	  	service.setOperator(newService.getOperator());
			    	  	service.setPriority(newService.getPriority());
			    	  	service.setRole(newService.getRole());
			    	  	service.setStatus(newService.getStatus());
			    	  	service.setType(newService.getType());
			    	  	service.setUrl(newService.getUrl());
			    		return serviceRepository.save(service);
			      })
			      .orElseGet(() -> {
			    	  newService.setId(id);
			    	  return newService;
			      });
		
		kafkaService.pushEvent("SERVICE","ServiceUpdated",id.toString(), Event.EVENT_TYPE, Event.INFO);
		
		return serviceTmp;
	}
	
	@PostMapping("/services/new/gateway")
	public Service newGatewayService(@RequestBody Service service) {
		
		List<Service> services = serviceRepository.findByRole("GATEWAY");
		
		serviceRepository.deleteAll(services);
		
		service.setType("SYSTEM");
		service.setRole("GATEWAY");
		service.setStatus(true);
		service.setPriority(1);
		service.setRole("TRANSFERT-SERVICE");
		return serviceRepository.save(service);		
	}
	
	@GetMapping("/services/get/gateway")
	public Service getGatewayService() {		
		List<Service> services = serviceRepository.findByRole("GATEWAY");
				
		if(services.isEmpty()) {
			return null;
		}else {
			return services.get(services.size() - 1);
		}		
	}
	
	@DeleteMapping("/services/delete/gateway")
	public void deleteGatewayService() {	
		
		List<Service> services = serviceRepository.findByRole("DISPATCHER");
		
		for(int i = 0; i < services.size(); i++) {
			serviceRepository.delete(services.get(i));
		}
			
	}
	
	
	
	
}
