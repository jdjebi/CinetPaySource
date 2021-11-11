/**
* @author  Jean-Marc Dje Bi
* @version 1
* @since   14-07-2021
*/

package com.payout.backoffice.webconf.gateway;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import com.payout.backoffice.dao.Service;
import com.payout.backoffice.repository.OperatorRepository;

/**
 * Classe d'envoie des configurations de la passerelle
 */
public class GatewayConfigsSender {
	
	/**
	 * @see OperatorRepository
	 */
	private OperatorRepository operatorRepository;
	
	Logger logger = LoggerFactory.getLogger(GatewayConfigsSender.class);
	
	public GatewayConfigsSender(OperatorRepository operatorRepository) {
		this.operatorRepository = operatorRepository;
	}
	
	/**
	 * Envoie la configuration de la passerelle à la passerelle
	 * @param service Instance du service passerelle
	 */
	public void send(Service service) throws Exception  {
		
		try{
					
			String gatewayUrl = service.getUrl();
			
			GatewayUpdater gatewayUpdater = new GatewayUpdater(operatorRepository);
			
			List<OperatorGateway> operators = gatewayUpdater.getOperators();
			
			logger.info("Send operators to gateway: " + operators.toString());
			
			RestTemplate restTemplate = new RestTemplate();
						
			restTemplate.put(gatewayUrl + "/configs/update/operators",operators,OperatorGateway.class);
						
		}catch(Exception e) {
			
			logger.info("@Envoie de la configuration de la passerelle impossible: " + e.getMessage());
			
		}
		
	}
	
}
