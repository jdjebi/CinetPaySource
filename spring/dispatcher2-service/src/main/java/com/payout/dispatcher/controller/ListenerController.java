/**
 * @author Jean-Marc Dje Bi
 * @since 10-08-2021
 * @version 1.1.0
 */

package com.payout.dispatcher.controller;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.payout.dispatcher.dispatchers.BasicApiDispatcher;
import com.payout.dispatcher.eventlog.entity.Event;
import com.payout.dispatcher.kafka.KafkaService;
import com.payout.dispatcher.operators.OperatorRepository;
import com.payout.dispatcher.transactions.Transaction;

/**
 * Classe du consumer du topic des transactions venant de la passerelle
 */
@Component
public class ListenerController {
	
	/**
	 * @see KafkaService
	 */
	@Autowired
	KafkaService kafkaService;
	
	/**
	 * @see OperatorRepository
	 */
	@Autowired
	protected OperatorRepository operatorRepository;
	
	Logger logger = LoggerFactory.getLogger(ListenerController.class);
	
	@KafkaListener(topics = "transaction_request_v2")
	public void getTransactionRequest(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) throws JsonMappingException, JsonProcessingException {
		
		long threadId = Thread.currentThread().getId();

		// Reception
		Transaction trx = BasicApiDispatcher.extractTransaction(message);
				
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("partition", partition);
		data.put("threadId", threadId);
				
		kafkaService.pushEvent("TRANSACTION","TransactionReceived",trx.getTransactionId(), Event.EVENT_TYPE, Event.INFO, data);
		logger.info(String.format("Transaction received from parition %d by thread %d : %s",partition, threadId, trx.getTransactionId()));
		
		// Debut du dispatch

		BasicApiDispatcher dispatcher = new BasicApiDispatcher();

		dispatcher.setup(kafkaService, operatorRepository);

		dispatcher.dispatch(trx);
	
	}

}
