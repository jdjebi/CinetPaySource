package com.payout.dispatcher.operators;


import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface OperatorRepository extends MongoRepository<Operator, String> {
	
	public Optional<Operator> findByName(String name);

}
