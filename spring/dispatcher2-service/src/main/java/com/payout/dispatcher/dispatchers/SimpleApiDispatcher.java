/**
 * @author Jean-Marc Dje Bi
 * @since 28-07-2021
 * @version 1
 */

package com.payout.dispatcher.dispatchers;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payout.dispatcher.controller.ListenerController;
import com.payout.dispatcher.eventlog.entity.Event;
import com.payout.dispatcher.kafka.KafkaService;
import com.payout.dispatcher.operators.Operator;
import com.payout.dispatcher.operators.OperatorRepository;
import com.payout.dispatcher.operators.Sim;
import com.payout.dispatcher.transactions.TransactionRequest;
import com.payout.dispatcher.transactions.Transaction;

/**
 * Classe de gestion de la repartition des transactions entre les services de transfert
 */
@Component
public class SimpleApiDispatcher {
	
	/**
	 * Indique le traitement de pour envoie a un service d'API a ete effectue
	 */
	private boolean isPassedByAPI;

	/**
	 * Indique le traitement de pour envoie a un service de SIM a ete effectue
	 */
	private boolean isPassedBySIM;
	
	/**
	 * Reference de la transacion en cours de traitement
	 */
	private String trxRef;
			
	/**
	 * Tampon du dernier evenement
	 */
	private Event event;
	
	@Autowired
	protected OperatorRepository operatorRepository;
	
	@Autowired
	KafkaService kafkaService;
	
	Logger logger = LoggerFactory.getLogger(ListenerController.class);
	
	/**
	 * Methode statique pour extraire la transaction de la requete de transaction
	 */
	public static Transaction extractTransaction(String message) throws JsonMappingException, JsonProcessingException {
		
		TransactionRequest trxRequest = new ObjectMapper().readValue(message, TransactionRequest.class);
		
		Transaction trx = trxRequest.getBody().getTransaction();
		
		return trx;
		
	}
	
	/**
	 * Initialise le traitement
	 */
	public void init() {
		
		isPassedByAPI = false;
		
		isPassedBySIM = false;
		
		trxRef = null;
	}
	
	public void useApi() {
		
		isPassedByAPI = true;
		
	}
	
	public void useSim() {
		
		isPassedBySIM = true;
		
	}
	
	/**
	 * Lance le dispatch. Il s'agit dans un premier d'effectuer quelque verification avant de demarrer le dispatch
	 * @param trx
	 * @throws JsonProcessingException
	 */
	public void dispatch(Transaction trx) throws JsonProcessingException {
		
		init();
		
		this.trxRef = trx.getTransactionId();
			
		kafkaService.pushLogEvent("TRANSACTION","TransactionDispatchStarted",trxRef, Event.INFO);

		Optional<Operator> operatorOptional = operatorRepository.findByName(trx.getOperator());
				
		if(operatorOptional.isEmpty() == false){
			
			kafkaService.pushLogEvent("TRANSACTION","TransactionOperatorSelected",trxRef, Event.INFO);
			
			startDispatch(trx, operatorOptional.get());
			
		}else{
			
			HashMap<String, Object> map =  new HashMap<String, Object>();
			
			map.put("error", "OperatorNotFound");
			
			map.put("errorValue",trx.getOperator());
			
			kafkaService.sendTransactionProcessFailed(trxRef, map);
			
			logger.warn(String.format("Trx processing failed because transaction operator is not found (%s): %s",trx.getOperator(),trx.getTransactionId()));

			endProcess();
			
		}
		
	}

	/**
	 * Execute le dispatch
	 * @param trx
	 * @param operator
	 * @throws JsonProcessingException
	 */
	public void startDispatch(Transaction trx, Operator operator) throws JsonProcessingException {
		
		kafkaService.pushLogEvent("TRANSACTION","TransactionSelectDispatchMethod",trxRef, Event.INFO);

		if(operator.getResourcesCode().equals(RX_CODE.API)) {
			
			apiMethod(trx, operator);

		}else if(operator.getResourcesCode().equals(RX_CODE.API_SIM)){
			
			changeTransfertMethod(trx, operator);
			
		}else {
			
			HashMap<String, Object> map =  new HashMap<String, Object>();
			
			map.put("error", "ResourceCodeUnknow");
			
			map.put("errorValue",operator.getResourcesCode());
			
			kafkaService.sendTransactionProcessFailed(trxRef, map);
			
			logger.warn(String.format("Trx processing failed because resource code operator is unknow (%s): %s",operator.getResourcesCode(), trxRef));
			
			endProcess();
			
		}
		
	}
	
	/**
	 * Represente le dispatch par la methode de l'API
	 * @param trx
	 * @param operator
	 * @throws JsonProcessingException
	 */
	public void apiMethod(Transaction trx, Operator operator) throws JsonProcessingException {
		
		useApi();
		
		kafkaService.pushLogEvent("TRANSACTION","TransactionApiMethodSelected",trxRef, Event.INFO);
		
		kafkaService.pushLogEvent("TRANSACTION","TransactionCheckOperatorAPIBalance",trxRef, Event.INFO);
		
		if(operator.getApi() != null) {
			
			if(operatorApiBalanceIsEnough(trx, operator)) {
				
				kafkaService.pushLogEvent("TRANSACTION","TransactionOperatorApiBalanceIsEnough",trxRef,Event.INFO);
						
				TransactionRequest trxRequest = new TransactionRequest();
				
				trxRequest.getBody().setTransaction(trx);
				
				try {
					
					kafkaService.sendTransactionRequestToTransfertService(trxRequest,operator.getTopicApi());
									
					kafkaService.pushEvent("TRANSACTION","TransactionSentToTransfertService",trxRef, Event.EVENT_TYPE, Event.INFO);

					logger.info(String.format("Trx sent to transfert service: %s",operator.getResourcesCode(), trxRef));
						
					// A retirer apres les tests
					kafkaService.sendTransactionProcessFailed(trxRef, null);	
					
					endProcess();

				} catch (JsonProcessingException e) {
					
					endProcess();

					e.printStackTrace();
					
					throw e;
					
				}					
				
			}else {
				
				kafkaService.pushEvent("TRANSACTION","TransactionOperatorApiBalanceIsNotEnough",trxRef, Event.LOG_TYPE, Event.WARNING);
				
				HashMap<String, Object> map =  new HashMap<String, Object>();			
				
				map.put("error", "TransactionOperatorApiBalanceIsNotEnough");
				
				map.put("operatorCode", operator.getName());
				
				map.put("operatorBalance",operator.getApi().getBalance());
				
				if(operator.getResourcesCode().equals(RX_CODE.API_SIM)) {
					
					changeTransfertMethod(trx, operator);
					
				}else {
					
					kafkaService.sendTransactionProcessFailed(trxRef, map);		
					
					logger.warn(String.format("Trx processing failed because balance is not enougth (%s < %s [api]): %s",trx.getAmount(), operator.getApi().getBalance(), trxRef));
					
					endProcess();
					
				}
			}
			
		}else {
			
			kafkaService.pushLogEvent("TRANSACTION","TransactionOperatorApiConfigsNotFound",trxRef, Event.INFO);
			
			HashMap<String, Object> map =  new HashMap<String, Object>();
			
			map.put("error", operator.getName());			
			
			logger.warn(String.format("Trx processing failed because operator api config is not found (%s): %s",operator.getName(), trxRef));
			
			changeTransfertMethod(trx, operator);
			
		}
		
	}

	/**
	 * Represente le dispatch par la methode
	 * @param trx
	 * @param operator
	 * @throws JsonProcessingException
	 */
	public void simMethod(Transaction trx, Operator operator) throws JsonProcessingException {
		
		useSim();
		
		kafkaService.pushLogEvent("TRANSACTION","TransactionSimMethodSelected",trxRef, Event.INFO);
		
		kafkaService.pushLogEvent("TRANSACTION","TransactionCheckOperatorSIMBalance",trxRef, Event.INFO);
		
		if(operator.getSims() != null) {
			
			if(operatorSimsBalanceIsEnough(trx, operator)) {
				
				kafkaService.pushLogEvent("TRANSACTION","TransactionOperatorSimsBalanceIsEnough",trxRef,Event.INFO);
				
				kafkaService.pushEvent("TRANSACTION","TransactionSentToSimTransfertService",trxRef, Event.EVENT_TYPE, Event.INFO);
				
				kafkaService.pushLogEvent("TRANSACTION","TransactionChangeTransfertNotOtherResourceFound",trxRef, Event.INFO);

				HashMap<String, Object> map =  new HashMap<String, Object>();		
				
				map.put("error", "NoSimsServiceFound");
				
				map.put("operatorCode", operator.getName());
				
				logger.warn(String.format("no sims service found (%s): %s",operator.getName(), trxRef));
								
				kafkaService.sendTransactionProcessFailed(trxRef, map);

			}else {
				
				kafkaService.pushEvent("TRANSACTION","TransactionOperatorSimsBalanceIsNotEnough",trxRef, Event.LOG_TYPE, Event.WARNING);
				
				HashMap<String, Object> map =  new HashMap<String, Object>();	
				
				map.put("error", "TransactionOperatorSimsBalanceIsNotEnough");
				
				map.put("operatorCode", operator.getName());
				
				map.put("operatorBalance","SIM_BALANCE");
				
				logger.warn(String.format("Trx processing failed because balance is not enougth (%s < %s [sim]): %s",trx.getAmount(), "NoSim", trxRef));
				
				kafkaService.sendTransactionProcessFailed(trxRef, map);	
				
				changeTransfertMethod(trx, operator);
				
			}
			
		}else {
			
			HashMap<String, Object> map =  new HashMap<String, Object>();
			
			map.put("error", "TransactionOperatorSimsConfigsNotFound");
			
			logger.warn(String.format("Trx processing failed because operator sim config is not found (%s): %s",operator.getName(), trxRef));
			
			changeTransfertMethod(trx, operator);
		}
				
	}
	
	
	/**
	 * Opere le changement de methode de transfert si possible
	 */
	public void changeTransfertMethod(Transaction trx, Operator operator) throws JsonProcessingException {
		
		kafkaService.pushLogEvent("TRANSACTION","TransactionChangeTransfertMethod",trxRef, Event.INFO);
		
		if(!operator.getResourcesCode().equals(RX_CODE.API_SIM)) {
			
			kafkaService.pushLogEvent("TRANSACTION","TransactionChangeTransfertNotOtherResourceFound",trxRef, Event.INFO);
			
			kafkaService.sendTransactionProcessFailed(trxRef, null);
			
			logger.warn(String.format("Trx processing failed because operator can'nt use any transfert method see transaction logs for more details (%s): %s",operator.getName(), trxRef));
			
			endProcess();

		}else {
			
			if(this.isPassedByAPI == false || this.isPassedBySIM == false) {
				if(this.isPassedByAPI == false) {
					
					apiMethod(trx, operator);
					
				}else if(this.isPassedBySIM == false) {
										
					simMethod(trx, operator);

				}						
			}else {
				
				kafkaService.pushEvent("TRANSACTION","TransactionOperatorNoResourceBalanceAvailable",trxRef, Event.EVENT_TYPE, Event.INFO);

				kafkaService.sendTransactionProcessFailed(trxRef, null);
				
				endProcess();

			}
			
		}
				
	}
	
	/**
	 * Evalue le solde de l'API l'operateur
	 * @return boolean indiquant si le solde de l'api est suffisant pour traiter la transaction
	 */
	private boolean operatorApiBalanceIsEnough(Transaction trx, Operator operator) {
		
		if(trx.getAmount() < operator.getApi().getBalance()) {
			return  true;
		}else {
			return false;
		}
		
	}
	
	/**
	 * Evalue le solde de la SIM l'operateur
	 * @return boolean indiquant si le solde de l'une des SIM est suffisant pour traiter la transaction
	 */
	private boolean operatorSimsBalanceIsEnough(Transaction trx, Operator operator) {
		
		List<Sim> sims = operator.getSims();
		
		int simFound = 0;
		
		for(Sim sim: sims) {	
			
			if(trx.getAmount() < sim.getBalance() ) {
				simFound += 1;
			}	
		}
		
		return simFound > 0 ? true : false;
		
	}
	
	public void endProcess() {
		
		kafkaService.pushEvent("TRANSACTION","TransactionProcessEnded",trxRef, Event.EVENT_TYPE, Event.INFO);
		
	}

	
}