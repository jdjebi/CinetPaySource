package com.payout.backoffice.repository;

import org.springframework.data.repository.CrudRepository;

import com.payout.backoffice.dao.Resource;

public interface ResourceRepository extends CrudRepository<Resource, Integer>{

	Resource findByName(String resourceName);

}
