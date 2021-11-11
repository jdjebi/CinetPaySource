/**
* @author  Jean-Marc Dje Bi
* @version 1
* @since   14-07-2021
*/

package com.payout.backoffice.webconf.gateway;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.payout.backoffice.dao.Operator;
import com.payout.backoffice.repository.OperatorRepository;

/**
*
* Classe de filtrage des operateurs autorite e être envoye à la passerelle
*
*/
public class GatewayUpdater {
	
	OperatorRepository operatorRepository;
	
	Logger logger = LoggerFactory.getLogger(GatewayUpdater.class);
	
	public GatewayUpdater(OperatorRepository operatorRepository) {
		this.operatorRepository = operatorRepository;
	}

	/** Determine la liste des operateur qui peuvent etre envoyer a la passerelle
	 * @return Liste des operateurs ayant au moins un service de transfert
	*/
	public List<OperatorGateway> getOperators() {
				
		Iterable<Operator> operators = operatorRepository.findAll();
		
		List<OperatorGateway> operatorsConfig = new ArrayList<OperatorGateway>();
		
		logger.info("Collect operators config");
		
		for(Operator operator: operators) {
			
			// Envoie la configuration de l'operateur uniquement si ces existent
			
			if(operator.getApiService() != null || operator.getSimService() != null) {
				
				operatorsConfig.add(new OperatorGateway(operator));
				
				logger.info("Operator " + operator.getAlias() + " config collected. Service: [" + operator.getSimService() + "," + operator.getApiService() + "]");

			}else {
				
				logger.warn("Operator " + operator.getAlias() + " can't be collected because it has not services");

			}
			
		}
		
		logger.info("Operators config collected: " + operatorsConfig.toString());
		
		return operatorsConfig;
	}
}
