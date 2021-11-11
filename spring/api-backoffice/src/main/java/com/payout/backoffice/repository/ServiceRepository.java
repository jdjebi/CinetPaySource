package com.payout.backoffice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payout.backoffice.dao.Service;

public interface ServiceRepository  extends JpaRepository<Service, Integer>{
	
	List<Service> findByRole(String role);
		
	List<Service> findByRoleAndOperatorIsNotNullOrderByPriority(String role);
	
	List<Service> findByTypeOrderByPriority(String type);
	
	List<Service> findByTypeAndOperatorIsNullOrderByPriority(String type);
	
	List<Service> findByTypeAndOperatorIsNotNullOrderByPriority(String type);
	
	Service findByCode(String code);
}
