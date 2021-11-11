package com.payout.backoffice.controller;

import java.util.HashMap;
import java.util.Optional;

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
import com.payout.backoffice.dao.Operator;
import com.payout.backoffice.dao.Service;
import com.payout.backoffice.eventlog.entity.Event;
import com.payout.backoffice.exception.OperatorNotFoundException;
import com.payout.backoffice.repository.OperatorRepository;
import com.payout.backoffice.repository.ServiceRepository;
import com.payout.backoffice.kafka.KafkaService;

@CrossOrigin
@RestController
public class OperatorController {

	@Autowired
	private OperatorRepository operatorRepository;
	
	@Autowired
	private ServiceRepository serviceRepository;
	
	@Autowired
	private KafkaService kafkaService;
	
	@PostMapping("/operators")
	public Operator createOperator(@RequestBody Operator operator) {			
		return operatorRepository.save(operator);
	}
	
	@GetMapping("/operators")
	public Iterable<Operator> getOperators() {
		Iterable<Operator> operators = operatorRepository.findAll();
		for(Operator operator: operators) {
			operator.buildForApi();
		}
		return operatorRepository.findAll();
	}
	
	@GetMapping("/operators/{id}")
	public Operator getOperator(@PathVariable Integer id) {
		Operator operator = operatorRepository.findById(id).orElseThrow(() -> new OperatorNotFoundException(id));
		operator.buildForApi();
		return operator;
	}
	
	@PutMapping("/operators/{id}")
	public Operator updateOperators(@RequestBody Operator newOperator, @PathVariable Integer id) {
		return operatorRepository.findById(id)
			      .map(operator -> {			
			    	  	
			    	  	System.out.println(newOperator.getCountry());
			    	  	
					  	if(newOperator.getCountry() == null) {
					  		operator.setCountry(new Country());
					  	}else {
					  		operator.setCountry(newOperator.getCountry());
					  	}
				  
					  	operator.setName(newOperator.getName());
					  	operator.setAlias(newOperator.getAlias());
					  	operator.setUseApi(newOperator.getUseApi());
					  	operator.setUseSIM(newOperator.getUseSIM());
					  	operator.setLogo(newOperator.getLogo());
			    	  	
			    		return operatorRepository.save(operator);
			      })
			      .orElseGet(() -> {
			    	  newOperator.setId(id);
			    	  return operatorRepository.save(newOperator);
			      });
	}
	
	@DeleteMapping("/operators/{id}")
	public void deleteOperator(@PathVariable Integer id) {		
		Operator operator = operatorRepository.findById(id).orElseThrow();
		
		Service api = operator.getApiService();
		Service sim = operator.getSimService();
		
		if(api != null) {
			api.setOperator(null);
			kafkaService.pushEvent("SERVICE","ServiceOperatorDissociated", api.getId().toString(),Event.INFO);
		}
		
		if(sim != null) {
			sim.setOperator(null);
			kafkaService.pushEvent("SERVICE","ServiceOperatorDissociated", sim.getId().toString(),Event.INFO);
		}
		
		kafkaService.pushEvent("OPERATOR","OperatorDeleted", operator.getId().toString(),Event.INFO);
		
		operatorRepository.delete(operator);				
	}
	
	@PostMapping("/operators/addservice")
	public Operator addServiceOperator(@RequestBody HashMap<String,Integer> data) throws Exception {	
		
		Integer serviceId = data.get("serviceId");
		Integer operatorId = data.get("operatorId");
		
		Optional<Service> serviceOpt = serviceRepository.findById(serviceId);
		Optional<Operator> operatorOpt = operatorRepository.findById(operatorId);

		if(serviceOpt.isPresent() && operatorOpt.isPresent()) {
			
			Service service = serviceOpt.get();
			Operator operator = operatorOpt.get();
		
			kafkaService.pushEvent("OPERATOR","OperatorNewServiceAdded", operator.getId().toString(),Event.UPDATE);
			kafkaService.pushEvent("SERVICE","ServiceNewOperatorAdded", service.getId().toString(),Event.UPDATE);
						
			if(service.getType().equals("API-SERVICE")) {
				service.setOperator(operator);
				operator.setApiService(service);
			}else if(service.getType().equals("SIM-SERVICE")){
				service.setOperator(operator);
				operator.setSimService(service);
			}else {
				throw new Exception("Le type du service n'est pas pris en charge");
			}
						
			operatorRepository.save(operator);
			
			return operator;
			
		}else {
			throw new Exception("Operateur ou Service introuvable");
		}
		
		
		// operatorRepository.deleteById(id);
	}
	
	@PostMapping("/operators/removeservice")
	public Operator removeServiceOperator(@RequestBody HashMap<String,Integer> data) throws Exception {	
		
		Integer serviceId = data.get("serviceId");
		Integer operatorId = data.get("operatorId");
		
		Optional<Service> serviceOpt = serviceRepository.findById(serviceId);
		Optional<Operator> operatorOpt = operatorRepository.findById(operatorId);

		if(serviceOpt.isPresent() && operatorOpt.isPresent()) {
			
			Service service = serviceOpt.get();
			Operator operator = operatorOpt.get();
		
			kafkaService.pushEvent("OPERATOR","OperatorServiceRemoved", operator.getId().toString(),Event.UPDATE);
			kafkaService.pushEvent("SERVICE","ServiceOperatorRemoved", service.getId().toString(),Event.UPDATE);
						
			if(service.getType().equals("API-SERVICE")) {	
				service.setOperator(null);
				operator.setApiService(null);
			}else if(service.getType().equals("SIM-SERVICE")){
				service.setOperator(null);
				operator.setSimService(null);
			}else {
				throw new Exception("Le type du service n'est pas pris en charge");
			}
						
			operatorRepository.save(operator);
			
			return operator;
			
		}else {
			throw new Exception("Operateur ou Service introuvable");
		}
		
		
		// operatorRepository.deleteById(id);
	}
}
