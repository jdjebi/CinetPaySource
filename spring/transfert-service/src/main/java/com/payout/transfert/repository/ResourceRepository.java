package com.payout.transfert.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.payout.transfert.dao.Resource;


public interface ResourceRepository extends MongoRepository<Resource, String>{

	Resource findByOperatorCode(String code);
	Resource findByName(String name);
}
