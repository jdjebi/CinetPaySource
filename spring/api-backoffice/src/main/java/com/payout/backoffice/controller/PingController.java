/**
 * @author Jean-Marc Dje Bi
 * @since 18-08-2021
 * @version 1
 */

package com.payout.backoffice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Controller de ping
 */
@CrossOrigin
@RestController
public class PingController {
	
	Logger logger = LoggerFactory.getLogger(PingController.class);
			
	@GetMapping("/ping")
	public boolean pingController(@RequestParam String url) {
		
		if(url == null)
			return false;
		
		logger.info("ping on " + url);
		
		RestTemplate restTemplate = new RestTemplate();
		
		ResponseEntity<String> response = null;

		try{
					
			response = restTemplate.getForEntity(url, String.class);
			
			logger.info("Ping response: " + response.getBody());
			
			return true;
				
		}catch(Exception e) {
			
			logger.error("request failed because:" + e.getMessage());
			
			return false;
		}
				
	}

}
