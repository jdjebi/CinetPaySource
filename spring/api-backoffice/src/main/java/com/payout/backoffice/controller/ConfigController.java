/**
 * @author Jean-Marc Dje Bi
 * @since 16-08-2021
 * @version 1
 */
package com.payout.backoffice.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.payout.backoffice.dao.Country;
import com.payout.backoffice.dao.Currency;
import com.payout.backoffice.dao.Operator;
import com.payout.backoffice.kafka.KafkaService;
import com.payout.backoffice.repository.CountryRepository;
import com.payout.backoffice.repository.CurrencyRepository;
import com.payout.backoffice.repository.OperatorRepository;
import com.payout.backoffice.services.dispatcher.DispatcherOperatorConfigBuilder;
import com.payout.backoffice.services.dispatcher.OperatorsConfigEntity;
import com.payout.backoffice.webconf.gateway.GatewayUpdater;
import com.payout.backoffice.webconf.gateway.OperatorGateway;

import java.lang.Exception;

/**
 * Classe des controllers de configurations
 */
@CrossOrigin
@RestController
public class ConfigController {
	
	@Autowired 
	private OperatorRepository operatorRepository;
	
	@Autowired 
	private CurrencyRepository currencyRepository;
	
	@Autowired 
	private CountryRepository countryRepository;

	
	@Autowired
	private KafkaService kafkaService;
	
	@Value("${GATEWAY_URL}")
	String gatewayUrl;
	
	@PutMapping("configs/gateway/operators")
	public List<OperatorGateway> getConfigGatewayOperator() throws Exception {
		
		GatewayUpdater gatewayUpdater = new GatewayUpdater(operatorRepository);
		List<OperatorGateway> operators = gatewayUpdater.getOperators();
		
		RestTemplate restTemplate = new RestTemplate();
		
		try{
			System.out.println(gatewayUrl + "/configs/update/operators");
			restTemplate.put(gatewayUrl + "/configs/update/operators",operators,OperatorGateway.class);
		
		}catch(Exception e) {
			System.out.println(e.getMessage());
			throw new Exception("une erreur c'est produite");
		}
				
		return operators;
		
	}
	
	
	@GetMapping("/configs/dispatcher/operators")
	public List<OperatorsConfigEntity> getConfigsDispatcherOperator() throws Exception {
				
		List<OperatorsConfigEntity>  operatorConfig = (new DispatcherOperatorConfigBuilder(operatorRepository, kafkaService)).build();
		
		return operatorConfig;

	}
	
	
	@GetMapping("/configs/export/all")
	public Object getConfigs() {
	
		Iterable<Currency> currencies = currencyRepository.findAll();
		Iterable<Country> countries = countryRepository.findAll();
		Iterable<Operator> operators = operatorRepository.findAll();
		
		HashMap<String, Object> configs = new HashMap<String, Object>();
		
		configs.put("operators", operators);
		configs.put("currency", currencies);
		configs.put("countries", countries);
		
		return configs;
				
	}
}
