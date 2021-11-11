/**
 * @author Jean-Marc Dje Bi
 * @since 20-08-2021
 * @version 1
 */

package com.payout.transfert.unit.mtn.cm;

import java.util.HashMap;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payout.transfert.cache.TokenDataService;
import com.payout.transfert.dao.Resource;
import com.payout.transfert.dao.ResourceToken;
import com.payout.transfert.helpers.HashHelper;
import com.payout.transfert.repository.ResourceTokenRepository;
import com.payout.transfert.transactions.Transaction;
import com.payout.transfert.unit.FinalStatus;
import com.payout.transfert.unit.TransfertUnitResponse;

/**
 * Classe charge d'operer les transferts avec MTN Cameroun
 */
@Component
public class MtnCM {

private RestTemplate restTemplate = new RestTemplate();
	
	Logger logger = LoggerFactory.getLogger(MtnCM.class);
	
	@Autowired
	TokenDataService tokenDataService;
	
	@Autowired
	ResourceTokenRepository resourceTokenRepository;
	
	/**
	 * Opere le transfert
	 * @param trx
	 * @param rx
	 * @return
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 * @throws InterruptedException 
	 */
	public TransfertUnitResponse makeTransfert(Transaction trx, Resource rx) throws JsonMappingException, JsonProcessingException, InterruptedException{
		
		/**
		 * Initialisation
		 */
		HashMap<String,Object> response = new HashMap<String,Object>();
		
		HashMap<String,Object> data = new HashMap<String,Object>();
								
		HashMap<String,Object> operatorAuthResponse = new HashMap<String,Object>();
		
		HashMap<String,Object> operatorTransfertResponse =  new HashMap<String,Object>();
						
		HashMap<String, Object> payee = new HashMap<String,Object>();
		
		String token = null;
		
		Integer lifetime = null;
		
		long expiredAt = 0;
		
		long savedAt = 0;
				
		/**
		 *  Affectation initiale
		 */
		
		// Access
		
		String authUrl = rx.getOperator_api_url() + "/" +  rx.getExtrasData().get("tokenUri");

		String transferUrl = rx.getOperator_api_url() + "/" +  rx.getExtrasData().get("transfer");
		
		String authorization = rx.getExtrasData().get("authorization");
		
		String subscriptionKey = rx.getExtrasData().get("subscriptionKey");
		
		String environment = rx.getExtrasData().get("targetEnvironment");
								
		// API usefull data
		
		String externalId = trx.getClientTransactionId();
				
		String referenceId = UUID.nameUUIDFromBytes(trx.getTransactionId().getBytes()).toString();
				
		// Initial process data
		
		response.put("authError", false);

		response.put("processError", false);
		
		response.put("getFinalStatusError", false);
		
		/**
		 * Cache du token
		 */
		
		ResourceToken rxToken = resourceTokenRepository.findByOperatorCode(rx.getOperatorCode());
		
		if(rxToken == null) {
			
			logger.info("New resource token entry for operator: " + rx.getOperatorCode());
			
			rxToken = new ResourceToken();
			
			rxToken.setOperatorCode(rx.getOperatorCode());
			
			rxToken = resourceTokenRepository.save(rxToken);
		}
				
		long currentTimestamp = System.currentTimeMillis();
		
		try{
			
			// Verifier l'existence du token dans le cache si oui on le genere sinon on l'utilise
			
			/**
			 * Authentification
			 */
			
			logger.info("Api timestamp expire: " + rxToken.getExpiredAt() + "; current timestamp: " + currentTimestamp);
			
			logger.info(""+(rxToken.getExpiredAt() - currentTimestamp));
			
			if(currentTimestamp >= rxToken.getExpiredAt()){
				
				logger.info("Generate new token by new authentication");
						
				try {
					
					operatorAuthResponse = this.authenticate(trx, authUrl, authorization, subscriptionKey);
																	
					response.put("operatorAuthResponse", operatorAuthResponse);
					
					logger.info("Authenticate success");
										
				 	// Recuperation des informations du  token de l'API
										
					token = (String) operatorAuthResponse.get("access_token");
					
					lifetime = (Integer) operatorAuthResponse.get("expires_in"); // Duree de vie du token en seconde
					
					rxToken.setToken(token);
					
					savedAt = System.currentTimeMillis();
					
					expiredAt = savedAt + (lifetime - 30) * 1000;
					
					rxToken.setSavedAt(savedAt);
					
					rxToken.setExpiredAt(expiredAt);
										
					resourceTokenRepository.save(rxToken);
											
				} catch(HttpStatusCodeException e) {
					
					logger.error("Authentication failed because: " + e.getMessage());
					
					int statusCode = e.getRawStatusCode();
											
					response.put("processError", true);
					
					response.put("statusCode", statusCode);
					
					response.put("authError", true);
					
					response.put("operatorAuthResponse", this.makeErrorResponseFromException(e));
					
					response.put("operatorError", this.makeErrorResponseFromException(e));
								
					return this.buildFinalResponse(response, rx);
					
				}
				
			}else {
				
				token = rxToken.getToken();
				
				logger.info("Using of cache api token");
				
			}									
			
			/**
			 * Envoie de la transaction
			 */
			
			try{
				
				// Preparation des donnees de transfert
				
				data = new HashMap<String, Object>();
				
				data.put("amount", trx.getAmount());

				data.put("currency", trx.getCurrency());
				
				data.put("externalId", externalId);
				
				data.put("payerMessage", "CinetPay");
				
				data.put("payeeNote", "Transfer from CinetPay");
				
				//Payee 
				
				payee.put("partyIdType", "MSISDN");
				
				payee.put("partyId", trx.getPhone());

				data.put("payee", payee);		
									
				// Envoie de la transaction
				
				operatorTransfertResponse = this.sendTransaction(trx, referenceId, transferUrl, data, token, authorization, subscriptionKey, environment);
				
				// Reception de la reponse de l'operateur
				
				response.put("operatorTransfertResponse", operatorTransfertResponse);
				
				response.put("referenceId", referenceId);
				
				logger.info(String.format("Transaction %s sent to operator with success",referenceId));
														
			}catch(HttpStatusCodeException e) {
							
				logger.error("Process failed because:" + e.getMessage());
				
				int statusCode = e.getRawStatusCode();
				
				response.put("processError", true);
				
				response.put("sendingTransactionError", true);
				
				response.put("statusCode", statusCode);
								
				response.put("operatorTransfertError", this.makeErrorResponseFromException(e));
				
				response.put("operatorError", this.makeErrorResponseFromException(e));
							
				return this.buildFinalResponse(response, rx);
			}
			
			
			// Appel periode de l'API jusqu'a 1 minute max avant stockage de transaction

			String status = null;
			
			long currentTimeMillis = System.currentTimeMillis(); // Timestamp de debut
			
			long endingGetTransactionTime = currentTimeMillis + (60 * 1000); // Timestamp de fin
			
			try {
								
				do {
										
					operatorTransfertResponse = this.getTransaction(trx, referenceId, transferUrl, token, authorization, subscriptionKey, environment);
					
					status = (String) operatorTransfertResponse.get("status");
										
					if(!status.equals("PENDING")) {
						break;
					}
					
					System.out.println(status);
					
					Thread.sleep(300);

					currentTimeMillis = System.currentTimeMillis();
					
				}while(currentTimeMillis < endingGetTransactionTime);
				
				response.put("operatorTransfertResponse", operatorTransfertResponse);
												
			} catch(HttpStatusCodeException e) {
				
				logger.error("Process failed because:" + e.getMessage());
				
				response.put("processError", true);
				
				response.put("getFinalStatusError", true);
				
				response.put("operatorGetFinalStatusError", this.makeErrorResponseFromException(e));
				
				response.put("operatorError", this.makeErrorResponseFromException(e));
							
				return this.buildFinalResponse(response, rx);
				
			}
			
		} catch(ResourceAccessException e) {
			
			throw e;
			
		}
						
		return this.buildFinalResponse(response, rx);
		
	}
	
	/**
	 * Construit la reponse final du transfert
	 * @param response
	 * @return
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	public TransfertUnitResponse buildFinalResponse(HashMap<String, Object> response, Resource rx) throws JsonMappingException, JsonProcessingException {
		
		System.out.println(HashHelper.getObjectHashMapString(response));
		
		// Initialisation
				
		TransfertUnitResponse transfertUnitResponse = null;
		
		String finalStatusStr = null;
		
		String status = null;
		
		String errorMessage = null;
		
		String operatorResponseStr = null;
		
		HashMap<String, Object> operatorTransfertError = null;
		
		HashMap<String, Object> operatorResponse = null;
		
		HashMap<String, Object> operatorError = null;
		
		// Affectation
				
		String referenceId = (String) response.get("referenceId");
		
		HashMap<String, Object> operatorTransfertResponse = (HashMap<String, Object>) response.get("operatorTransfertResponse");
		
		Integer statusCode = (Integer) response.get("statusCode");
				
		Boolean processError = (Boolean) response.get("processError");
		
		Boolean isGetFinalStatusError = (Boolean) response.get("getFinalStatusError");
		
		FinalStatus finalStatus = new FinalStatus();

		finalStatus.setOperatorAlias(rx.getOperatorCode());
		
		finalStatus.setOperatorResource(rx.getName());
		
		finalStatus.setResourceStatus(true);
		
		if(processError == true) {
			
			// Preparation
									
			operatorError = (HashMap<String, Object>) response.get("operatorError");
			
			operatorResponseStr = (String) operatorError.get("operatorResponse");
			
			operatorResponse = new ObjectMapper().readValue(operatorResponseStr, HashMap.class);
			
			operatorError.replace("operatorResponse",operatorResponse);
			
			errorMessage = (String) operatorResponse.get("message");
			
			// Gestion des cas d'erreurs generales
			
			if(statusCode == 403) {
				
				finalStatus.setComment("operatorApiQuotaLimit");
				
				finalStatus.setOperatorComment(errorMessage);
				
				logger.error("Out of call volume quota");
								
				if(isGetFinalStatusError == false) { // La transaction n'a pas pu etre envoyée a l'operateur
										
					finalStatus.setStatus("PAUSE");
					
				}else { // Le statut de la transaction n'a pas put etre recupere
										
					finalStatus.setStatus("FAILURE");
					
				}
				
			}else if(statusCode == 429) {
						
				finalStatus.setComment("operatorApiRateLimit");
				
				finalStatus.setOperatorComment(errorMessage);
								
				if(isGetFinalStatusError == false) { // La transaction n'a pas pu etre envoyée a l'operateur
					
					logger.error("Rate limit problem during sending to operator");
					
					finalStatus.setStatus("PAUSE");
					
				}else { // Le statut de la transaction n'a pas put etre recupere
					
					logger.error("Rate limit problem during getting transaction");
					
					finalStatus.setStatus("FAILURE");
					
				}
				
			}else { // Gestion des cas d'erreurs specifiques
				
				System.out.println("Gestion des cas d'erreur d'authentification");
				
				System.out.println("Gestion des cas d'erreur de transfert");
				
				System.out.println("Gestion des cas d'erreur des cas dans recuperation du statut de transaction");
				
				return null;
				
			}
			
		}else {
										
			status = (String) operatorTransfertResponse.get("status");
			
			finalStatus.setOperatorComment(referenceId);

			switch(status) {
			
				case "SUCCESSFUL":
					
					finalStatusStr = "SUCCESS";
					
					finalStatus.setComment("TransactionSuccess");
					
					break;
					
				case "FAILED":
					
					finalStatusStr = "FAILURE";
					
					finalStatus.setComment("TransactionFailed");
					
					break;
				
				case "PENDING":
					
					finalStatusStr = "NOS";
					
					finalStatus.setComment("TransactionInNos");
					
					break;
				
				default:
								
					finalStatusStr = "FAILURE";
						
			}
			
			finalStatus.setStatus(finalStatusStr);
					
			finalStatus.setOperatorComment((String) operatorTransfertResponse.get("payerMessage"));
			
			finalStatus.setOperatorTransactionId(referenceId);
						
		}
		
		// Retire l'erreur operateur pour reduire
		
		response.replace("operatorError",null);
		
		finalStatus.setBigdata(operatorTransfertResponse);
		
		transfertUnitResponse = new TransfertUnitResponse(finalStatus);
		
		return transfertUnitResponse;

	}

	/**
	 * Genere un token d'acces a l'api
	 * @param authUrl
	 * @param authorization
	 * @param subscriptionKey
	 * @return
	 */
	public HashMap<String, Object> authenticate(Transaction trx, String authUrl, String authorization, String subscriptionKey) {
		
		// En-tete
		
		HttpHeaders headers = new HttpHeaders();
		
		headers.set("Authorization", authorization);
		
		headers.set("Ocp-Apim-Subscription-Key", subscriptionKey);
				
		// Authentification - Recuperation du token d'acces
				
		logger.info(String.format("Authenticating to %s API on url %s", trx.getOperator(),authUrl));
		
		HttpEntity<HashMap<String, Object>> request = new HttpEntity<HashMap<String, Object>>(null,headers);
		
		ResponseEntity<HashMap> response = restTemplate.exchange(authUrl, HttpMethod.POST, request, HashMap.class);
			
		return response.getBody();
	}
	
	/**
	 * Envoie la transaction
	 * @param trx
	 * @param referenceId
	 * @param transferUrl
	 * @param data
	 * @param token
	 * @param authorization
	 * @param subscriptionKey
	 * @param environment
	 * @return
	 */
	public HashMap<String,Object> sendTransaction(Transaction trx, String referenceId, String transferUrl, HashMap<String, Object> data, String token, String authorization, String subscriptionKey, String environment) {
						
		// En-tetes
		
		HttpHeaders headers = new HttpHeaders();
		
		headers.setBearerAuth(token);
				
		headers.set("Ocp-Apim-Subscription-Key", subscriptionKey);

		headers.set("X-Target-Environment", environment);
				
		headers.set("X-Reference-Id", referenceId);
		// Requete
		
		logger.info("Transfert Data: " + data);
		
		logger.info(String.format("transfert url %s used with reference id of %s", transferUrl, referenceId));
		
		HttpEntity<HashMap<String, Object>> request = new HttpEntity<HashMap<String, Object>>(data,headers);
		
		ResponseEntity<HashMap> response = restTemplate.exchange(transferUrl, HttpMethod.POST, request, HashMap.class);
		
		// Reponse
		
		HashMap<String,Object> reponseHash = new HashMap<String,Object>();
		
		reponseHash.put("status", response.getStatusCode());
				
		return reponseHash;
		
	}
	
	/**
	 * Recupere les informations sur la transaction dans but d'extraire le statut final"
	 * @param trx
	 * @param referenceId
	 * @param transferUrl
	 * @param token
	 * @param authorization
	 * @param subscriptionKey
	 * @param environment
	 * @return
	 */
	public HashMap<String,Object> getTransaction(Transaction trx, String referenceId, String transferUrl, String token, String authorization, String subscriptionKey, String environment) {

		// Initialisation
		
		String getTransactionUrl = transferUrl + "/" + referenceId;
		
		// En-tetes
		
		HttpHeaders headers = new HttpHeaders();
		
		headers.setBearerAuth(token);
				
		headers.set("Ocp-Apim-Subscription-Key", subscriptionKey);

		headers.set("X-Target-Environment", environment);
						
		// Requete
		
		logger.info("Get transaction: " + referenceId);
				
		HttpEntity<HashMap<String, Object>> request = new HttpEntity<HashMap<String, Object>>(null,headers);
		
		ResponseEntity<HashMap> response = restTemplate.exchange(getTransactionUrl, HttpMethod.GET, request, HashMap.class);
		
		// Reponse
		
		HashMap<String,Object> reponseHash = response.getBody();
								
		return reponseHash;
		
	}
	
	/**
	 * Construit une reponse d'erreur
	 * @param e
	 * @return
	 */
	public HashMap<String,Object> makeErrorResponseFromException(HttpStatusCodeException e) {
		
		HashMap<String,Object> response = new HashMap<String,Object>();
		
		String body = e.getResponseBodyAsString();

		response.put("statusCode", e.getRawStatusCode());
		response.put("operatorErrorMessage", e.getMessage());
		response.put("operatorLocalizedMessage", e.getLocalizedMessage());
		response.put("operatorResponse", body);	
		
		return response;
	}
	
}
