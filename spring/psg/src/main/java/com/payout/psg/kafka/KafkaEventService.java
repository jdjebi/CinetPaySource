/**
 * @author Jean-Marc Dje Bi
 * @since 30-07-2021
 * @version 1
 */

package com.payout.psg.kafka;

import java.util.HashMap;

import org.springframework.stereotype.Component;

import com.payout.psg.eventlog.entity.Event;
import com.payout.psg.events.EventTransaction;
import com.payout.psg.transactions.Transaction;

@Component("kafkaEventService")
public class KafkaEventService extends KafkaService{
	
	/**
	 * Emet un evenement d'echec dans un processus de traitement d'une transaction
	 * @param trx
	 * @param data
	 */
	public void emitProcessFailed(Transaction trx, HashMap<String, Object> data) {
		this.sendTransactionProcessFailed(trx.getTransactionId(), data);
	}
	
	/**
	 * Envoie l'evenement avec comme reference l'identifiant de la transaction
	 * @param trx
	 */
	public void pushForEntity(Event event, Transaction trx) {
		event.setEntityRef(trx.getTransactionId());
		this.sendEvent(event);
	}
	
	/**
	 * Emet un evenement de transaction de type evenement et de niveau INFO
	 * @param action
	 * @param trx
	 */
	public void emit(String action, Transaction trx) {
		
		String strictType = Event.EVENT_TYPE;
		String loggingLevel = Event.INFO;
		EventTransaction ex = EventTransaction.make(action, trx, strictType, loggingLevel);
		this.sendEvent(ex);
		
	}
	
	/**
	 * Emet un evenement de transaction de type evenement
	 * @param action
	 * @param trx
	 * @param loggingLevel
	 */
	public void emit(String action, Transaction trx, String loggingLevel) {
		
		String strictType = Event.EVENT_TYPE;
		EventTransaction ex = EventTransaction.make(action, trx, strictType, loggingLevel);
		this.sendEvent(ex);
		
	}
	
	/**
	 * Emet un evenement de transaction
	 * @param action
	 * @param trx
	 * @param strictType
	 * @param loggingLevel
	 */
	public void emit(String action, Transaction trx, String strictType, String loggingLevel) {
		
		EventTransaction ex = EventTransaction.make(action, trx, strictType, loggingLevel);
		this.sendEvent(ex);
		
	}
	
	/**
	 * Emet un evenement de transaction avec des donnees
	 * @param action
	 * @param trx
	 * @param strictType
	 * @param loggingLevel
	 * @param data
	 */
	public void emit(String action, Transaction trx, String strictType, String loggingLevel, HashMap<String, Object> data) {
		
		EventTransaction ex = EventTransaction.make(action, trx, strictType, loggingLevel);
		ex.getEventData().setMap(data);
		this.sendEvent(ex);
		
	}
	
	/**
	 * Emet un evenement de transaction de type log
	 * @param action
	 * @param trx
	 * @param loggingLevel
	 */
	public void log(String action, Transaction trx, String loggingLevel) {
		
		String strictType = Event.LOG_TYPE;
		EventTransaction ex = EventTransaction.make(action, trx, strictType, loggingLevel);
		this.sendEvent(ex);
		
	}
	
	/**
	 * Emet un evenement de transaction de type log et de niveau LOG
	 * @param action
	 * @param trx
	 */
	public void log(String action, Transaction trx) {
		
		String strictType = Event.LOG_TYPE;
		String loggingLevel = Event.INFO;
		EventTransaction ex = EventTransaction.make(action, trx, strictType, loggingLevel);
		this.sendEvent(ex);
		
	}

}
