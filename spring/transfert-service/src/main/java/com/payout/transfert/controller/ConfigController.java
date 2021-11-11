package com.payout.transfert.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.payout.transfert.adapter.TransactionProcessorAdapter;
import com.payout.transfert.core.eventlog.entity.Event;
import com.payout.transfert.core.kafka.KafkaService;
import com.payout.transfert.core.transfert.entity.ResourceTransfertService;
import com.payout.transfert.dao.Resource;
import com.payout.transfert.repository.ResourceRepository;

@RestController
public class ConfigController {
	
	@Autowired
	ResourceRepository resourceRepository;
	
	@Autowired
	KafkaService kafkaService;
	
    Logger logger = LoggerFactory.getLogger(ConfigController.class);

	@PutMapping("/configs/update/resources")
	public void updateResource(@RequestBody List<ResourceTransfertService> resources) {
				
		Resource rxTmp = null;
				
		for(ResourceTransfertService resource: resources) {
			
			Resource rx = resourceRepository.findByName(resource.getName());
			
			if(rx != null) {
				resourceRepository.delete(rx);
			}
			
			rxTmp = new Resource();	
			rxTmp.setComission(resource.getComission());
			rxTmp.setEmail(resource.getEmail());
			rxTmp.setName(resource.getName());
			rxTmp.setOperator_api_url(resource.getOperator_api_url());
			rxTmp.setOperatorCode(resource.getOperatorCode());
			rxTmp.setPassword(resource.getPassword());
			rxTmp.setLastBalance(resource.getLastBalance());
			rxTmp.setOperator_api_url(resource.getOperator_api_url());
			rxTmp.setOperatorCode(resource.getOperatorCode());
			rxTmp.setExtrasData(resource.getExtrasData());
			
			resourceRepository.save(rxTmp);
			
		}
		
		kafkaService.pushEvent("SERVICE","ResourcesUpdated","API-TRANSFERT-SERVICE",Event.UPDATED);
		
		logger.info("Resources updated: " + resources.toString());
		
	}
}
