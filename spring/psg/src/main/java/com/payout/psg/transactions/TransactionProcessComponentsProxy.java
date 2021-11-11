package com.payout.psg.transactions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.payout.psg.kafka.KafkaEventService;
import com.payout.psg.kafka.KafkaService;
import com.payout.psg.kafka.KafkaTransactionService;
import com.payout.psg.repository.OperatorRepository;

@Component
public class TransactionProcessComponentsProxy {

	@Autowired 
	private KafkaService kafkaService;
	
	@Autowired 
	private KafkaTransactionService kafkaTrxService;
	
	@Autowired 
	private KafkaEventService kafkaEventService;
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private OperatorRepository operatorRepository;

	public KafkaService getKafkaService() {
		return kafkaService;
	}

	public void setKafkaService(KafkaService kafkaService) {
		this.kafkaService = kafkaService;
	}

	public KafkaTransactionService getKafkaTrxService() {
		return kafkaTrxService;
	}

	public void setKafkaTrxService(KafkaTransactionService kafkaTrxService) {
		this.kafkaTrxService = kafkaTrxService;
	}

	public TransactionRepository getTransactionRepository() {
		return transactionRepository;
	}

	public void setTransactionRepository(TransactionRepository transactionRepository) {
		this.transactionRepository = transactionRepository;
	}

	public OperatorRepository getOperatorRepository() {
		return operatorRepository;
	}

	public void setOperatorRepository(OperatorRepository operatorRepository) {
		this.operatorRepository = operatorRepository;
	}

	public KafkaEventService getKafkaEventService() {
		return kafkaEventService;
	}

	public void setKafkaEventService(KafkaEventService kafkaEventService) {
		this.kafkaEventService = kafkaEventService;
	}
}
