package com.payout.notificationservice.transaction;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionRepository extends MongoRepository<Transaction, String> {

	Optional<Transaction> findByTransactionId(String id);
		
	List<Transaction> findByStatus(String status);
	
	List<Transaction> findByStatusAndBatchnumber(String status, String batchnumber);
	
	List<Transaction> findByBatchnumber(String batchnumber);
	
	List<Transaction> findByBatchnumberOrderByCreatedAtAsc(String batchnumber);

}
