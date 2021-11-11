/**
 * @author Jean-Marc Dje Bi
 * @since 26-06-2021
 * @version 1
 */
package com.payout.notificationservice.listener;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payout.notificationservice.core.eventlog.entity.Event;
import com.payout.notificationservice.kafka.KafkaService;
import com.payout.notificationservice.transaction.Transaction;
import com.payout.notificationservice.transaction.TransactionRepository;
import com.payout.notificationservice.transaction.TransactionRequest;
import com.payout.notificationservice.transaction.TransactionService;
import com.payout.notificationservice.updaters.UpdateSystem;

/**
 * Classe responsable traitement requete de transaction venant d'un service de transfert
 */
@Component
public class TransactionListenerAdapter {
	
	/**
	 * @see TransactionRepository
	 */
	@Autowired
	TransactionRepository transactionRepository;
	
	/**
	 * @see KafkaService
	 */
	@Autowired
	KafkaService kafkaService;
	
	/**
	 * @see TransactionService
	 */
	@Autowired
	TransactionService transactionService;
	
	@Autowired
	UpdateSystem updateSystem;
	
    Logger logger = LoggerFactory.getLogger(TransactionService.class);
	
    /**
     * Declenche le processus de traitement
     * @param message
     * @param partition
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
	public void on(String message, int partition) throws JsonMappingException, JsonProcessingException {
				
		TransactionRequest trxRequest = extractTransaction(message);
		
		Transaction trx = trxRequest.getBody().getTransaction();
		
		logger.info("Trx received on partition " + partition + " by thread " + Thread.currentThread().getId() + ": " + trx.getTransactionId());
			
		HashMap<String, Object> data = getEventData(trx, partition);
				
		kafkaService.pushEvent("TRANSACTION","TransactionWithFinalStatusReceived",trx.getTransactionId(), Event.EVENT_TYPE,Event.INFO, data);
		
		this.process(trx,trxRequest);
						
		kafkaService.pushEvent("TRANSACTION","TransactionNotified",trx.getTransactionId(), Event.INFO);
		
		logger.info("Trx global process finished " + trx.getTransactionId());

	}
	
	/**
	 * Traite la requete de transaction
	 * @param trx
	 * @param trxRequest
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	public void process(Transaction trx, TransactionRequest trxRequest) throws JsonMappingException, JsonProcessingException {
		
		HashMap<String,Object> finaStatus = trxRequest.getBody().getFinalStatus();
				
		// Mise a jour de la transaction
		transactionService.update(trx.getTransactionId(), trxRequest.getBody().getFinalStatus());
		
		// Mise a jour du systeme
		updateSystem.update(trx, finaStatus);
		
	}
	
	public TransactionRequest extractTransaction(String message) throws JsonMappingException, JsonProcessingException {
		
		return new ObjectMapper().readValue(message, TransactionRequest.class);
		
	}
	
	public HashMap<String, Object> getEventData(Transaction trx, int partition) {
		
		HashMap<String, Object> data = new HashMap<String, Object>();
		
		data.put("partition", partition);
		
		data.put("threadId", Thread.currentThread().getId());
				
		return data;
		
	}
}
