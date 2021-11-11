package com.payout.psg.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.payout.psg.eventlog.entity.Event;
import com.payout.psg.kafka.KafkaService;
import com.payout.psg.model.Operator;
import com.payout.psg.repository.OperatorRepository;
import com.payout.psg.transactions.TransactionService;

@CrossOrigin
@RestController
public class ConfigController {
	
	@Value(value="${info.app.code}")
	String serviceCode;
	
	@Autowired OperatorRepository operatorRepository;
	
	@Autowired KafkaService kafkaService;
	
	Logger logger = LoggerFactory.getLogger(TransactionService.class);
		
	@PutMapping("/configs/update/operators")
	public void updateOperators(@RequestBody List<Operator> operators) {
			
		operatorRepository.deleteAll();
		
		operatorRepository.saveAll(operators);

		// logging
		
		logger.info("Saved operators: " + operators.toString());
		
		Event event = new Event("SERVICE","ServiceConfigsOperatorsUpdated",serviceCode,Event.EVENT_TYPE,Event.INFO);
		
		HashMap<String, Object> data = new HashMap<String, Object>();
		
		data.put("operators", operators.toString());
		
		event.getEventData().setMap(data);
		
		kafkaService.sendEvent(event);
		
	}
	
	@GetMapping("/configs")
	public Map<String,Object> getConfigs() {
				
		Iterable<Operator> operators = operatorRepository.findAll();
	
		Map<String,Object> configs = new HashMap<String,Object>();
	
		configs.put("operators",operators);
		
		return configs;
	}
	
}
