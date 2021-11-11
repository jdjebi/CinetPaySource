/**
 * @author Jean-Marc Dje Bi
 * @since 12-08-2021
 * @version 1
 */
package com.payout.transfert.adapter;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payout.transfert.core.eventlog.entity.Event;
import com.payout.transfert.core.kafka.KafkaService;
import com.payout.transfert.dao.Resource;
import com.payout.transfert.repository.ResourceRepository;
import com.payout.transfert.transactions.Transaction;
import com.payout.transfert.transactions.TransactionRequest;
import com.payout.transfert.unit.FinalStatus;
import com.payout.transfert.unit.TransfertUnitDispatcher;
import com.payout.transfert.unit.TransfertUnitResponse;

/**
 * Classe de gestion des transfert
 */
@Component
public class TransactionProcessorAdapter {
	
	/**
	 * @see KafkaService
	 */
	@Autowired
	KafkaService kafkaService;
	
	/**
	 * @see ResourceRepository
	 */
	@Autowired
	ResourceRepository resourceRepository;
	
	/**
	 * @see TransfertUnitDispatcher
	 */
	@Autowired
	TransfertUnitDispatcher operatorTransfertUnitDispatcher;
	
    Logger logger = LoggerFactory.getLogger(TransactionProcessorAdapter.class);
	
	public Transaction extractTransaction(String message) throws JsonMappingException, JsonProcessingException {
		TransactionRequest trxRequest = new ObjectMapper().readValue(message, TransactionRequest.class);
		return trxRequest.getBody().getTransaction();
	}
	
	/**
	 * Receptionne un transfert à effectuer
	 * @param message
	 * @param partition
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 * @throws InterruptedException
	 */
	public void on(String message, int partition) throws JsonMappingException, JsonProcessingException, InterruptedException {
				
		Transaction trx = extractTransaction(message);
		
		String trxRef = trx.getTransactionId();
		
		long threadId = Thread.currentThread().getId();
		
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("partition", partition);
		data.put("threadId", threadId);
										
		kafkaService.pushEvent("TRANSACTION","TransactionReceived",trxRef, Event.EVENT_TYPE, Event.INFO, data);
		
		logger.info(String.format("Trx received from parition %s by  thread %s : %s",partition,threadId,trxRef));
		
		process(trx);
		
	}
	
	/**
	 * Traite le transfert
	 * @param trx
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 * @throws InterruptedException 
	 */
	public void process(Transaction trx) throws JsonMappingException, JsonProcessingException, InterruptedException {
				
		Resource rx = resourceRepository.findByOperatorCode(trx.getOperator());
		
		if(rx == null) { // On verifie si la ressource du transfert existe
			
			closeTransfert(trx);

		}else {
						
			kafkaService.pushEvent("TRANSACTION","TransactionSentToOperator",trx.getTransactionId(), Event.INFO);	
							
			/**
			 *  Effectue le transfert  et recupere le resultat
			 */
			
			TransfertUnitResponse unitResponse = null;
			
			try {
				
				unitResponse = operatorTransfertUnitDispatcher.dispatch(trx,rx);				
			
				if(unitResponse != null) {
							
					// Effectue les operation de post-transfert
					sendTransactionResultToNotication(trx, rx, unitResponse);	
					
				}else { // Si la reponse est nulle
					
					kafkaService.sendTransactionEvent("TransactionFinalStatusReceived",trx.getTransactionId(), null);
	
					closeForNoTransfertUnit(trx);
					
				}
			
			} catch(ResourceAccessException e) {
				
				kafkaService.pushEvent("OPERATOR","OperatorResourceAccessException",rx.getOperatorCode(), Event.ERROR);	
								
				closeForResourceAccessException(trx, rx, e);
				
		    	logger.error("Can't access to operator '"+ rx.getOperatorCode() +"' API");

				throw e;
				
			} catch(HttpClientErrorException e) {
				
				kafkaService.pushEvent("OPERATOR","OperatorResourceAccessException",rx.getOperatorCode(), Event.ERROR);	
				
				closeForHttpClientErrorException(trx, rx, e);
				
		    	logger.error("Can't access to operator '"+ rx.getOperatorCode() +"' API");

				throw e;
				
			}
					
		}
			
	}
	
	/**
	 * Conclue le transfert envoyant le resultat au service de notification
	 * @param unitResponse
	 */
	public void sendTransactionResultToNotication(Transaction trx, Resource rx, TransfertUnitResponse unitResponse) {
		
		/**
		 * Initialisation
		 */
		
		HashMap<String, Object> response = new HashMap<String, Object>();
		
		String status = null;
		
		String comment = null;
		
		String operatorComment = null;
		
		String operatorTransactionId = null;
		
		String resourceName = null;
		
		Boolean resourceStatus = null;
		
		Integer currentBalance = null;
						
		HashMap<String, Object>  data = null;
				
		/**
		 * Creation de la nouvelle requete de transaction
		 */
		
		TransactionRequest trxRequest = new TransactionRequest();
		
		trxRequest.getBody().setTransaction(trx);
		
		/**
		 * Construction de la requete
		 */
		
		// Analyse du statut final
		
		FinalStatus finalStatus = unitResponse.getFinalStatus();
		
		status = finalStatus.getStatus();
		
		comment = finalStatus.getComment();
		
		operatorComment = finalStatus.getOperatorComment();
		
		operatorTransactionId = finalStatus.getOperatorTransactionId();
		
		currentBalance = finalStatus.getCurrentBalance();
				
		resourceName = rx.getName();

		resourceStatus = finalStatus.getResourceStatus();
		
		data = finalStatus.getBigdata();
				
		response.put("status", status);
		
		response.put("comment", comment);
		
		response.put("currentBalance", (currentBalance != null ? currentBalance : rx.getLastBalance()));
		
		response.put("operatorComment", operatorComment);
		
		response.put("operatorTransactionId",operatorTransactionId);
		
		// response.put("operatorExtrasData", operatorExtrasData);

		response.put("resourceName", resourceName);

		response.put("resourceStatus", resourceStatus);

		response.put("data", data);
		
		trxRequest.getBody().setFinalStatus(response);
		
		// Emission des evenements
		
		kafkaService.sendTransactionEvent("TransactionFinalStatusReceived",trx.getTransactionId(), response);
		
		logger.info(String.format("Trx send to operator %s with success [status:%s] : %s",trx.getOperator(),finalStatus.getStatus(), trx.getTransactionId()));
				
		/**
		 * Envoie de la transaction a apache Kafka
		 */
			
		this.sendFinalTransactionRequest(trxRequest);
		
	}
	
	/**
	 * Envoie la requete de transaction final a apache kafka en direction du service de notification
	 */
	public void sendFinalTransactionRequest(TransactionRequest trxRequest) {
		
		ListenableFuture<SendResult<String, TransactionRequest>> future = kafkaService.sendTransactionRequestToNotification(trxRequest);
		
		Transaction trx = trxRequest.getBody().getTransaction();

		future.addCallback(new ListenableFutureCallback<SendResult<String, TransactionRequest>>(){
			
		    @Override
		    public void onSuccess(SendResult<String, TransactionRequest> result) {
		    
		    	kafkaService.pushEvent("TRANSACTION","TransactionSentToNotification",trx.getTransactionId(), Event.INFO);
						    	
				endProcess(trx.getTransactionId());
				
		    }

		    @Override
		    public void onFailure(Throwable ex) {
		    	
		    	HashMap<String, Object> data = new HashMap<String, Object>();
		    	
				data.put("error", ex);	
				
				endProcess(trx.getTransactionId());
				
				logger.error(String.format("Trx (%s) sending notifcation service failed  because: %s",trx.getTransactionId(), ex.getMessage()));

				kafkaService.sendTransactionProcessFailed(trx.getTransactionId(), data);
				
		    }
		});
		
	}
	

	/**
	 * Arrete le transfert au cas ou la ressource du transfert n'existe pas
	 * @param trx
	 */
	public void closeTransfert(Transaction trx) {
		
		HashMap<String, Object> data = new HashMap<String, Object>();
		
		data.put("error", "TransactionOperatorResourceIsNotPresent");
		data.put("comment", "No API resource");	
		data.put("operator", trx.getOperator());
									
		kafkaService.pushEvent("TRANSACTION","TransactionOperatorResourceIsNotPresent", trx.getTransactionId(), Event.EVENT_TYPE, Event.ERROR, data);

		kafkaService.sendTransactionProcessFailed(trx.getTransactionId(), data);
		
		logger.error(String.format("Trx processing failed because no resource found for operator %s :",trx.getOperator(),trx.getTransactionId()));
	
		endProcess(trx.getTransactionId());

	}
		
	/**
	 * Arrete le transfert au cas ou la reponse de l'unite de transfert est null
	 * @param trx
	 */
	public void closeForNoTransfertUnit(Transaction trx) {
		
		HashMap<String, Object> data = new HashMap<String, Object>();
		
		data.put("error", "Transfert unit of " + trx.getOperator()  + " response with null");	
		data.put("operator", trx.getOperator());
					
		kafkaService.pushEvent("TRANSACTION","TransactionOperatorUnitNotFound", trx.getTransactionId(), Event.EVENT_TYPE, Event.ERROR, data);
				
		kafkaService.sendTransactionProcessFailed(trx.getTransactionId(), data);
		
		logger.error(String.format("Trx processing failed because transfert unit response for operator %s is null: %s",trx.getOperator(),trx.getTransactionId()));

		endProcess(trx.getTransactionId());
	}
	
	/**
	 * Arrete le transfert au cas ou l'api n'est pas accessible
	 * @param trx
	 */
	public void closeForResourceAccessException(Transaction trx, Resource rx, ResourceAccessException e) {
		
		HashMap<String, Object> data = new HashMap<String, Object>();
		
		data.put("error", "ResourceAccessException");	
		data.put("operator", trx.getOperator());
		data.put("message", e.getMessage());
		data.put("localizedMessage", e.getLocalizedMessage());
		data.put("apiUrl", rx.getOperator_api_url());
	
		kafkaService.pushEvent("TRANSACTION","TransactionOperatorApiInaccessible", trx.getTransactionId(), Event.EVENT_TYPE, Event.ERROR, data);
				
		kafkaService.sendTransactionProcessFailed(trx.getTransactionId(), data);
		
		logger.error(String.format("Trx processing failed because api is inaccessible for operator %s on url %s: %s",trx.getOperator(), rx.getOperator_api_url(), trx.getTransactionId()));

		endProcess(trx.getTransactionId());
	}
	
	/**
	 * Arrete le transfert au cas d'erreur lie au client Http
	 * @param trx
	 */
	public void closeForHttpClientErrorException(Transaction trx, Resource rx, HttpClientErrorException e) {
		
		HashMap<String, Object> data = new HashMap<String, Object>();
		
		data.put("error", "HttpClientErrorException");	
		data.put("operator", trx.getOperator());
		data.put("message", e.getMessage());
		data.put("localizedMessage", e.getLocalizedMessage());
		data.put("apiUrl", rx.getOperator_api_url());

					
		kafkaService.pushEvent("TRANSACTION","TransactionOperatorApiHttpClientErrorException", trx.getTransactionId(), Event.EVENT_TYPE, Event.ERROR, data);
				
		kafkaService.sendTransactionProcessFailed(trx.getTransactionId(), data);
		
		logger.error(String.format("Trx processing failed because system raise HttpClientErrorException for operator %s on url %s: %s",trx.getOperator(),rx.getOperator_api_url(),trx.getTransactionId()));

		endProcess(trx.getTransactionId());
	}
		
	public void endProcess(String trxRef) {
		
		kafkaService.pushEvent("TRANSACTION","TransactionProcessEnded", trxRef, Event.EVENT_TYPE, Event.INFO);

	}
}
