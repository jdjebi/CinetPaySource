/**
 * @author Jean-Marc Dje Bi
 * @since 10-08-2021
 * @version 1
 */

package com.payout.backoffice.scheduler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.payout.backoffice.dao.Service;
import com.payout.backoffice.eventlog.entity.Event;
import com.payout.backoffice.kafka.KafkaService;
import com.payout.backoffice.repository.OperatorRepository;
import com.payout.backoffice.repository.ServiceRepository;
import com.payout.backoffice.services.dispatcher.DispatcherConfigsSender;
import com.payout.backoffice.supervisor.service.system.SystemServiceChecker;
import com.payout.backoffice.webconf.gateway.GatewayConfigsSender;

/**
*
* Classe de gestion des mises a jour du systeme
*
* @author  Jean-Marc Dje Bi
* @version 1
* @since   14-07-2021
*
*/

@Component
public class UpdateSystemSchedule {
	
	@Autowired
	private ServiceRepository serviceRepository;
	
	@Autowired
	OperatorRepository operatorRepository;
	
	@Autowired 
	private KafkaService kafkaService;
	
	@Autowired 
	private SystemServiceChecker systemServiceChecker;
	
	@Autowired
	private DispatcherConfigsSender dispatcherConfigsSender;
	
	@Value(value="${GATEWAY_URL}")
	String gatewayURL;
	
	@Value(value="${EVENTLOG_URL}")
	String eventlogURL;
	
	@Value(value="${DISPATCHER_URL}")
	String dispatcherURL;
	
	@Value(value="${NOTIFICATION_URL}")
	String notificationURL;
	

	/** Retourne une HashMap de l'url des services systeme du systeme
	 * @return HashMap de l'url des services systeme
	*/
	public HashMap<String,String> getSystemServicesURL() {
		HashMap<String,String> systemServicesURL = new HashMap<String,String>();
		systemServicesURL.put("GATEWAY", gatewayURL);
		systemServicesURL.put("EVENTLOG", eventlogURL);
		systemServicesURL.put("DISPATCHER", dispatcherURL);
		systemServicesURL.put("NOTIFICATION", notificationURL);
		return systemServicesURL;
	}
	
	/** Met a jour les services systeme
	*/
	@Scheduled( initialDelay = 10 * 1000, fixedDelay =   60 * 1000)
	public void updateServices() {
		
		Event eventTmp = null;
		Event event = new Event("SYSTEM","ServicesConfigsUpdateStarted","GATEWAY", Event.EVENT_TYPE, Event.INFO);
		kafkaService.sendEvent(event);
		
		List<Service> services = serviceRepository.findByTypeOrderByPriority("SYSTEM");
		
		for(Service service: services) {
						
			if(service.getRole().equals("GATEWAY")) {
				
				kafkaService.pushLogEvent("SYSTEM","ServicesConfigsUpdateStarting","GATEWAY",  Event.INFO);
				
				GatewayConfigsSender gatewayConfigsSender = new GatewayConfigsSender(operatorRepository);	
				
				try {
					gatewayConfigsSender.send(service);
					event = new Event("SYSTEM","ServiceConfigsUpdated","GATEWAY", Event.EVENT_TYPE, Event.INFO);
					kafkaService.sendEvent(event);
					service.setStatus(true);
					serviceRepository.save(service);
				} catch (Exception e) {
					System.out.println("Gateway error : " + e.getMessage());
					eventTmp = new Event("SYSTEM","ServiceConfigsUpdateFailed","GATEWAY", Event.EVENT_TYPE, Event.ERROR);
					kafkaService.sendEvent(eventTmp);
					e.printStackTrace();
					service.setStatus(false);
					serviceRepository.save(service);
					kafkaService.pushLogEvent("SYSTEM","ServiceStatusSetToDown","GATEWAY", Event.ERROR);
				}
				
			}else if(service.getRole().equals("DISPATCHER2")) {
				kafkaService.pushLogEvent("SYSTEM","ServicesConfigsUpdateStarting","DISPATCHER", Event.INFO);
				try {
					dispatcherConfigsSender.send(service);					
					event = new Event("SYSTEM","ServiceConfigsUpdated","DISPATCHER", Event.EVENT_TYPE, Event.UPDATE);
					kafkaService.sendEvent(event);
					service.setStatus(true);
				} catch (Exception e) {
					System.out.println("Une erreur inattendue c'est produite ! : " + e.getMessage());
					e.printStackTrace();
					eventTmp = new Event("SYSTEM","ServiceConfigsUpdateFailed","DISPATCHER", Event.EVENT_TYPE, Event.ERROR);
					kafkaService.sendEvent(eventTmp);
					e.printStackTrace();
					service.setStatus(false);
					serviceRepository.save(service);
					kafkaService.pushLogEvent("SYSTEM","ServiceStatusSetToDown","DISPATCHER", Event.ERROR);
				}
				
			}
			
		}
		
		
	}
	
	/** Verifie l'etat des services systeme
	*/
	@Scheduled( initialDelay = 1000, fixedDelay =   10 * 1000)
	public void checkSystemServiceRegistre() {
		

		Event event = new Event("SYSTEM","ServicesCheckExistenceStarted","SYSTEM", Event.EVENT_TYPE, Event.INFO);
		kafkaService.sendEvent(event);
		
		HashMap<String,String> servicesURL = this.getSystemServicesURL();
				
		for(Map.Entry<String,String> service: servicesURL.entrySet()) {
			
			try {
				systemServiceChecker.check(service.getKey(),service.getValue());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		event = new Event("SYSTEM","ServicesConfigsUpdateFinished","BACKOFFICE", Event.EVENT_TYPE, Event.INFO);
		kafkaService.sendEvent(event);
		
	}
	
}
