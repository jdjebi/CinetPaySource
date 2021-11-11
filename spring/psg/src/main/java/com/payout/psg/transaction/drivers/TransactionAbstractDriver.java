package com.payout.psg.transaction.drivers;

import org.json.JSONArray;
import org.json.JSONObject;

public class TransactionAbstractDriver {
	
	protected JSONObject trxPackMetaJson;
	protected JSONArray trxListJson;
	
	public TransactionAbstractDriver(JSONObject trxPackMetaJson, JSONArray trxListJson) {
		this.trxPackMetaJson = trxPackMetaJson;
		this.trxListJson = trxListJson;
	}

	public void save() {
	}
}
