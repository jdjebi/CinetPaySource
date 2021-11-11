package com.payout.psg.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.payout.psg.model.GatewaySystem;

import java.util.List;

public interface SystemRepository extends MongoRepository<GatewaySystem, String>{
	List<GatewaySystem> findAll();
}
