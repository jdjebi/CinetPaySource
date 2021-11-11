package com.payout.dispatcher.controller;

import com.payout.dispatcher.eventlog.entity.Event;
import com.payout.dispatcher.kafka.KafkaService;
import com.payout.dispatcher.operators.Operator;
import com.payout.dispatcher.operators.OperatorRepository;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ConfigController{

    @Autowired
    KafkaService kafkaService;

    @Autowired
    OperatorRepository operator2Repository;

    @Value(value="${info.app.role}")
    String serviceRole;
    
	Logger logger = LoggerFactory.getLogger(ConfigController.class);


    @PutMapping("/configs/update/operators")
    public void updateOperators(@RequestBody List<Operator> operators){
    	
    	Event event = null;
    	
        event = new Event("SERVICE","ServiceConfigsOperatorsUpdating",serviceRole, Event.LOG_TYPE,Event.INFO);
        kafkaService.sendEvent(event);
    	
    	operator2Repository.deleteAll();
    	    	
    	for(Operator operator: operators) {
    		Operator newOperator = operator2Repository.save(operator);
            event = new Event("OPERATOR","OperatorUpdated",newOperator.getId(), Event.LOG_TYPE,Event.INFO);
            kafkaService.sendEvent(event);
    	}

        event = new Event("SERVICE","ServiceConfigsOperatorsUpdated",serviceRole, Event.EVENT_TYPE,Event.UPDATED);
        kafkaService.sendEvent(event);
        
    	logger.info("operators config to saved: " + operators.toString());
        
    }

}