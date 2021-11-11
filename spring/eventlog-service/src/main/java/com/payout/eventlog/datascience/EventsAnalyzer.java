/**
 * @author Jean-Marc Dje Bi
 * @since 28-07-2021
 * @version 1
 */

package com.payout.eventlog.datascience;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;

import com.payout.eventlog.dao.Event;

/**
 * Classe d'analyse des evenements contenu dans un TransactionEventsPrettyFormat
 */
public class EventsAnalyzer {

	/**
	 * @see TransactionEventsPrettyFormat
	 */
	private TransactionEventsPrettyFormat transactionEventsPrettyFormat;
	
	public EventsAnalyzer(TransactionEventsPrettyFormat transactionEventsPrettyFormat) {
		this.transactionEventsPrettyFormat = transactionEventsPrettyFormat;
	}

	/**
	 * Analyse de façon generale les evenements
	 * @return HashMap des temps de traitement
	 */
	public HashMap<String, Object> analyzeAll() {
		
		HashMap<String, Object> gatewayEvents = transactionEventsPrettyFormat.get("GATEWAY");
		
		HashMap<String, Object> dispatcherEvents = transactionEventsPrettyFormat.get("DISPATCHER");
		
		HashMap<String, Object> transfertServiceEvents = transactionEventsPrettyFormat.get("TRANSFERT-SERVICE");
		
		HashMap<String, Object> notificationEvents = transactionEventsPrettyFormat.get("NOTIFICATION-SERVICE");
				
		HashMap<String, Object> DataMap = new HashMap<String, Object>();
		
		HashMap<String, Long> gatewayMap = new HashMap<String, Long>();
		
		HashMap<String, Long> dispatcherMap = new HashMap<String, Long>();
		
		HashMap<String, Long> transfertServiceMap = new HashMap<String, Long>();
		
		HashMap<String, Long> notificationMap = new HashMap<String, Long>();
		
		long currentDuration = 0;

		Event e1 = null;
		Event e2 = null;
		
		/* Passerelle */
				
		// Temps de traitement au niveau de la passerelle
		e1 = (Event) gatewayEvents.get("TransactionReceived");
		e2 = (Event) gatewayEvents.get("TransactionSentToDispatcher");
		currentDuration = getDateDifference(e1, e2);		
		gatewayMap.put("gatewayProcessDuration", currentDuration);
				
		// Temps de traitement d'attente avant traitement
		e1 = (Event) gatewayEvents.get("TransactionReceived");
		e2 = (Event) gatewayEvents.get("TransactionProcessStarted");
		currentDuration = getDateDifference(e1, e2);		
		gatewayMap.put("gatewayWaitingProcessDuration", currentDuration);
		
		// Temps de traitement passer dans la boucle de traitement
		e1 = (Event) gatewayEvents.get("TransactionProcessStarted");
		e2 = (Event) gatewayEvents.get("TransactionProcessIterationEnded");
		currentDuration = getDateDifference(e1, e2);
		gatewayMap.put("gatewayProcessLoopIterationDuration", currentDuration);
		
		// Temps de traitement passer dans la boucle de traitement
		e1 = (Event) gatewayEvents.get("TransactionReceived");
		e2 = (Event) gatewayEvents.get("TransactionSaving");
		currentDuration = getDateDifference(e1, e2);
		gatewayMap.put("gatewayProcessLoopDuration", currentDuration);	
		
		// Temps de d'enregistrement de la transaction
		e1 = (Event) gatewayEvents.get("TransactionSaving");
		e2 = (Event) gatewayEvents.get("TransactionSaved");
		currentDuration = getDateDifference(e1, e2);
		gatewayMap.put("gatewaySaveDuration", currentDuration);	
		
		// Temps de verification de l'operateur
		e1 = (Event) gatewayEvents.get("TransactionExistanceCheck");	
		e2 = (Event) gatewayEvents.get("TransactionExistanceChecked");	
		currentDuration = getDateDifference(e1, e2);
		gatewayMap.put("gatewayTransactionExistanceCheck", currentDuration);	
		
		// Temps de verification de la transaction
		e1 = (Event) gatewayEvents.get("TransactionOperatorCheck");	
		e2 = (Event) gatewayEvents.get("TransactionOperatorChecked");
		currentDuration = getDateDifference(e1, e2);
		gatewayMap.put("gatewayTransactionOperatorCheck", currentDuration);	
		
		if(dispatcherEvents != null) {
			
			// Temps d'envoie de la transaction a la passerelle au niveau du dispatcher
			e1 = (Event) gatewayEvents.get("TransactionSentToDispatcher");
			e2 = (Event) dispatcherEvents.get("TransactionReceived");
			currentDuration = getDateDifference(e1, e2);				
			gatewayMap.put("sendToDispatcherDuration", currentDuration);	
			
			// Temps de traitement de la transaction dans le dispatcher
			e1 = (Event) dispatcherEvents.get("TransactionReceived");
			e2 = (Event) dispatcherEvents.get("TransactionProcessEnded");
			currentDuration = getDateDifference(e1, e2);		
			dispatcherMap.put("dispatcherProcessDuration", currentDuration);	
			
		}	
		
		if(transfertServiceEvents != null) {
			
			// Temps d'envoie de la transaction dispatcher-service de transfert			
			e1 = (Event) dispatcherEvents.get("TransactionSentToTransfertService");	
			e2 = (Event) transfertServiceEvents.get("TransactionReceived");		
			currentDuration = getDateDifference(e1, e2);		
			dispatcherMap.put("sendToTransfertServiceDuration", currentDuration);	
									
			// Temps de traitement du service de transfert		
			e1 = (Event) transfertServiceEvents.get("TransactionReceived");	
			e2 = (Event) transfertServiceEvents.get("TransactionProcessEnded");		
			currentDuration = getDateDifference(e1, e2);		
			transfertServiceMap.put("transfertServiceProcessDuration", currentDuration);	
			
			// Temps avant l'envoie a l'operateur
			e1 = (Event) transfertServiceEvents.get("TransactionReceived");	
			e2 = (Event) transfertServiceEvents.get("TransactionSentToOperator");		
			currentDuration = getDateDifference(e1, e2);		
			transfertServiceMap.put("transfertServiceTimeoutTransactionSendingToOperator", currentDuration);	
			
			// Temps de reception du statut final	
			e1 = (Event) transfertServiceEvents.get("TransactionSentToOperator");	
			e2 = (Event) transfertServiceEvents.get("TransactionFinalStatusReceived");		
			currentDuration = getDateDifference(e1, e2);		
			transfertServiceMap.put("transfertServiceReceivingFinalStatusDuration", currentDuration);	
			
			// Temps avant envoie de la notification au service de notification
			e1 = (Event) transfertServiceEvents.get("TransactionFinalStatusReceived");	
			e2 = (Event) transfertServiceEvents.get("TransactionSentToNotification");		
			currentDuration = getDateDifference(e1, e2);		
			transfertServiceMap.put("transfertServiceTimeoutSendingToNotificationDuration", currentDuration);	
						
		}
		
		if(notificationEvents != null) {
			
			// Temps d'envoie de la transaction service de transfert-notification			
			e1 = (Event) transfertServiceEvents.get("TransactionProcessEnded");	
			e2 = (Event) notificationEvents.get("TransactionWithFinalStatusReceived");		
			currentDuration = getDateDifference(e1, e2);		
			transfertServiceMap.put("sendToNotificationDuration", currentDuration);	
									
			// Temps de traitement du service de transfert		
			e1 = (Event) notificationEvents.get("TransactionWithFinalStatusReceived");	
			e2 = (Event) notificationEvents.get("TransactionNotified");		
			currentDuration = getDateDifference(e1, e2);		
			notificationMap.put("notificationProcessDuration", currentDuration);	
						
		}
		
		DataMap.put("gateway", gatewayMap);
		DataMap.put("dispatcher", dispatcherMap);	
		DataMap.put("transfert", transfertServiceMap);	
		DataMap.put("notification", notificationMap);
		
		return DataMap;
		
	}
	
	/**
	 * 
	 * @param e1 Evenement 1
	 * @param e2 Evenement 2
	 */
	public long getDateDifference(Event e1, Event e2) {
		
		if(e1 == null || e2 == null) {
			return -1;
		}
		
		long t1 = getMilli(e1);
		long t2 = getMilli(e2);
		
		return t2 - t1;
	}
	
	/**
	 * Converti la date de creation de l'evenement en millisecondes
	 * @param e Evenement
	 * @return long
	 */
	public long getMilli(Event e) {
		LocalDateTime t = e.getDate();
		ZonedDateTime zdt = ZonedDateTime.of(t, ZoneId.systemDefault());
		return zdt.toInstant().toEpochMilli();
	}
	
	
}
