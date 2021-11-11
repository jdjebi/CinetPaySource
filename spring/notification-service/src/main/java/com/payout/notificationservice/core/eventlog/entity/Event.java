package com.payout.notificationservice.core.eventlog.entity;

import java.time.LocalDateTime;

public class Event {
	
	public static String DEBUG = "DEBUG";
	public static String INFO = "INFO";
	public static String WARNING = "WARNING";
	public static String ERROR = "ERROR";
	public static String CRITICAL = "CRITICAL";
	public static String UPDATED = "UPDATED";

	
	public static String EVENT_TYPE = "EVENT";
	public static String LOG_TYPE = "LOG";
	
	private String id;
	
	private String action;
	
	private String entityType;
	
	private String entityRef;
	
	private String originService = "NOTIFICATION-SERVICE";
	
	private String loggingLevel = Event.INFO;
	
	private String strictType = Event.EVENT_TYPE;
	
	private LocalDateTime date = LocalDateTime.now();
	
	private EventData eventData = new EventData();
		
	private String EventFormatVersion = "alpha";
	
	private Integer EventFormatVersionNumber = 1;

	public Event() {
		
	}

	public Event(String entity, String action, String ref, String strictType, String loggingLevel) {
		this.entityType = entity;
		this.action = action;
		this.entityRef = ref;
		this.setStrictType(strictType);
		this.setLoggingLevel(loggingLevel);
	}
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
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
