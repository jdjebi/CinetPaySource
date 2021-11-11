package com.payout.psg.transaction.drivers;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.payout.psg.model.TransactionMongo;
import com.payout.psg.repository.TransactionMongoRepository;

public class TransactionMongoDriver extends TransactionAbstractDriver{
	
	private TransactionMongoRepository transactionMongoRepository;
	
	
	public TransactionMongoDriver(JSONObject trxPackMetaJson, JSONArray trxListJson) {
		super(trxPackMetaJson, trxListJson);
	}
	
	public TransactionMongoDriver setRepo(TransactionMongoRepository transactionMongoRepository) {
		this.transactionMongoRepository = transactionMongoRepository;
		return this;
	}

	public void save() {		
		for(Object trxJson: this.trxListJson) {
    		JSONObject t = (JSONObject) trxJson; 	    		
			TransactionMongo trx = new TransactionMongo();
    		trx.setRemoteId(t.getString("remoteId"));
    		trx.setCountry(t.getString("country"));
    		trx.setPaymentmethod(t.getString("paymentmethod"));
    		trx.setBatchnumber(t.getString("batchnumber"));
    		trx.setAmount(t.getInt("amount"));
    		trx.setPhone(t.getString("phone"));
    		trx.setStatus("PENDING");
    		transactionMongoRepository.save(trx);
    	}
	}
	
	public List<TransactionMongo> saveMongo() {
				
		List<TransactionMongo> trxList = new ArrayList<TransactionMongo>();
		
		for(Object trxJson: this.trxListJson) {
    		JSONObject t = (JSONObject) trxJson; 	    		
			TransactionMongo trx = new TransactionMongo();
    		trx.setRemoteId(t.getString("remoteId"));
    		trx.setCountry(t.getString("country"));
    		trx.setPaymentmethod(t.getString("paymentmethod"));
    		trx.setBatchnumber(t.getString("batchnumber"));
    		trx.setAmount(t.getInt("amount"));
    		trx.setPhone(t.getString("phone"));
    		trx.setStatus("PENDING");	
    		trx.setBatchnumber(t.getString("batchnumber"));
    		trx.setGametrx_id(t.getInt("gametrx_id"));
    		TransactionMongo trxMongo = transactionMongoRepository.save(trx);
    		trxList.add(trxMongo);
    	}
		
		return trxList;
	}
}
