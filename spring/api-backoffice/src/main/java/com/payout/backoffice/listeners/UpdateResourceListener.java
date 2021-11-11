package com.payout.backoffice.listeners;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.payout.backoffice.dao.Resource;
import com.payout.backoffice.eventlog.entity.Event;
import com.payout.backoffice.helpers.HashHelper;
import com.payout.backoffice.kafka.KafkaService;
import com.payout.backoffice.repository.ResourceRepository;

@Component
public class UpdateResourceListener {
	
	@Autowired
	KafkaService kafkaService;
	
	@Autowired
	ResourceRepository resourceRepository;
	
	@KafkaListener(topics = "backoffice_update_resources")
	public void updateFromNotification(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) throws JsonMappingException, JsonProcessingException {
		
		HashMap<String, Object> resource =  (HashMap<String, Object>) HashHelper.getHashMap(message);
		
		String resourceName = (String) resource.get("resourceName");
		
		Boolean resourceStatus = (Boolean) resource.get("status");
		
		Integer resourceBalance = (Integer) resource.get("currentBalance");
		
		Resource rx = resourceRepository.findByName(resourceName);
		
		if(rx.getStatus() != resourceStatus) {
			rx.setStatus(resourceStatus);
		}
		
		rx.setBalance(resourceBalance);
		
		resourceRepository.save(rx);
		
		kafkaService.pushEvent("OPERATOR", "OperatorResourceUpdate", rx.getOperator().getAlias(), Event.INFO, resource);
		
	}
	
}
