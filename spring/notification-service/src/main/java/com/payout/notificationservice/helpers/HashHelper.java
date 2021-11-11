/**
 * @author Jean-Marc Dje Bi
 * since 26-08-2021
 * @version 1
 */
package com.payout.notificationservice.helpers;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payout.notificationservice.transaction.TransactionRequest;

/**
 * Classe de methode helpers pour la manipulation des objets de type HashMap
 */
public class HashHelper {

	/**
	 * Retourne un toString de la version HashMap d'un object
	 * @param object
	 * @return
	 */
	static public String getObjectHashMapString(Object object) {
		
		return (new ObjectMapper().convertValue(object, Map.class)).toString();
 
	}
	
	/**
	 * Retourne le json en HashMap
	 * @param json
	 * @return
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	static public HashMap<?, ?> getHashMap(String json) throws JsonMappingException, JsonProcessingException {
		
		return new ObjectMapper().readValue(json, HashMap.class);
 
	}
}
