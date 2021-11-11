package com.payout.eventlog.dao;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection="events")
public class Event {
	
	public static String DEBUG = "DEBUG";
	public static String INFO = "INFO";
	public static String WARNING = "WARNING";
	public static String ERROR = "ERROR";
	public static String CRITICAL = "CRITICAL";
	
	public static String EVENT_TYPE = "EVENT";
	public static String LOG_TYPE = "LOG";
	
	private String action;
	
	@Id
	private String id; 
	
	@Field(name="entity_type")		
	private String entityType;
	
	@Field(name="entity_ref")
	private String entityRef;
	
	@Field(name="origin_service")
	private String originService = "SYSTEM";
	
	@Field(name="logging_level")
	private String loggingLevel = Event.INFO;
	
	@Field(name="strict_type")
	private String strictType = Event.EVENT_TYPE;
	
	private LocalDateTime date = LocalDateTime.now();
	
	@Field(name="event_data")
	private EventData eventData = new EventData();
	
	@Field(name="event_format_version")
	private String EventFormatVersion = "alpha";
	
	@Field(name="event_format_version_number")
	private Integer EventFormatVersionNumber = 1;

	public Event() {
		
	}
	
	public Event(String entity, String action, String ref, String strictType, String loggingLevel) {
		System.out.println(entity);
		this.entityType = entity;
		this.setAction(action);
		this.entityRef = ref;
		this.setStrictType(strictType);
		this.setLoggingLevel(loggingLevel);
	}

	
	public String getEntityType() {
		return entityType;
	}
	
	public void setEntityType(String enityType) {
		this.entityType = enityType;
	}
	
	public String getEntityRef() {
		return entityRef;
	}
	public void setEntityRef(String entityRef) {
		this.entityRef = entityRef;
	}
	
	public LocalDateTime getDate() {
		return date;
	}
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	public EventData getEventData() {
		return eventData;
	}
	public void setEventData(EventData eventData) {
		this.eventData = eventData;
	}
	
	public String toString() {
		return String.format("Event::%s@%s[entity:%s; action:%s; entifyRef: %s; date: %s, service: %s, data: %s]", strictType, loggingLevel, entityType, action, entityRef, date.toLocalDate() + " " + date.toLocalTime(), originService,eventData.toString() );
	}

	public String getEventFormatVersion() {
		return EventFormatVersion;
	}

	public void setEventFormatVersion(String eventFormatVersion) {
		EventFormatVersion = eventFormatVersion;
	}

	public Integer getEventFormatVersionNumber() {
		return EventFormatVersionNumber;
	}

	public void setEventFormatVersionNumber(Integer eventFormatVersionNumber) {
		EventFormatVersionNumber = eventFormatVersionNumber;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getLoggingLevel() {
		return loggingLevel;
	}


	public void setLoggingLevel(String loggingLevel) {
		this.loggingLevel = loggingLevel;
	}


	public String getStrictType() {
		return strictType;
	}


	public void setStrictType(String strictType) {
		this.strictType = strictType;
	}

	public String getOriginService() {
		return originService;
	}

	public void setOriginService(String originService) {
		this.originService = originService;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
