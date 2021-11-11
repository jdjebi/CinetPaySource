package com.payout.eventlog.manager;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.payout.eventlog.dao.Event;
import org.springframework.stereotype.Component;

@Component
public class SimpleEventAdapter {
	
	@Autowired 
	SystemEventManager systemEventManager;
	
	public void on(String message) throws Exception {
		
		try {
			
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());
			Event event = mapper.readValue(message, Event.class);
			
			try {
				
				// System.out.print(event.getDate());
				// System.out.println(" " + event.getOriginService());
				
				TimeUnit.MILLISECONDS.sleep(0);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
			systemEventManager.manage(event);
									
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

	}
	
}
