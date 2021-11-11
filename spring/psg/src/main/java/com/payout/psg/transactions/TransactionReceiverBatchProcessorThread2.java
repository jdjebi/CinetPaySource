/**
 * @author Jean-Marc Dje Bi
 * @since 29-07-2021
 * @version 1
 */
package com.payout.psg.transactions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.payout.psg.eventlog.entity.Event;
import com.payout.psg.events.EventTransaction;
import com.payout.psg.kafka.KafkaEventService;
import com.payout.psg.model.Operator;

/**
 * Classe du thread de traitement des transactions par lot
 */
public class TransactionReceiverBatchProcessorThread2 extends Thread {
	
	public List<TransactionReceiveStatus> trxReceiveStatusList;
	
	/**
	 * Objet contenant l'ensemble des composants utiles pour la reception des transaction
	 */
	private TransactionProcessComponentsProxy trxComponentsProxy;
		
	/**
	 * Liste des transactions a receptioner
	 */
	private List<Transaction> trxList;
	
	/**
	 * Liste des transactions a enregistrer
	 */
	private List<Transaction> newTrxList = new ArrayList<Transaction>();
	
	/**
	 * Evenement de reception
	 */
	private EventTransaction event;
	
	/**
	 * @see KafkaEventService
	 */
	KafkaEventService kafkaEventService;
	
	/**
	 * Table de hashage pour contenir les operateurs
	 */
	private Map<String, List<Operator>> operatorMap;
	
    Logger logger = LoggerFactory.getLogger(TransactionService.class);

	public TransactionReceiverBatchProcessorThread2(List<Transaction> trxList, TransactionProcessComponentsProxy trxComponentsProxy, List<TransactionReceiveStatus> trxReceiveStatusList, EventTransaction event, Map<String, List<Operator>> operatorMap){	    
		super("Thread-" + (new Date()).getTime());	    
	    this.trxReceiveStatusList = new ArrayList<TransactionReceiveStatus>();	    
	    this.trxList = trxList;	    
	    this.trxComponentsProxy = trxComponentsProxy;
	    this.event = new EventTransaction(event); 	    
	    this.operatorMap = operatorMap;	    
	    this.kafkaEventService = trxComponentsProxy.getKafkaEventService();
	}
	
	/**
	 * Methode d'execution du thread
	 */
	public void run(){
		
		for(Transaction trx: trxList) {
			
			logger.info("process start");

			kafkaEventService.pushForEntity(event, trx); // Emission d'un evenement de reception
						
			kafkaEventService.emit("TransactionProcessStarted", trx);
			
			if(validateTransaction(trx)) { // Si la transaction est invalide, on passe a la suivante, son statut est automatiquement mis a jour
				atomicProcess(trx); // Traitement
			}else {
				continue;
			}
				
		}
		
	}
	
	/**
	 * Valide une transaction : La methode ajoute la transaction a la liste des nouvelles transactions en cas de validation.
	 * Elle gere aussi la determination du status
	 * @param trx
	 * @param newtrxList
	 * @return
	 */
	public boolean validateTransaction(Transaction trx) {
				
		TransactionReceiveStatus trxStatus = new TransactionReceiveStatus(trx.getTransactionId());
				
		boolean validate = true;
						
		// On verifie si la transaction a deja ete envoyee
				
		kafkaEventService.emit("TransactionExistanceCheck", trx);
		
		if(isTransactionAlreadySaved(trx)) {
			
			// Emission d'un evenement de transaction deja existente	
			
			HashMap<String, Object> map =  new HashMap<String, Object>();
			map.put("error", "TransactionAlreadyExist");
			
			kafkaEventService.emitProcessFailed(trx, map);
						
			// Mise a jour du statut de reception
			
			trxStatus.setRefuse();  
			
			trxStatus.setComment("Transaction already exist");
							
			trxReceiveStatusList.add(trxStatus);
			
			return false;

		}
		
		kafkaEventService.log("TransactionExistanceChecked", trx);
				
		// Verification de son integrite
		
		kafkaEventService.log("TransactionOperatorCheck", trx);
						
		if(!operatorMap.containsKey(trx.getOperator())) { // Operateur de la transaction introuvable: La transaction est mise en pause
			
			// Mise en pause de la transaction
			
			logger.info("Operator " + trx.getOperator() + " not operator configured");
											
			// Emission d'un evenement d'arret de traitement
			
			HashMap<String, Object> map =  new HashMap<String, Object>();
			
			map.put("error", "OperatorCodeUnkown");
			
			map.put("operator", trx.getOperator());
			
			kafkaEventService.emitProcessFailed(trx, map);
			
			
			// Mise a jour du statut de reception
			
			trxStatus.setProblem();
			
			trxStatus.setComment("Operator '"+ trx.getOperator() +"' not configured");
			
			trxReceiveStatusList.add(trxStatus);										
			
		}else {
			
			kafkaEventService.log("TransactionOperatorChecked", trx);
						
			trx.setStatus("REC");
			
			trx.setSystemStatus("REC");	
			
			trxStatus.setAccept(); // a ce stade la transaction est accepte
			
			trxReceiveStatusList.add(trxStatus);
			
			newTrxList.add(trx); // Ajout de la transaction a la liste des nouvelles transaction 
						
		}
		
		kafkaEventService.log("TransactionProcessIterationEnded", trx);
				
		return validate;
	}
	
	
	public void process(List<Transaction> newTrxList) {
		
		Event event = new Event("TRANSACTION","TransactionSaving",null,Event.LOG_TYPE,Event.INFO);
		
		// Enregistrement des transactions

		List<Transaction> transactions = this.trxComponentsProxy.getTransactionRepository().saveAll(newTrxList);
		
		Event event2 = new Event("TRANSACTION","TransactionSaved",null,Event.LOG_TYPE,Event.INFO);

		for(Transaction t: transactions){
						
			// Emission de l'evenement de fin d'enregistrement
			
			kafkaEventService.pushForEntity(event, t);
			kafkaEventService.pushForEntity(event2, t);
			
			// Preparation de l'envoie de la transaction au dispatcher
			
			TransactionRequest trxRequest = new TransactionRequest();
			
			trxRequest.getBody().setTransaction(t);
			
			// Envoie de la transaction au dispatcher
			
			ListenableFuture<SendResult<String, String>> future;
			
			//t.setStatus("PAUSE");
			//t.setPauseComment("SYSTEM_AUTO_PAUSE");
			
			trxComponentsProxy.getTransactionRepository().save(t);

			try {
				
				future = trxComponentsProxy.getKafkaService().sendTransactionRequestToDispatcher(trxRequest);
				
				logger.info("Send to dispatcher: " + t.getTransactionId());
				
				trxComponentsProxy.getKafkaService().pushEvent("TRANSACTION","TransactionSentToDispatcher",t.getTransactionId(), Event.INFO);
				
				// Gestion du callback de l'envoie
				
				Transaction trxTmp = t;
				
				future.addCallback(new ListenableFutureCallback<SendResult<String,String>>(){
					
				    @Override
				    public void onSuccess(SendResult<String, String> result) {
				    	
				    	trxComponentsProxy.getKafkaService().pushLogEvent("TRANSACTION","TransactionSentToDispatcherSuccess",trxTmp.getTransactionId(), Event.INFO);
				    	
						logger.error("Sending to dispatcher succed: " + t.getTransactionId());
						
				    }
	
				    @Override
				    public void onFailure(Throwable ex) {
				    	
				    	// En cas d'echec la transaction mise en pause
				    	
						logger.error("Sending to dispatcher failed: " + t.getTransactionId());
				    	
				    	trxTmp.setStatus("PAUSE");
				    	
				    	trxTmp.setComment("KAFKA_NOT_AVAILABLE");
				    	
				    	trxComponentsProxy.getTransactionRepository().save(trxTmp);
				    						    	
				    	// Envoie d'un evenement d'echec d'envoie
				    	
				    	HashMap<String,Object> map = new HashMap<String,Object>();
				    	
				    	map.put("error",ex.getMessage());
				    	
				    	map.put("cause",ex.getCause());
				    	
				    	trxComponentsProxy.getKafkaService().pushLogEvent("TRANSACTION","TransactionSentToDispatcherFailed",trxTmp.getTransactionId(), Event.ERROR);
				    
				    }
				    
				});
				
			} catch (JsonProcessingException e) {
				
				e.printStackTrace();
				
			}
			
		}
	}
	
	/**
	 * Opere la reception sur une et une seule transaction
	 * @param trx
	 * @return
	 */
	public boolean atomicProcess(Transaction newTrx) {
		
		kafkaEventService.emit("TransactionSaving", newTrx);
		
		Transaction trx = trxComponentsProxy.getTransactionRepository().save(newTrx);
		
		kafkaEventService.emit("TransactionSaved", trx);
		
		// Preparation de l'envoie de la transaction au dispatcher
		
		TransactionRequest trxRequest = new TransactionRequest();
		
		trxRequest.getBody().setTransaction(trx);
		
		// Envoie de la transaction au dispatcher
		
		ListenableFuture<SendResult<String, String>> future;
		
		//t.setStatus("PAUSE");
		//t.setPauseComment("SYSTEM_AUTO_PAUSE");
		
		trxComponentsProxy.getTransactionRepository().save(trx);

		try {
			
			future = trxComponentsProxy.getKafkaService().sendTransactionRequestToDispatcher(trxRequest);
			
			logger.info("Send to dispatcher: " + trx.getTransactionId());
			
			trxComponentsProxy.getKafkaService().pushEvent("TRANSACTION","TransactionSentToDispatcher",trx.getTransactionId(), Event.INFO);
			
			// Gestion du callback de l'envoie
			
			Transaction trxTmp = trx;
			
			future.addCallback(new ListenableFutureCallback<SendResult<String,String>>(){
				
			    @Override
			    public void onSuccess(SendResult<String, String> result) {
			    	
			    	trxComponentsProxy.getKafkaService().pushLogEvent("TRANSACTION","TransactionSentToDispatcherSuccess",trxTmp.getTransactionId(), Event.INFO);
			    	
					logger.error("Sending to dispatcher succed: " + trx.getTransactionId());
					
			    }

			    @Override
			    public void onFailure(Throwable ex) {
			    	
			    	// En cas d'echec la transaction mise en pause
			    	
					logger.error("Sending to dispatcher failed: " + trx.getTransactionId());
			    	
			    	trxTmp.setStatus("PAUSE");
			    	
			    	trxTmp.setComment("KAFKA_NOT_AVAILABLE");
			    	
			    	trxComponentsProxy.getTransactionRepository().save(trxTmp);
			    						    	
			    	// Envoie d'un evenement d'echec d'envoie
			    	
			    	HashMap<String,Object> map = new HashMap<String,Object>();
			    	
			    	map.put("error",ex.getMessage());
			    	
			    	map.put("cause",ex.getCause());
			    	
			    	trxComponentsProxy.getKafkaService().pushLogEvent("TRANSACTION","TransactionSentToDispatcherFailed",trxTmp.getTransactionId(), Event.ERROR);
			    
			    }
			    
			});
			
		} catch (JsonProcessingException e) {
			
			e.printStackTrace();
			
		}
					
		return true;
		
	}
	
	/**
	 * Verifie si la transaction n'a pas encore ete enregistree
	 * @param trx
	 * @return true si la transaction n'existe pas
	 */
	public boolean isTransactionAlreadySaved(Transaction trx) {
		
		// Conteneur d'existence d'une transaction
		Optional<Transaction> newTrxOpt = trxComponentsProxy.getTransactionRepository().findByTransactionId(trx.getTransactionId());
				
		if(newTrxOpt.isPresent()) { // Transaction existente, Au saute la transaction
			return true;
		}else {
			return false;
		}
		
	}

}
