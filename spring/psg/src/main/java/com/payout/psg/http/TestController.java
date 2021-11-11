package com.payout.psg.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	@Autowired 
	private KafkaTemplate<String, String> kafkaTemplate;
	
}
