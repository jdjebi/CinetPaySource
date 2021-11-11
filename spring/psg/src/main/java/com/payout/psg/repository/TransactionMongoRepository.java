package com.payout.psg.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.payout.psg.model.TransactionMongo;

public interface TransactionMongoRepository extends MongoRepository<TransactionMongo, String> {

	TransactionMongo findByRemoteId(String ref);
		
	List<TransactionMongo> findByStatus(String status);
	
	List<TransactionMongo> findByStatusAndBatchnumber(String status, String batchnumber);
	
	List<TransactionMongo> findByBatchnumber(String batchnumber);
	
	List<TransactionMongo> findByBatchnumberOrderByCreatedAtAsc(String batchnumber);

}
