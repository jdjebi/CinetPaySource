/**
 * @author Jean-Marc Dje
 * @since 30-07-2021
 * @version 1
 */
package com.payout.psg.events;

import com.payout.psg.eventlog.entity.Event;
import com.payout.psg.transactions.Transaction;

public class TransactionEventManger extends Event{
	
	/**
	 * @see Transaction
	 */
	private Transaction trx;
	
	public TransactionEventManger(Transaction trx) {
		this.trx = trx;
	}
	
	
}
