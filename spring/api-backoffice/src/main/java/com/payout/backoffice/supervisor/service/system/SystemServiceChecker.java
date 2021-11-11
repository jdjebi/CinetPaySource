package com.payout.backoffice.supervisor.service.system;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.payout.backoffice.dao.Service;
import com.payout.backoffice.eventlog.entity.Event;
import com.payout.backoffice.kafka.KafkaService;
import com.payout.backoffice.repository.ServiceRepository;
import com.payout.backoffice.supervisor.service.entity.ServiceInfo;
import com.payout.backoffice.supervisor.service.entity.ServiceInfoWrapper;

/**
*
* Classe de verification de la presence des services systeme
*
* @author  Jean-Marc Dje Bi
* @version 1
* @since   14-07-2021
*
*/
@Component
public class SystemServiceChecker {
	
	@Autowired
	private ServiceRepository serviceRepository;
	
	@Autowired 
	private KafkaService kafkaService;
	
	/** Verifie l'existence d'un service a parti de son role. Si le service n'existe pas on recupere
	 * ses informations depuis son endpoint actuator et on cree le service.
	 * Lorsque le service est contactable, alors son statut est mis a true sinon il est mis a false
	*/
	public void check(String role, String url) throws Exception {
		
		List<Service> services = serviceRepository.findByRole(role);
				
		if(services.isEmpty()) {
			
			Event event = new Event("SYSTEM","ServiceMissingDetected","ServiceRole" + role, Event.EVENT_TYPE, Event.WARNING);
			kafkaService.sendEvent(event);

			RestTemplate restTemplate = new RestTemplate();
			ServiceInfoWrapper response = null;
			Service service = null;
			
			try{
				
				response = restTemplate.getForObject(url + "/actuator/info", ServiceInfoWrapper.class);		
				ServiceInfo serviceInfo = response.getApp();
				
				service = new Service();
				service.setName(serviceInfo.getName());
				service.setDescription(serviceInfo.getDescription());
				service.setCode(serviceInfo.getCode());
				service.setFullname(serviceInfo.getFullname());
				service.setStatus(true);
				service.setPriority(1);
				service.setType("SYSTEM");
				service.setRole(role);
				service.setUrl(url);
				service.setStatus(true);
				service.setActive(true);
				serviceRepository.save(service);
				
				event = new Event("SYSTEM","SystemServiceCreated",service.getRole(), Event.EVENT_TYPE, Event.WARNING);
				kafkaService.sendEvent(event);
				System.out.println(event);	
			}catch(Exception e) {
				System.out.println(e.getMessage());
				throw new Exception("une erreur c'est produite");
			}
		}else if(services.size() > 1) {
			
			Event event = new Event("SYSTEM","MultiServiceDetected",role, Event.EVENT_TYPE, Event.WARNING);
			kafkaService.sendEvent(event);
			
			for(int i = 0; i < services.size() - 2; i++) {
				serviceRepository.delete(services.get(i));
			}
			
			event = new Event("SYSTEM","DuplicateServiceDeleted","Service", Event.EVENT_TYPE, Event.WARNING);
			kafkaService.sendEvent(event);

		}else {
			
			Service service = services.get(0);

			Event event = new Event("SYSTEM","SystemServiceUpdating",service.getRole(), Event.LOG_TYPE, Event.WARNING);
			kafkaService.sendEvent(event);

			RestTemplate restTemplate = new RestTemplate();
			ServiceInfoWrapper response = null;
						
			try{
				response = restTemplate.getForObject(url + "/actuator/info", ServiceInfoWrapper.class);		
				ServiceInfo serviceInfo = response.getApp();
				
				service.setName(serviceInfo.getName());
				service.setDescription(serviceInfo.getDescription());
				service.setCode(serviceInfo.getCode());
				service.setFullname(serviceInfo.getFullname());
				service.setStatus(true);
				service.setType("SYSTEM");
				service.setRole(role);
				service.setUrl(url);
				service.setPriority(1);
				serviceRepository.save(service);
				
				event = new Event("SYSTEM","SystemServiceUpdated",service.getRole(), Event.EVENT_TYPE, Event.UPDATE);
				kafkaService.sendEvent(event);
			}catch(Exception e) {
				service.setStatus(false);
				serviceRepository.save(service);
				event = new Event("SYSTEM","ServiceCheckFailed",service.getRole(), Event.EVENT_TYPE, Event.ERROR);
				kafkaService.sendEvent(event);
				System.out.println("@Vérification du service " + service.getName() +  " échouée: " + e.getMessage());
			}
		}
			
	}
}
