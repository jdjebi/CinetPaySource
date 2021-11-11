package com.payout.transfert.unit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.payout.transfert.dao.Resource;
import com.payout.transfert.transactions.Transaction;
import com.payout.transfert.transactions.TransactionRequest;
import com.payout.transfert.unit.basic.DefaultTransfertUnit;
import com.payout.transfert.unit.mtn.cm.MtnCM;
import com.payout.transfert.unit.om.ci.OrangeCI;

@Component
public class TransfertUnitDispatcher {

	@Autowired
	OrangeCI orangeCI;
	
	@Autowired
	MtnCM mtnCM;
	
	@Autowired
	DefaultTransfertUnit defaultTransfertUnit;
	
	public TransfertUnitResponse dispatch(Transaction trx, Resource rx) throws JsonMappingException, JsonProcessingException, InterruptedException {
		
		String operatorAlias = trx.getOperator();
		
		switch(operatorAlias) {
		
			case "OMCI":
				
				return orangeCI.makeTransfert(trx, rx);
				
			case "MTNCM":				
				
				return mtnCM.makeTransfert(trx, rx);
							
			default:
				
				return defaultTransfertUnit.makeTransfert(trx, rx);
				
		}
		
	}
	
}
