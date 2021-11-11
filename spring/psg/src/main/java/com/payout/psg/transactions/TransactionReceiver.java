package com.payout.psg.transactions;

import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.payout.psg.eventlog.entity.Event;
import com.payout.psg.kafka.KafkaService;
import com.payout.psg.kafka.KafkaTransactionService;
import com.payout.psg.model.Operator;
import com.payout.psg.repository.OperatorRepository;

@Component
public class TransactionReceiver {
	
	private KafkaService kafkaService;
	
	private KafkaTransactionService kafkaTrxService;
	
	private TransactionRepository transactionRepository;
	
	private OperatorRepository operatorRepository;
	
	public TransactionReceiver(TransactionProcessComponentsProxy trxComponentsProxy) {
		
		this.kafkaService = trxComponentsProxy.getKafkaService();
		
		this.kafkaTrxService = trxComponentsProxy.getKafkaTrxService();
		
		this.transactionRepository = trxComponentsProxy.getTransactionRepository();
		
		this.operatorRepository = trxComponentsProxy.getOperatorRepository();
	}
	
	public TransactionReceiveStatus receive(Transaction trx) throws JsonProcessingException {
		
		Transaction newTrx = null; // Objet d'une nouvelle transaction
		
		Optional<Transaction> newTrxOpt = null; // Conteneur d'existence d'une transaction
		
		// On verifie si la transaction a deja ete envoyee
		newTrxOpt = transactionRepository.findByTransactionId(trx.getTransactionId());
		
		// Creation d'une instance de statut de transaction
		TransactionReceiveStatus trxStatus = new TransactionReceiveStatus(trx.getTransactionId());
					
		if(newTrxOpt.isPresent()) { // Transaction existente, Au saute la transaction
							
			// Mise a jour du statut de reception
			trxStatus.setRefuse();  
			trxStatus.setComment("Transaction already exist");
			
			// Emission d'un evenement de transaction deja existente
			HashMap<String, Object> map =  new HashMap<String, Object>();
			map.put("error", "TransactionAlreadyExist");
			
			kafkaService.sendTransactionProcessFailed(newTrxOpt.get().getTransactionId(), map);
							
			return trxStatus;
		}
		
		// Nouvelle transaction - Verification de son integrite
			
		Operator operator = operatorRepository.findByAlias(trx.getOperator());
		
		if(operator == null) { // Operateur de la transaction introuvable: La transaction est mise en pause
			
			trx.setStatus("PAUSE");
			trx.setSystemStatus("REC");				
			trx.setPauseComment("OperatorCodeUnkown");				
			trx.setFinishedAt(new Date());
			
			newTrx = transactionRepository.save(trx);
														
			// Emission d'un evenement d'arret de traitement
			
			HashMap<String, Object> map =  new HashMap<String, Object>();
			map.put("error", "OperatorCodeUnkown");
			kafkaService.sendTransactionProcessFailed(trx.getTransactionId(), map);
			
			return trxStatus;
		}
		
		//  Transaction valide
		
		trx.setStatus("REC");	
		
		trx.setSystemStatus("REC");
		
		newTrx = transactionRepository.save(trx);
				
		trxStatus.setAccept(); // a ce stade la transaction est accepte
				
		kafkaService.pushLogEvent("TRANSACTION","TransactionSaved",trx.getTransactionId(), Event.INFO);
		
		// Preparation de l'envoie
		
		TransactionRequest trxRequest = new TransactionRequest();
		
		trxRequest.getBody().setTransaction(newTrx);
		
		// Envoie de la transaction au dispatcher
		ListenableFuture<SendResult<String, String>> future = kafkaTrxService.sendTransactionRequestToDispatcher(trxRequest); 
						
		// Emission de l'evenement d'envoie
		
		kafkaService.pushEvent("TRANSACTION","TransactionSentToDispatcher",newTrx.getTransactionId(), Event.INFO);
		
		// Gestion du callback de l'envoie

		Transaction trxTmp = newTrx;
		
		future.addCallback(new ListenableFutureCallback<SendResult<String,String>>(){
			
		    @Override
		    public void onSuccess(SendResult<String, String> result) {
		    	kafkaService.pushLogEvent("TRANSACTION","TransactionSentToDispatcherSuccess",trxTmp.getTransactionId(), Event.INFO);
		    }

		    @Override
		    public void onFailure(Throwable ex) {
		    	// En cas d'echec la transaction mise en pause
		    	trxTmp.setStatus("PAUSE");
		    	trxTmp.setStatus("KAFKA_NOT_AVAILABLE");
		    	transactionRepository.save(trxTmp);
		    	
		    	System.out.println("SET PAUSE TO" + trxTmp.getTransactionId());
		    	
		    	// Envoie d'un evenement d'echec d'envoie
		    	HashMap<String,Object> map = new HashMap<String,Object>();
		    	map.put("error",ex.getMessage());
		    	map.put("cause",ex.getCause());
		    	kafkaService.pushLogEvent("TRANSACTION","TransactionSentToDispatcherFailed",trxTmp.getTransactionId(), Event.ERROR);
		    }
		    
		});
				
		return trxStatus;
	}

}
