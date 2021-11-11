/**
 * @author Jean-Marc Dje
 * @since 30-07-2021
 * @version 1
 */

package com.payout.psg.events;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.payout.psg.eventlog.entity.Event;
import com.payout.psg.kafka.KafkaService;
import com.payout.psg.transactions.Transaction;

/**
 * Classe representant un evenement de transaction. Contrairement a un evenement classique,
 * on ne precise pas le type de l'entite qui est a TRANSACTION. Aussi, au lieu d'envoyer directement la reference,
 * on en l'instance de la transaction. La reference sera recupere lors de l'intanciation de l'evenement
 * 
 */
public class EventTransaction extends Event{
	
	/**
	 * @see KafkaService
	 */
	@Autowired 
	private KafkaService kafkaService;
	
	/**
	 * Cree des evenements de transaction. Tient du fait que trx peut etre nul en considerant la reference
	 * @param action
	 * @param trx
	 * @param strictType
	 * @param loggingLevel
	 * @return Instance d'un evenement de transaction
	 */
	public static EventTransaction make(String action, Transaction trx, String strictType, String loggingLevel) {
		
		String ref = null;
		
		if(trx != null)
			ref = trx.getTransactionId();
		
		return new EventTransaction(action, ref, strictType, loggingLevel);
	}
	
	
	
	private EventTransaction(String action, String ref, String strictType, String loggingLevel){
				
		super("TRANSACTION", action, ref, strictType, loggingLevel);
		
	}
	
	/**
	 * Constructeur de copie
	 * @param ex
	 */
	public EventTransaction(EventTransaction ex){
		
		super(ex);
		
	}
	
	/**
	 * Envoie l'evenement avec comme reference l'identifiant de la transaction
	 * @param trx
	 */
	public void pushForEntity(Transaction trx) {
		this.setEntityRef(trx.getTransactionId());
		kafkaService.sendEvent(this);
	}
	
	public void put(String key, Object value) {
		
		this.getEventData().getMap().put(key, value);
	}
	
	
}
