package com.payout.transfert.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.payout.transfert.dao.Resource;
import com.payout.transfert.dao.ResourceToken;


public interface ResourceTokenRepository extends MongoRepository<ResourceToken, String>{

	ResourceToken findByOperatorCode(String code);
	
}
