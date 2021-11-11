/**
 * @author Jean-Marc Dje Bi
 * @since 28-07-2021
 * @version
 */
package com.payout.eventlog.datascience;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Component;

import com.payout.eventlog.dao.Event;

/**
 * Classe permet de formater les evenements d'une transaction dans un structure de donnees de type table de hashage
 * pour faciliter leur analyse.
 * Exemple: La determination des temps de traitement
 * 
 * La structure de donnee de presente comme suite: les titres d'evenement sont regroupes par leur service d'origine et les donnees des evenements sont associes a leur titre.
 * Exemple:
 * {
 * 	"GATEWAY":{
 * 		"TransactionReceived":{
 * 			id:"xxxxxxx",
 *          action:"TransactionReceived",
 *          date:"xx-xx-xx",
 * 		},
 * 		....
 * 	}
 *  "DISPATCHER":{
 * 		"TransactionReceived":{
 * 			id:"xxxxxxx",
 *          action:"TransactionReceived",
 *          date:"xx-xx-xx",
 * 		},
 * 		....
 * 	}
 * }
 */
@Component
public class TransactionEventsPrettyFormater {
	
	/**
	 * Formate la liste des evenements en table de hashage pour faciliter son analyse
	 * @param events
	 * @return
	 * @throws Exception
	 * @return TransactionEventsPrettyFormat
	 */
	public TransactionEventsPrettyFormat format(List<Event> events) throws Exception {
		
		TransactionEventsPrettyFormat eventsPretty = new TransactionEventsPrettyFormat();
		
		for(Event event: events) {
			
			String serviceName = event.getOriginService();
						
			eventsPretty.putIfAbsent(serviceName, new HashMap<String, Object>());
			
			HashMap<String, Object> eventsContainer = eventsPretty.get(serviceName);
			
			/*
			if(eventsContainer.containsKey(event.getAction())){
				throw new Exception("Duplicate event found: " + event.getAction() + " from " + event.getOriginService() + ". Event must be unique per service");
			}
			*/
			
			eventsContainer.put(event.getAction(), event);
					
		}
		
		return eventsPretty;
	}
}
