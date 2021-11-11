package com.payout.backoffice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.payout.backoffice.dao.User;

public interface UserRepository extends CrudRepository<User, Integer>{
	
	User findByUsernameAndPassword(String username, String password);
	
	Optional<User> findByUsername(String username);
	
	List<User> findAllByUsernameAndPassword(String username, String password);
	
	List<User> findAllByOrderByCreatedAtDesc();
	
	List<User> findAllByOrderByCreatedAtAsc();


	
}
