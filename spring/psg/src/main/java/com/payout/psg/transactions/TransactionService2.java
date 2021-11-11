/**
 * @author Jean-Marc Dje Bi
 * @since 13-08-2021
 * @version 1.2
 */
package com.payout.psg.transactions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.payout.psg.eventlog.entity.Event;
import com.payout.psg.events.EventTransaction;
import com.payout.psg.model.Operator;
import com.payout.psg.repository.OperatorRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe representant le traitement des transactions après reception.
 * L'objectif est d'enregistrer les transactions et de demarrer le processus de traitement des transactions
 */
@Component
public class TransactionService2 {
	
	/**
	 * @see com.payout.psg.kafka.KafkaService
	 */
	@Autowired 
	private TransactionProcessComponentsProxy trxComponentsProxy;
	
    Logger logger = LoggerFactory.getLogger(TransactionService2.class);
    
    /**
     * Taille d'une partition de transactions
     */
    Integer partitionFactor = 100; 
    
	/**
	 * Traite les transactions en creant des lots de taille egale au facteur de partitionnement. Chaque partition des traitee dans un thread. Chaque est ensuite traite dans un thread. 
	 * La methode retourne une liste contenant les statuts d'acception des transactions pour traitement 
	 * @param trxList
	 * @return List<TransactionReceiveStatus>
	 * @throws JsonProcessingException
	 * @throws InterruptedException
	 */
	public List<TransactionReceiveStatus> receive(List<Transaction> trxList) throws JsonProcessingException, InterruptedException{		
		
		// Liste des partitions
		List<List<Transaction>> batchs = null; 
		
		// Liste des threads
		List<TransactionReceiverBatchProcessorThread2> threads = new ArrayList<TransactionReceiverBatchProcessorThread2>();
		
		// Liste des status d'acception
		List<TransactionReceiveStatus> trxReceiveStatusList = new ArrayList<TransactionReceiveStatus>();
				
		// Creation de l'evenement de reception: Cet evenement est le meme pour toute les transactions
		EventTransaction event = EventTransaction.make("TransactionReceived",null,Event.EVENT_TYPE,Event.INFO);
		
		logger.info("Start list partionned: list of " + trxList.size() + " elements");
		

		/* Partitionnement de la liste des transactions
		 * Le partitionnement utilise la methode de paritionnenent fournie par Apache Common Collecion.
		 * Il s'agit d'une methode partitionnement hautement performante pour les grands volume de donne
		 */
		batchs = ListUtils.partition(trxList, partitionFactor);
						
		logger.info("Partionned: finshed");
		
		logger.info("Process starting batch");
						
		// Demarrage du traitement des lots
		
		Map<String, List<Operator>> operatorMap = getOperatorsMap();
		
		for(List<Transaction> batch: batchs) {
			
			// Creation du traitement du lot
			TransactionReceiverBatchProcessorThread2 transactionReceiverThread 
				= new TransactionReceiverBatchProcessorThread2(
						batch, trxComponentsProxy, trxReceiveStatusList, event, operatorMap
					);
			
			// Ajout du thread a la liste des threads
			threads.add(transactionReceiverThread);
						
			// Demarrage du thread
			transactionReceiverThread.start();	
			
		}
		
		// Nombre de threads lances
		int threadCounter = threads.size();
		
		// Nombre de threads termines
		int nbrThreadTerminated = 0;
			
		// On ne continue pas tant que tous les theads ne sont pas termines
		
		do {
			
			nbrThreadTerminated = 0;
			
			for(Thread thread: threads) {
				
				if(thread.getState().toString().equals("TERMINATED")) {
										
					nbrThreadTerminated++;
					
				}
				
			}
						
		}while(nbrThreadTerminated != threadCounter);
		
		logger.info("process ending batch");
		
		// On recupere les status des transactions produit par chaque threads
		
		threads.forEach(t -> {
			
			trxReceiveStatusList.addAll(t.trxReceiveStatusList);
			
		});
						
		return trxReceiveStatusList; // Retourne la liste des statuts
	}
	
	/**
	 * Retourne une map contenant les operateurs accessible par leur alias
	 * @return Map<String, List<Operator>>
	 */
	public Map<String, List<Operator>> getOperatorsMap() {
		  
		OperatorRepository operatorRepo = trxComponentsProxy.getOperatorRepository();   
		    
		return operatorRepo.findAll().stream().collect(Collectors.groupingBy(Operator::getAlias));
		
	}
	
}
