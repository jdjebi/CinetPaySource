package com.payout.psg.transaction.drivers;

/*
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.payout.psg.model.TransactionElastic;
import com.payout.psg.model.TransactionMongo;
import com.payout.psg.repository.TransactionElasticRepository;
import com.payout.psg.repository.TransactionMongoRepository;

public class TransactionElasticDriver extends TransactionAbstractDriver{
	
	private TransactionElasticRepository transactionElasticRepository;
	
	public TransactionElasticDriver(JSONObject trxPackMetaJson, JSONArray trxListJson) {
		super(trxPackMetaJson, trxListJson);
	}
	
	public TransactionElasticDriver setRepo(TransactionElasticRepository transactionElasticRepository) {
		this.transactionElasticRepository = transactionElasticRepository;
		return this;
	}

	public void save() {
		for(Object trxJson: this.trxListJson) {
    		JSONObject t = (JSONObject) trxJson; 	    		
			TransactionElastic trx = new TransactionElastic();
    		trx.setRemoteId(t.getString("remoteId"));
    		trx.setCountry(t.getString("country"));
    		trx.setPaymentmethod(t.getString("paymentmethod"));
    		trx.setBatchnumber(t.getString("batchnumber"));
    		trx.setAmount(t.getInt("amount"));
    		trx.setPhone(t.getString("phone"));
    		trx.setStatus("PENDING");
    		trx.setGametrx_id(t.getInt("gametrx_id"));
    		transactionElasticRepository.save(trx);
    	}
	}
}

*/
