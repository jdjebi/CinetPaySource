/**
 * @author Jean-Marc Dje Bi
 * @since 20-08-2021
 * @version 1
 */
package com.payout.transfert.unit.om.ci;

import java.util.HashMap;
import java.util.Map;

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
import com.payout.transfert.repository.ResourceRepository;
import com.payout.transfert.repository.ResourceTokenRepository;
import com.payout.transfert.transactions.Transaction;
import com.payout.transfert.unit.FinalStatus;
import com.payout.transfert.unit.TransfertUnitResponse;

/**
 * Classe charge d'operer les transferts avec Orange Money Cote d'Ivoire
 */
@Component
public class OrangeCI {
	
	private RestTemplate restTemplate = new RestTemplate();
	
	Logger logger = LoggerFactory.getLogger(OrangeCI.class);
	
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
	 */
	public TransfertUnitResponse makeTransfert(Transaction trx, Resource rx) throws JsonMappingException, JsonProcessingException {
		
		HashMap<String,Object> response = new HashMap<String,Object>();
		
		HashMap<String,Object> data = new HashMap<String,Object>();
								
		HashMap<String,Object> operatorAuthResponse = new HashMap<String,Object>();
				
		HashMap<String,Object> token = new HashMap<String,Object>();
				
		String tokenJwt = null;
		
		Integer lifetime = null;
		
		long expiredAt = 0;
		
		long savedAt = 0;
		
		/**
		 *  Initialisation
		 */
				
		String transfertUrl = rx.getOperator_api_url() + "/" +  rx.getExtrasData().get("transfert");
		
		String bucketMsisdn = rx.getExtrasData().get("msisdn");
		
		String customerMsisdn = trx.getPhone();
		
		Integer amount = trx.getAmount();
		
		String externalId = trx.getClientTransactionId();
				
		response.put("processError", false);
		
		response.put("authError", false);
		
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
					
					operatorAuthResponse = this.authenticate(trx, rx);
					
					response.put("operatorAuthResponse", operatorAuthResponse);
					
					logger.info("Authenticate success");
					
					/**
					 *  Recuperation des informations du  token de l'API
					 */
					
					token = (HashMap<String,Object>) operatorAuthResponse.get("token");	
					
					tokenJwt = (String) token.get("jwt");
					
					lifetime = (Integer) token.get("lifetime"); // Duree de vie du token en seconde
					
					rxToken.setToken(tokenJwt);
					
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
								
					return this.buildFinalResponse(response, rx);
					
				}
				
			}else {
				
				tokenJwt = rxToken.getToken();
				
				logger.info("Using of cache api token");
				
			}									
			
			/**
			 * Envoie de la transaction
			 */
			
			try{
				
				// Preparation des donnees de transfert
				
				data = new HashMap<String, Object>();
				
				data.put("bucketMsisdn", bucketMsisdn);
				
				data.put("customerMsisdn", customerMsisdn);
				
				data.put("amount", amount);
				
				data.put("externalId", externalId);
				
				HashMap<String,Object> operatorTransfertResponse = this.sendTransaction(transfertUrl, tokenJwt, data);
				
				response.put("operatorTransfertResponse", operatorTransfertResponse);
														
			}catch(HttpStatusCodeException e) {
							
				logger.error("Process failed because:" + e.getMessage());
				
				response.put("processError", true);
				
				response.put("operatorTransfertError", this.makeErrorResponseFromException(e));
							
			}
		
		} catch(ResourceAccessException e) {
			
			throw e;
			
		}
			
		return this.buildFinalResponse(response, rx);
		
	}

	public HashMap<String, Object> authenticate(Transaction trx, Resource rx) {
		
		// Authentification
				
		HashMap<String,Object> data = new HashMap<String,Object>();
		data.put("username", rx.getEmail());
		data.put("password", rx.getPassword());
		
		String authUrl = rx.getOperator_api_url() + "/" +  rx.getExtrasData().get("authUri");
		
		logger.info(String.format("Authenticating to %s API on url %s", trx.getOperator(),authUrl));
		
		HashMap<String,Object> operatorAuthResponse = restTemplate.postForObject(authUrl, data, HashMap.class);
	
		return operatorAuthResponse;
	}
	
	/**
	 * Envoie la transaction a l'operateur
	 * @param transfertUrl
	 * @param token
	 * @param data
	 * @return
	 */
	public HashMap<String,Object> sendTransaction(String transfertUrl, String token, HashMap<String, Object> data) {
		
		// En-tete
		
		HttpHeaders headers = new HttpHeaders();
		
		headers.setBearerAuth(token);
		
		// Requete
		
		logger.info("Transfert Data: " + data);
		
		logger.info(String.format("transfert url %s using", transfertUrl));
		
		HttpEntity<HashMap<String, Object>> request = new HttpEntity<HashMap<String, Object>>(data,headers);
		
		ResponseEntity<HashMap> response = restTemplate.exchange(transfertUrl, HttpMethod.POST, request, HashMap.class);
		
		// Reponse
				
		return response.getBody();
		
	}
	
	/**
	 * Construit la reponse final du transfert
	 * @param response
	 * @return
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	private TransfertUnitResponse buildFinalResponse(HashMap<String, Object> response,  Resource rx) throws JsonMappingException, JsonProcessingException {
				
		FinalStatus finalStatus = new FinalStatus();
		
		finalStatus.setOperatorAlias(rx.getOperatorCode());
		
		finalStatus.setOperatorResource(rx.getName());
		
		finalStatus.setResourceStatus(true);
		
		Boolean processError = (Boolean) response.get("processError");
						
		if(processError == true) {
			
			Boolean authError = (Boolean) response.get("authError");
			
			if(authError == true) { // L'authentification a echoue, dans ce cas la reponse de l'operateur etre vide
				
				HashMap<String,Object> operatorAuthResponse = (HashMap<String,Object>) response.get("operatorAuthResponse");
				
				Integer statusCode = (Integer) operatorAuthResponse.get("statusCode");
				
				String errorMessage = (String) operatorAuthResponse.get("operatorErrorMessage");
				
				finalStatus.setStatus("PAUSE");

				finalStatus.getData().put("error", true);
				
				finalStatus.getData().put("code", statusCode);
				
				finalStatus.setOperatorData(operatorAuthResponse);
				
				finalStatus.setResourceStatus(false);
								
				if(statusCode == 404) {
					
					logger.error("Operator API not found on url " + rx.getOperator_api_url());
					
					finalStatus.setComment("OperatorApiNotAvailable");
					finalStatus.setOperatorComment("[System] Operator API not found on url " + rx.getOperator_api_url());
					finalStatus.getData().put("message", "Operator API not found on url " + rx.getOperator_api_url());
					finalStatus.getData().put("messageCode", "api_not_found");
										
				}else if(statusCode == 401) {
					
					logger.error("Operateur: Le nom d'utilisateur ou le mot de passe sont incorrects:" + operatorAuthResponse.get("operatorErrorMessage"));
					
					finalStatus.setComment("ApiAccessNotAuthorize");
					finalStatus.setOperatorComment("[System] Unauthorized access");
					finalStatus.getData().put("message", "Le nom d'utilisateur ou le mot de passe sont incorrects");
					finalStatus.getData().put("messageCode", "unauthorized_access");
					
				}else if(statusCode == 500) {
					
					logger.error("Operateur: Erreur interne du serveur:" + operatorAuthResponse.get("operatorErrorMessage"));
					
					finalStatus.setComment("OperatorServerError");
					finalStatus.setOperatorComment("[System] Operator server error");
					finalStatus.getData().put("message", operatorAuthResponse.get("operatorErrorMessage"));
					finalStatus.getData().put("messageCode", "Internal_error");
					
				}else if(statusCode == 400) {
					
					logger.error("Operateur: Erreur 400: " + operatorAuthResponse.get("operatorErrorMessage"));

					finalStatus.setComment("OperatorRespondWithBadRequest");
					finalStatus.setOperatorComment("[System] Server respond with bad request");
					finalStatus.getData().put("message", operatorAuthResponse.get("operatorErrorMessage"));
					finalStatus.getData().put("messageCode", "bad_request");
					
				}else {
				
					logger.error("Unkown auth API error detected. Transaction will be set to PAUSE:" + operatorAuthResponse.get("operatorErrorMessage"));
					
					finalStatus.setComment("UnknowTransfertError");
					finalStatus.setOperatorComment(errorMessage);
					finalStatus.getData().put("message", "Unkown API error detected. Transaction will be set to PAUSE: " + operatorAuthResponse.get("operatorErrorMessage"));
					finalStatus.getData().put("messageCode", "unkown_error");
				}
								
			}else { // Si l'erreur n'est pas une erreur d'authentification
								
				HashMap<String,Object> operatorTransfertError = (HashMap<String,Object>) response.get("operatorTransfertError");
				
				Boolean operatorProcessStatus = (Boolean) response.get("processError");
				
				String operatorResponseStr = (String) operatorTransfertError.get("operatorResponse");
								
				HashMap<String,Object> operatorResponse = new HashMap<String, Object>();
				
				Integer statusCode = (Integer) operatorTransfertError.get("statusCode");
				
				String errorMessage = (String) operatorResponse.get("message");
				
				String code = null;
					
				String operatorTransactionId = null;
				
				// Construction de la reponse de l'operateur et recuperation de l'id de transaction si possible
				
				if(operatorProcessStatus == true) {
					
					operatorResponse = new ObjectMapper().readValue(operatorResponseStr, HashMap.class);
					
					operatorTransfertError.replace("operatorResponse",operatorResponse);
					
					code = (String) operatorResponse.get("code");
					
					operatorTransactionId = (String) operatorResponse.get("omRequestId");
					
					finalStatus.setOperatorTransactionId(operatorTransactionId);

				}else {
					
					operatorResponse.put("message", operatorResponseStr);
				}
				
												
				if(statusCode == 400) {
					
					if(code.equals("60019")) { // Solde de la ressource insuffisant
						
						logger.error("Resource balance is not enougth: " + errorMessage);
						
						finalStatus.setStatus("PAUSE");
						finalStatus.setComment("OperatorBalanceNotEnough");
						finalStatus.setOperatorComment(errorMessage);
						finalStatus.setData(operatorResponse);
						finalStatus.setKafkaEventTag("insuffisent_balance");
						
					}else { // Probleme sur le token
						
						logger.error("Token error: " + (String) operatorResponse.get("message"));
						
						finalStatus.setStatus("PAUSE");
						finalStatus.setComment("TokenError");
						finalStatus.setOperatorComment(errorMessage);
						finalStatus.setData(operatorResponse);
						
					}
								
				} else if(statusCode == 500) {
															
					switch(code) {
						
						case "99046": // Montant de la transaction inferieur au minimum
							
							logger.error("Transaction amount too min (5 XOF): " + errorMessage);
							
							finalStatus.setStatus("FAILURE");
							finalStatus.setComment("TransactionAmountTooMin");
							finalStatus.setOperatorComment(errorMessage);
							finalStatus.setData(operatorResponse);
							
							break;
							
						case "60019": // Solde de la resource insuffisant
							
							logger.error("Resource balance is not enougth: " + errorMessage);
							
							finalStatus.setStatus("PAUSE");
							finalStatus.setComment("OperatorBalanceNotEnough");
							finalStatus.setOperatorComment(errorMessage);
							finalStatus.setData(operatorResponse);
							finalStatus.setKafkaEventTag("insuffisent_balance");
							
							break;
							
							
						case "0066111": // Erreur dans la structure de la requete
							
							logger.error("500 Internal Server Error: " + errorMessage);
							
							finalStatus.setStatus("FAILURE");
							finalStatus.setComment("TransactionBadFormat");
							finalStatus.setOperatorComment(errorMessage);
							finalStatus.setData(operatorResponse);
							finalStatus.setKafkaEventTag("initiatee_msisdn_error");
							
							break;
							
						case "00410": // Montant de la transaction trop eleve
							
							logger.error("Transaction amout too expensive: " + errorMessage);
							
							finalStatus.setStatus("FAILURE");
							finalStatus.setComment("TransactionTooExpensive");

							finalStatus.setOperatorComment((String) operatorTransfertError.get("operatorErrorMessage"));
						
							finalStatus.setData(operatorResponse);
							
							break;
						
						default:
							
							logger.error("Unkown transfert API 500 error. Transaction will be set to PAUSE:" + errorMessage);
							
							finalStatus.setStatus("FAILURE");
							finalStatus.setComment("UnknowTransfertError");
							
							if(errorMessage == null || errorMessage.isEmpty()) {
								finalStatus.setOperatorComment((String) operatorTransfertError.get("operatorErrorMessage"));
							}else {
								finalStatus.setOperatorComment(errorMessage);
							}
							
							
							finalStatus.setData(operatorResponse);
							finalStatus.setResourceStatus(false);
					}
					
				}else {
									
					logger.error("Unkown transfert API error detected. Transaction will be set to FAILURE:" + errorMessage);
					
					finalStatus.setStatus("FAILURE");
					finalStatus.setComment("UnknowTransfertError");
					finalStatus.setOperatorComment(errorMessage);
					finalStatus.getData().put("statusCode",statusCode);
					finalStatus.getData().put("message", "Unkown API error detected. Transaction will be set to FAILURE: " + errorMessage);
					finalStatus.getData().put("messageCode", "unkown_status_code_error");
					finalStatus.setOperatorData(operatorResponse);
					finalStatus.setResourceStatus(false);
					
				}
				
			}
			
		}else {
			
			HashMap<String,Object> operatorTransfertResponse = (HashMap<String,Object>) response.get("operatorTransfertResponse");

			finalStatus.setComment("TransactionSuccess");
			finalStatus.setData(operatorTransfertResponse);
			finalStatus.setCurrentBalance((Integer) operatorTransfertResponse.get("remainingBalance"));
			finalStatus.setOperatorTransactionId((String) operatorTransfertResponse.get("omTransactionId"));
			finalStatus.setResourceStatus(true);
			finalStatus.setOperatorComment((String) operatorTransfertResponse.get("message"));
			
			String status = (String) operatorTransfertResponse.get("status");

			String finalStatusStr = null;
			
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
			
		}
						
		// DEBUG: logger.info((new ObjectMapper().convertValue(finalStatus, Map.class)).toString());
		
		finalStatus.setBigdata(response);
		
		TransfertUnitResponse transfertUnitResponse = new TransfertUnitResponse(finalStatus);
		
		return transfertUnitResponse;
		
	}
	
	/**
	 * Construit une reponse d'erreur
	 * @param e
	 * @return
	 */
	public HashMap<String,Object> makeErrorResponseFromException(HttpStatusCodeException e) {
		
		HashMap<String,Object> response = new HashMap<String,Object>();

		response.put("statusCode", e.getRawStatusCode());
		response.put("operatorErrorMessage", e.getMessage());
		response.put("operatorLocalizedMessage", e.getLocalizedMessage());
		response.put("operatorStackTrace", e.getStackTrace().toString());	
		response.put("operatorResponse", e.getResponseBodyAsString());	
		
		return response;
	}
}
