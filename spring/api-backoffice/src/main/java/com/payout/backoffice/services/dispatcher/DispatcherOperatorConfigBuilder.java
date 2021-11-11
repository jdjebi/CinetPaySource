/**
* @author  Jean-Marc Dje Bi
* @version 1
* @since   15-07-2021
*/

package com.payout.backoffice.services.dispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import com.payout.backoffice.dao.Operator;
import com.payout.backoffice.dao.Resource;
import com.payout.backoffice.dao.Service;
import com.payout.backoffice.eventlog.entity.Event;
import com.payout.backoffice.kafka.KafkaService;
import com.payout.backoffice.repository.OperatorRepository;

/**
* Classe de construction des configurations operateur du Dispatcher
*/
@Component
@RestController
public class DispatcherOperatorConfigBuilder {
	
	private OperatorRepository operatorRepository;
	
	private KafkaService kafkaService;
	
	Logger logger = LoggerFactory.getLogger(DispatcherOperatorConfigBuilder.class);
	
	public DispatcherOperatorConfigBuilder(OperatorRepository operatorRepository, KafkaService kafkaService) {
		this.operatorRepository = operatorRepository;
		this.kafkaService = kafkaService;
	}
	
	/**
	*
	* Construit les donnees de configuration du dispatcher
	* @return Liste des configurations d'operateur du dispatcher
	*
	*/
	public List<OperatorsConfigEntity> build() throws Exception {
		
		List<OperatorsConfigEntity>  operatorsConfigList = new ArrayList<OperatorsConfigEntity>();
		
		Resource api = null;
		List<Resource> sims = null;
				
		List<Operator> operators = (List<Operator>) operatorRepository.findAll();
		
		logger.info("Build operators config");
					
		
		for(int i = 0; i < operators.size(); i++) {
						
			api = null;
			sims = new ArrayList<Resource>();
			
			Operator operator = operators.get(i);
			
			logger.info("Building config for operator: " + operator.getAlias());
						
			List<Resource> resources = operator.getResources();
			
			for(int j = 0; j < resources.size(); j++) {
				
				if(resources.get(j).getType().equals("API")) {
					api = resources.get(j);
										
				}else if(resources.get(j).getType().equals("SIM")) {
					sims.add(resources.get(j));
				}else {
					throw new Exception("La ressource d'id " + resources.get(i) + " ne possède pas de type");
				}
			}
			
			OperatorsConfigEntity opConf = new OperatorsConfigEntity();
			
			Service apiService = operator.getApiService();
			Service simService = operator.getSimService();
			
			opConf.setName(operator.getAlias());
			opConf.setResourcesCode(operator.getResourcesCode());
			
			if(operator.getResourcesCode() == "API_SIM") {
				opConf.setApi(api);
				opConf.pushSims(sims);
			}else if(operator.getResourcesCode() == "API") {
				opConf.setApi(api);
			}else if(operator.getResourcesCode() == "SIM") {
				opConf.pushSims(sims);
			}
			
			if(apiService == null) {
				opConf.setApi(null);
				logger.warn("Operator have not API resource");
			}
			
			if(simService == null) {
				opConf.setSims(null);
				logger.warn("Operator have not SIMS resources");
			}
			
			Boolean check = (api != null || sims.size() != 0) && (apiService != null || simService != null);
			
			logger.info("Analyze resources condition check status :" + check.toString());
				
			
			if(check) {
				
				logger.info("Analyzing operator resources url and services kafka topics and services of " + operator.getAlias());
									
				String urlApi = apiService != null ? apiService.getUrl() : "";
				
				String topicApiTransfertService = apiService != null ? apiService.getKafkaTopic() : "";
				
				String urlSim = simService != null ? simService.getUrl() : "";
				
				String topicSimTransfertService = simService != null ? simService.getKafkaTopic() : "";
								
				check = (!urlApi.isEmpty() && topicApiTransfertService != null && !topicApiTransfertService.isEmpty()) || (!urlSim.isEmpty() && topicSimTransfertService != null && !topicSimTransfertService.isEmpty());
				
				logger.info("Analyze operator components addressable status : " + check.toString());

				if(check) {
					
					logger.info(String.format("Operator %s is OK ! Config building...",operator.getAlias()));
					
					opConf.setTopicApi(topicApiTransfertService);
					opConf.setTopicSim(topicSimTransfertService);
					opConf.setUrlApi(urlSim);
					opConf.setUrlSim(urlSim);
					
					operatorsConfigList.add(opConf);
					
					kafkaService.pushLogEvent("SYSTEM","OperatorAddToDispatcherConfig", operator.getAlias(), Event.INFO);
					
				}else {
					
					HashMap<String,Object> data = new HashMap<String,Object>();
					
					data.put("topicApi", topicApiTransfertService);
					data.put("topicSim", topicSimTransfertService);
					data.put("urlSim", urlApi);
					data.put("urlSim", urlSim);
										
					kafkaService.pushEvent("OPERATOR","OperatorIncompleteDataDetected", operator.getAlias(), Event.ERROR, data);
					
					logger.warn("Can't send config for operator " + operator.getAlias() + " because none transfert services urls or topic for api and sims defined. Please check operator service url config");
				
				}

			}else {
				
				kafkaService.pushEvent("OPERATOR","OperatorIncompleteDataDetected", operator.getAlias(), Event.ERROR);
				
				logger.warn("Can't send config for operator " + operator.getAlias() + " because none resources or/and transfert services associate defined. Please check operator config");
			}	
			
	
		}
		
		logger.info("Operators config built: " + operatorsConfigList.toString());
		
		return operatorsConfigList;
	}
}
