/**
 * @author Jean-Marc Dje Bi
 * @since 21-07-2021
 * @version 1.1
 */
package com.payout.psg.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.payout.psg.kafka.KafkaService;
import com.payout.psg.model.TransactionMongo;
import com.payout.psg.repository.TransactionMongoRepository;
import com.payout.psg.transactions.Transaction;
import com.payout.psg.transactions.TransactionRepository;

@CrossOrigin
@RestController
public class TransactionController {

	@Autowired
	private TransactionMongoRepository transactionRepository;
	
	@Autowired
	private TransactionRepository transactionRepository2;
	
	@Autowired
	private KafkaService kafkaService;
	
	@Autowired 
	MongoTemplate mongoTemplate;
	
	@GetMapping("/transactions")
	public List<Transaction> getTransaction() {
				
		Query query = new Query();
				
		query.limit(500);
		
		Sort sort = Sort.by("created_at").descending();
		
		query.with(sort);	
		
		return mongoTemplate.find(query, Transaction.class);
	}
	
	@GetMapping("/transactions/findbyref/{ref}")
	public TransactionMongo getTransaction(@PathVariable String ref) {
		
		return transactionRepository.findByRemoteId(ref);
	}
	
	@GetMapping("v2/transactions/{batchnumber}")
	public List<Transaction> getTransactionByBatchnumber(@PathVariable String batchnumber) {
		
		List<Transaction> trxList = transactionRepository2.findByBatchnumberOrderByCreatedAtAsc(batchnumber);
			
		return trxList;
	}
	
	@GetMapping("/transactions/stats")
	public HashMap<String, Object> getTransactionStats() {
		
		List<Transaction> recTrx = transactionRepository2.findByStatus("REC");
		List<Transaction> successTrx = transactionRepository2.findByStatus("SUCCESS");
		List<Transaction> pauseTrx = transactionRepository2.findByStatus("PAUSE");
		List<Transaction> failureTrx = transactionRepository2.findByStatus("FAILURE");
		
		HashMap<String, Object> response = new HashMap<String, Object>();
		
		response.put("rec", recTrx.size());
		response.put("success", successTrx.size());
		response.put("pause", pauseTrx.size());
		response.put("failure", failureTrx.size());
		
		return response;
	}
	
	@GetMapping("/transactions/stats/{batchnumber}")
	public HashMap<String, Object> getTransactionStatsbyBatchnumber2(@PathVariable String batchnumber) {
		
		List<TransactionMongo> newTrx = transactionRepository.findByStatusAndBatchnumber("NEW",batchnumber);
		List<TransactionMongo> currentTrx = transactionRepository.findByStatusAndBatchnumber("SUCCESS",batchnumber);
		List<TransactionMongo> pauseTrx = transactionRepository.findByStatusAndBatchnumber("PAUSE",batchnumber);
		List<TransactionMongo> failedTrx = transactionRepository.findByStatusAndBatchnumber("FAILURE",batchnumber);
		
		List<TransactionMongo> trxWithFinalStatus = new ArrayList<TransactionMongo>();
		
		trxWithFinalStatus.addAll(currentTrx);
		trxWithFinalStatus.addAll(failedTrx);


		HashMap<String, Object> response = new HashMap<String, Object>();
						
		long minDuration = 0;
		long maxDuration = 0;
		long avgDuration = 0;
	
		for(TransactionMongo trx: trxWithFinalStatus) {
			
			if(trx.getFinishedAt() != null) {
								
				long durationTrx = trx.getFinishedAt().getTime() - trx.getCreatedAt().getTime();
				
				avgDuration += durationTrx;
				
				if(minDuration == 0) {
					minDuration = durationTrx;
				}
				
				if(durationTrx < minDuration) {
					minDuration = durationTrx;
				}
				
				if(durationTrx > maxDuration) {
					maxDuration = durationTrx;
				}
					
			}
			
		}
		
		if(trxWithFinalStatus.size() != 0) {
			avgDuration = avgDuration / trxWithFinalStatus.size();
		}
		
		response.put("new", newTrx.size());
		response.put("success", currentTrx.size());
		response.put("pause", pauseTrx.size());
		response.put("failure", failedTrx.size());
		response.put("minProcessDuration", minDuration);
		response.put("maxProcessDuration", maxDuration);
		response.put("avgProcessDuration", avgDuration);
				
		return response;
	}
	
	
	@GetMapping("/v2/transactions")
	public List<Transaction> getTransaction2() {
				
		Query query = new Query();
				
		query.limit(500);
		
		Sort sort = Sort.by("created_at").descending();
		
		query.with(sort);	
		
		return mongoTemplate.find(query, Transaction.class);
	}
	
	@GetMapping("/v2/transactions/findbytransactionid/{id}")
	public Transaction getTransaction2(@PathVariable String id) {
		
		return transactionRepository2.findByTransactionId(id).orElse(null);
		
	}
	
	@PatchMapping("/v2/transactions/update/status/of/{id}")
	public ResponseEntity updateStatus(@PathVariable String id, @RequestBody HashMap<String, Object> data) throws Exception {
		
		if(!data.containsKey("status") || data.get("status") == null) {			
			throw new Exception("Data incorrect");
		}
		
		Transaction trx = transactionRepository2.findByTransactionId(id).orElseThrow();
		
		trx.setStatus(data.get("status").toString());
		
		transactionRepository2.save(trx);
		
		// kafkaService.pushEvent("TRANSACTION","TransactionStatusUpdate", trx.getTransactionId(), id, Event.EVENT_TYPE);
				
		return new ResponseEntity<>(trx, HttpStatus.NO_CONTENT);
		
	}
	
	@GetMapping("/v2/transactions/operator/{alias}")
	public List<Transaction> getOperatorTransaction(@PathVariable String alias){
				
		List<Transaction> trxList = transactionRepository2.findByOperatorOrderByCreatedAtDesc(alias);
		
		return trxList;
		
	}
	
}
