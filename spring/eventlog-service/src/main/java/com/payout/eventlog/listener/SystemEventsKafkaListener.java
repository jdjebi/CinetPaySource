package com.payout.eventlog.listener;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payout.eventlog.dao.Event;
import com.payout.eventlog.manager.SimpleEventAdapter;
import com.payout.eventlog.manager.SystemEventManager;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Component
public class SystemEventsKafkaListener{
	
	@Autowired 
	SystemEventManager systemEventManager;
	
	@Autowired 
	SimpleEventAdapter simpleEventAdapter;

	@KafkaListener(topics = "system_events", groupId = "system_events_group")
	public void listenSystemEvents(String message) throws Exception {
		
		// System.out.println(message);
			
		try { 
			
			ObjectMapper mapper = new ObjectMapper();
			
			mapper.registerModule(new JavaTimeModule());
			
			Event event = mapper.readValue(message, Event.class);
			
			
			try {
				/*
				System.out.print("EVENT: ");
				System.out.print(event.getDate());
				System.out.println(" " + event.getOriginService());
				*/
				
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
	
	@KafkaListener(topics = "backoffice_events", groupId = "system_events_group_backoffice")
	public void listenSystemEventsLogs(String message) throws Exception {
		
		// System.out.println(message);
			
		try {
			
			ObjectMapper mapper = new ObjectMapper();
			
			mapper.registerModule(new JavaTimeModule());
			
			Event event = mapper.readValue(message, Event.class);
			
			
			try {
				
				/*
				System.out.print("BACKOFFICE_EVENTS: ");
				System.out.print(event.getDate());
				System.out.println(" " + event.getOriginService());
				*/
				
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
	
	@KafkaListener(topics = "gateway_events", groupId = "system_events_group_gateway_events")
	public void listenSystemEventsGateway(String message) throws Exception {
					
		try {
			
			ObjectMapper mapper = new ObjectMapper();
			
			mapper.registerModule(new JavaTimeModule());
			
			Event event = mapper.readValue(message, Event.class);
			
			
			try {
				
				/*
				System.out.print("GATEWAY: ");
				System.out.print(event.getDate());
				System.out.println(" " + event.getOriginService());
				*/
				
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
	
	@KafkaListener(topics = "backoffice_logs", groupId = "system_events_group_backoffice_logs")
	public void listenSystemBackofficeLogs(String message) throws Exception {
		
		// System.out.println(message);
			
		try {
			
			ObjectMapper mapper = new ObjectMapper();
			
			mapper.registerModule(new JavaTimeModule());
			
			Event event = mapper.readValue(message, Event.class);
			
			
			try {
				/*
				System.out.print("BACKOFFICE_LOGS: ");
				System.out.print(event.getDate());
				System.out.println(" " + event.getOriginService());
				*/
				
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
	
	@KafkaListener(topics = "dispatcher_transactions_events", groupId = "dispatcher_transactions_events_group")
	public void listenDispatcherTransactionEvents(String event) throws Exception {
		// System.out.println("DISPATCH TRX: ");
		simpleEventAdapter.on(event);
	}
	
}
