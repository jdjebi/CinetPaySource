/**
 * @author Jean-Marc Dje Bi
 * @since 10-08-2021
 * @version 1
 */

package com.payout.backoffice.services.dispatcher;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.payout.backoffice.dao.Service;
import com.payout.backoffice.kafka.KafkaService;
import com.payout.backoffice.repository.OperatorRepository;


@Component
@RestController
public class DispatcherConfigsSender {
	
	/**
	 * @see OperatorRepository
	 */
	@Autowired
	private OperatorRepository operatorRepository;
	
	/**
	 * @see KafkaService
	 */
	@Autowired
	private KafkaService kafkaService;
	
	Logger logger = LoggerFactory.getLogger(DispatcherConfigsSender.class);

	/**
	 * Envoie la configuration du dispatcher
	 * @param service
	 * @throws Exception
	 */
	public void send(Service service) throws Exception {
		
		DispatcherOperatorConfigBuilder configBuilder = new DispatcherOperatorConfigBuilder(operatorRepository, kafkaService);
		
		logger.info("Sending operators config to dispatcher starting");
		
		List<OperatorsConfigEntity> configs = configBuilder.build();
				
		logger.info("Configs to send: " + configs.toString());
		
		RestTemplate restTemplate = new RestTemplate();

		try{
		
			String dispatcherUrl = service.getUrl();
			
			restTemplate.put(dispatcherUrl + "/configs/update/operators",configs,List.class);
			
			logger.info("Sending operators config to dispatcher done");
				
		}catch(HttpClientErrorException e) {
			e.printStackTrace();
			logger.error("@Sending operators config to dispatcher impossible because:" + e.getMessage());
			throw e;
		}catch(Exception e) {
			logger.error("@Sending operators config to dispatcher impossible because:" + e.getMessage());
			e.printStackTrace();
			throw e;
		}
				
	}
}
