/**
 * @author Jean-Marc Dje Bi
 * @since 28-07-2021
 * @version 1
 */
package com.payout.eventlog.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payout.eventlog.repository.EventRepository;
import com.payout.eventlog.dao.Event;
import com.payout.eventlog.datascience.EventsAnalyzer;
import com.payout.eventlog.datascience.TransactionEventsPrettyFormat;
import com.payout.eventlog.datascience.TransactionEventsPrettyFormater;

/**
 * Controller rest du service de journalisation
 */
@CrossOrigin
@RestController
public class EventController {
	
	private Integer defaultListEventLimit = 50;
	
	@Autowired EventRepository eventRepository;
	
	@Autowired MongoTemplate mongoTemplate;
	
	@Autowired TransactionEventsPrettyFormater transactionEventsPrettyFormater;

	/**
	 * Retourne la liste de tous les evenements
	 * @return Event
	 */
	@GetMapping("/events")
	public List<Event> getEvents(@RequestParam(name="limit", required = false) Integer limit, @RequestParam(name="orderby", required = false) String orderBy) {
		
		if(limit == null || limit < 0) {
			limit = defaultListEventLimit;
		}
		
		Query query = new Query();
		
		query.limit(limit);
		
		Sort sort = null;
		
		if(orderBy != null && orderBy.equals("asc")) {
			sort = Sort.by("date").ascending();
		}else {
			sort = Sort.by("date").descending();
		}
		
		query.with(sort);	
										
		return mongoTemplate.find(query, Event.class);
	}
	
	/**
	 * Retoune les evenements lies a une entite
	 * @return List<Event>
	 */
	@GetMapping("/events/searchby/entityref/{ref}")
	public List<Event> searchEventsByEntityRef(@PathVariable String ref, @RequestParam(required=false) Integer limit, @RequestParam(name="orderby", required = false) String orderBy){
		
		Query query = new Query();
				
		Sort sort = null;

		query.addCriteria(Criteria.where("entity_ref").is(ref));
						
		if(orderBy != null && orderBy.equals("asc")) {
			sort = Sort.by("date").ascending();
		}else {
			sort = Sort.by("date").descending();
		}
		
		query.with(sort);
		
		if(limit != null && limit > 0) {
			query.limit(limit);
		}
						
		List<Event> events = mongoTemplate.find(query, Event.class);
		
		return events;
	}
	
	/**
	 * Retoune les donnees ici de l'analyse des evenements lies a une transaction: Structure de repartition des evenements par type et service; Les temps de traitement de la transaction
	 * @return TransactionEventsPrettyFormat
	 */
	@GetMapping("/events/stats/entityref/{ref}")
	public Object getEventStatsByEntityRef(@PathVariable String ref) throws Exception{
		
		Query query = new Query();
		
		Sort sort = Sort.by("date").ascending();
		
		query.addCriteria(Criteria.where("entity_ref").is(ref));
				
		query.with(sort);	
						
		List<Event> events = mongoTemplate.find(query, Event.class);
		
		if(events.size() == 0) {
			return null;
		}
		
		TransactionEventsPrettyFormat eventsPrettyFormat = transactionEventsPrettyFormater.format(events);
		
		EventsAnalyzer eventsAnalyzer = new EventsAnalyzer(eventsPrettyFormat); 
		
		HashMap<String, Object> dataMap = eventsAnalyzer.analyzeAll();
		
		HashMap<String, Object> response = new HashMap<String, Object>();
		
		response.put("eventsPrettyFormat", eventsPrettyFormat);
		response.put("_data", dataMap);
				
		return response;
	}
}
