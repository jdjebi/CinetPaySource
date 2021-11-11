package com.payout.psg.kafka;

import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payout.psg.eventlog.entity.Event;
import com.payout.psg.transactions.TransactionRequest;

/**
 * Classe du service kafka pour l'envoie des requetes de transactions entre services
 */
@Component("kafkaTransactionsService")
public class KafkaTransactionService extends KafkaService{

	/**
	 * Envoie une requete de transaction au dispatcher
	 */
	@Override
	public ListenableFuture<SendResult<String, String>> sendTransactionRequestToDispatcher(TransactionRequest trxRequest) throws JsonProcessingException {
		
		ObjectMapper mapper = new ObjectMapper();
		
		String trxRequestStr = null;
		
		trxRequestStr = mapper.writeValueAsString(trxRequest);
				
		ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send("transaction_request_v2",trxRequestStr);
		
		return future;
		
	}
}
