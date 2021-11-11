/**
 * @author Jean-Marc Dje Bi
 * @since 12-08-2021
 * @version 1
 */
package com.payout.backoffice.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.payout.backoffice.repository.ServiceRepository;
import com.payout.backoffice.webconf.transfert.entity.ResourceTransfertService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payout.backoffice.dao.Operator;
import com.payout.backoffice.dao.Resource;
import com.payout.backoffice.dao.Service;
import com.payout.backoffice.eventlog.entity.Event;
import com.payout.backoffice.kafka.KafkaService;

/**
 * Classe de la gestion de la mise a jour des services de transfert.
 * Met a jour les resources utilises par le service de transfert
 */
@Component
public class UpdateTransfertServiceConfigScheduler {
	
	/**
	 * @see ServiceRepository
	 */
	@Autowired
	ServiceRepository serviceRepository;
	
	@Autowired 
	KafkaService kafkaService;
	
    Logger logger = LoggerFactory.getLogger(UpdateTransfertServiceConfigScheduler.class);

    /**
     * Met a jour tous les services de transfert associe a un operateur 
     * @throws Exception
     */
	@Scheduled(initialDelay = 10 * 1000, fixedDelay =  60 * 1000)
	public void update() throws Exception {
		
		HashMap<String,String> extrasData = null;
		
		List<Service> services = serviceRepository.findByRoleAndOperatorIsNotNullOrderByPriority("TRANSFERT-SERVICE");
				
		logger.info("Update transfert services(with operator associate only !) starting");
		
		logger.info(services.toString());

		for(Service service: services) {
						
			List<ResourceTransfertService> resourcesToSend = new ArrayList<ResourceTransfertService>();
			
			logger.info("Updating transfert service " + service.getName());
			
			if(service.getActive()) {
				
				RestTemplate restTemplate = new RestTemplate();
				
				Operator operator = service.getOperator();
				
				List<Resource> resources = operator.getResources();
								
				ResourceTransfertService rxTrx = null;
					
				logger.info("Collecting resources of service " + service.getName());

				for(Resource resource: resources) {
					
					rxTrx = new ResourceTransfertService();
					
					if(service.getType().equals("API-SERVICE")) {
						
						if(resource.getType().equals("API") && resource.getApiUrl() != null && !resource.getApiUrl().isEmpty()) {	
							
							// System.out.println(service.getName() + "|" + resource.getName() + "|" + resource.getApiUrl() + "|" +  resource.getOperator().getName());

							rxTrx.setEmail(resource.getEmail());
							rxTrx.setPassword(resource.getPassword());
							rxTrx.setName(resource.getName());
							rxTrx.setLastBalance(resource.getBalance());
							rxTrx.setPhone(resource.getPhone());
							rxTrx.setOperator_api_url(resource.getApiUrl());
							rxTrx.setOperatorCode(operator.getAlias());
							
							if(resource.getExtrasData() != null) {
								
								extrasData = new ObjectMapper().readValue(resource.getExtrasData(), HashMap.class);
								
								logger.info("Extras data for " + operator.getAlias() + " formated :" + extrasData);
								
							}else {
								extrasData = null;
							}
							
							rxTrx.setExtrasData(extrasData);
							
							resourcesToSend.add(rxTrx);
						}
					}else {
						
						logger.warn("Les SIM ne sont pas encore pris en charge");
						
						continue;
					}
													
				}
				
				logger.info("Resources of service " + service.getName() + " collected: " + resourcesToSend.toString());
				
				try {
					
					String serviceUrl = service.getUrl();
										
					restTemplate.put(serviceUrl + "/configs/update/resources",resourcesToSend,List.class);
					
					logger.info("Resources of service " + service.getName() + " sent");
					
					HashMap<String, Object> data = new HashMap<String, Object>();
					
					data.put("resources", resourcesToSend);
					
					kafkaService.pushLogEvent("OPERATOR", "OperatorConfigAddToTransfertService", operator.getAlias(), Event.INFO, data);
					
				} catch(Exception e) {
					
					logger.error("Impossible to send config to service " + service.getName() + " because " + e.getMessage());
					
				}
				
			}else {
				
				logger.warn("Failed to update service " + service.getName() + " because it is desactive");

			}
						
		}
		
	}

}
