/**
 * @author Jean-Marc Dje Bi
 * @since 11-08-2021
 * @version 1
 */
package com.payout.backoffice.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.payout.backoffice.dao.Service;
import com.payout.backoffice.eventlog.entity.Event;
import com.payout.backoffice.kafka.KafkaService;
import com.payout.backoffice.repository.OperatorRepository;
import com.payout.backoffice.repository.ServiceRepository;
import com.payout.backoffice.services.dispatcher.DispatcherConfigsSender;

/**
 * Met a jour les configurations du dispatcher
 */
@Component
public class DispatcherConfigScheduler {
	
	/**
	 * @see OperatorRepository
	 */
	@Autowired
	private ServiceRepository serviceRepository;
	
	/**
	 * @see OperatorRepository
	 */
	@Autowired
	OperatorRepository operatorRepository;
	
	/**
	 * @see KafkaService
	 */
	@Autowired 
	private KafkaService kafkaService;
	
	/**
	 * @see DispatcherConfigsSender
	 */
	@Autowired
	private DispatcherConfigsSender dispatcherConfigsSender;
	
	Logger logger = LoggerFactory.getLogger(DispatcherConfigScheduler.class);

	/**
	 * Met a jour le dispatcher s'il existe
	 * @throws Exception
	 */
	@Scheduled(initialDelay = 10 * 1000,fixedDelay =   60 * 1000)
	public void run() throws Exception {
				
		kafkaService.pushEvent("SYSTEM","DispatcherConfigsUpdateStarting","DISPATCHER", Event.EVENT_TYPE, Event.INFO);
		
		logger.info("Dispatcher update starting configs");
		
		Service dispatcher = serviceRepository.findByCode("PDS");
		
		if(dispatcher == null) {
			
			kafkaService.pushEvent("SYSTEM","DispatcherServiceNotFound","DISPATCHER", Event.EVENT_TYPE, Event.WARNING);
			
			kafkaService.pushEvent("SYSTEM","DispatcherConfigsUpdateFailed","DISPATCHER", Event.EVENT_TYPE, Event.ERROR);
			
			logger.error("Dispatcher updating failed because dispatcher service not found in database. Please create dispatcher service manually or wait system dispatcher auto creating");
			
			throw new Exception("Dispatcher service not exist");
			
		}
				
		dispatcherConfigsSender.send(dispatcher);
		
		kafkaService.pushEvent("SYSTEM","DispatcherConfigsUpdateFinished","DISPATCHER", Event.EVENT_TYPE, Event.INFO);

	}
}
