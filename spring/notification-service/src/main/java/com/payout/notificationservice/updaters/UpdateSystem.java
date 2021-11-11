/**
 * @author Jean-Marc Dje Bi
 * since 26-08-2021
 * @version 1
 */
package com.payout.notificationservice.updaters;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.payout.notificationservice.core.eventlog.entity.Event;
import com.payout.notificationservice.helpers.HashHelper;
import com.payout.notificationservice.kafka.KafkaService;
import com.payout.notificationservice.transaction.Transaction;
import com.payout.notificationservice.transaction.TransactionService;

/**
 * Classe pour la mise a jour du systeme:
 * - Mise a jour du statut et du solde de la resource du transfert
 */
@Component
public class UpdateSystem {
	
	@Autowired
	KafkaService kafkaService;
	
    Logger logger = LoggerFactory.getLogger(TransactionService.class);

	public void update(Transaction trx, HashMap<String, Object> finalStatus) {
		
		/**
		 * Initialisation
		 */
		HashMap<String, Object> data = new HashMap<String, Object>();

		Boolean resourceStatus = (Boolean) finalStatus.get("resourceStatus");
		
		String resourceName = (String) finalStatus.get("resourceName");
		
		Integer currentBalance = (Integer) finalStatus.get("currentBalance");
		
		logger.info(String.format("Resource '%s' updating sent to backoffice [Status=%s; Balance=%s]",resourceName,resourceStatus,currentBalance));
				
		/**
		 * Construction de la structure de mise a jour
		 */
		
		data.put("resourceName", resourceName);
		
		data.put("status", resourceStatus);
		
		data.put("currentBalance", currentBalance);
				
		/**
		 * Envoie de la resource au backoffice
		 */
		kafkaService.sendMessage("backoffice_update_resources", data);
				
		/**
		 * Emission d'un evenement d'envoie de mises a jour
		 */
		kafkaService.pushEvent("SERVICE","OperatorSentResourceUpdated",resourceName,Event.EVENT_TYPE,Event.INFO,data);

		
	}
}
