/**
 * @author Jean-Marc
 * @since 12-08-2021
 * @version 1
 */
package com.payout.backoffice.scheduler.service.transfert;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.payout.backoffice.dao.Service;
import com.payout.backoffice.eventlog.entity.Event;
import com.payout.backoffice.kafka.KafkaService;
import com.payout.backoffice.repository.ServiceRepository;
import com.payout.backoffice.webconf.transfert.UpdateTransactionService;
import com.payout.backoffice.webconf.transfert.entity.ServiceInfoWrapper;

/**
 * Classe de gestion des la synchronisation des services de transfert.
 * Synchronise le statut, l'activite et le topic kafka des services de transfert
 * 
 */
@Component
public class ServiceCheckerScheduler {
	
	/**
	 * @see ServiceRepository
	 */
	@Autowired
	ServiceRepository serviceRepository;
	
	/**
	 * 	@see KafkaService
	 */
	@Autowired
	KafkaService kafkaService;
	
	/**
	 * @see UpdateTransactionService
	 */
	@Autowired
	UpdateTransactionService updateTransactionService;
	
	Logger logger = LoggerFactory.getLogger(ServiceCheckerScheduler.class);

	/**
	 * Synchronisation des donnees entre les services de transferts et l'api du backoffice
	 */
	@Scheduled(fixedDelay =   10 * 1000)
	public void checkTransfertService() {
		
		List<Service> services = serviceRepository.findByRole("TRANSFERT-SERVICE");
				
		kafkaService.pushEvent("SYSTEM", "TransfertServiceLocalConfigsStrating", "SYSTEM", Event.EVENT_TYPE, Event.WARNING);
		
		logger.info("Synchronize transfert services starting");

		for(Service service: services) {
			
			kafkaService.pushLogEvent("SERVICE", "TransfertServiceLocalConfigUpdating", service.getId().toString(), Event.INFO);
										
			service.setActive(true);
			
			logger.info("Synchronizing service: " + service.getName());

			if(service.getUrl() != null && !service.getUrl().isBlank()) {
				
				RestTemplate restTemplate = new RestTemplate();
				ServiceInfoWrapper response = null;
				String serviceUrl = service.getUrl();
				String serviceGetInfoUrl = serviceUrl + "/actuator/info";
				
				kafkaService.pushLogEvent("SERVICE", "TransfertServiceLocalConfigUpdating", service.getId().toString(), Event.INFO);
							
				try {
					
					logger.info("Get service info at " + serviceGetInfoUrl);
					
					response = restTemplate.getForObject(serviceGetInfoUrl, ServiceInfoWrapper.class);	
					
					updateTransactionService.updateFromServiceInfoWrapper(service, response);
					
					service.setStatus(true);
					
					serviceRepository.save(service);
					
					kafkaService.pushLogEvent("SERVICE", "TransfertServiceLocalConfigUpdated", service.getId().toString(), Event.INFO);
										
					logger.info("Service " + service.getName() + " synchronization done !");
					
				} catch(ResourceAccessException e) {
										
					kafkaService.pushLogEvent("SERVICE", "TransfertServiceUrlNotWorking", service.getName(), Event.ERROR);
					
					logger.error("Impossible to get service " + service.getName() + "infos from url: " + service.getUrl());

					service.setStatus(false);
					
					serviceRepository.save(service);
					
				} catch(Exception e) {
					
					logger.error("Error during service " + service.getName() + " info getting. Detail: " + e.getMessage());
					
					service.setStatus(false);
					
					e.printStackTrace();
					
					throw e;
				}
				
									
			}else {
								
				logger.error("Failed to synchronize service " + service.getName() + " besause its url doesn't exist");
					
				kafkaService.pushEvent("SERVICE", "TransfertServiceLocalConfigUpdateFailed", service.getId().toString(), Event.EVENT_TYPE, Event.ERROR);

			}
			
			kafkaService.pushEvent("SERVICE", "TransfertServiceLocalConfigFinished", service.getId().toString(), Event.EVENT_TYPE, Event.INFO);

		}
				
		
	}
}
