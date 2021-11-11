package com.payout.eventlog.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.payout.eventlog.dao.Event;

public interface EventRepository extends MongoRepository<Event, String>{
	
}
