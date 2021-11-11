/**
 * @author Jean-Marc Dje Bi
 * @since 21-07-2021
 * @version 1
 */

package com.payout.backoffice.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.payout.backoffice.dao.User;
import com.payout.backoffice.exception.UserNotFoundException;
import com.payout.backoffice.repository.UserRepository;

/**
 * Classe de gestion des utilisateurs
 *
 */
@CrossOrigin
@RestController
public class UserController {
	
	@Autowired
	UserRepository userRepository;
	
	Logger logger = LoggerFactory.getLogger(UserController.class);

	
	@PostMapping("users")
	public HashMap<String, Object> createUser(@RequestBody User userTmp) throws Exception {
		
		HashMap<String, Object> response = new HashMap<String, Object>();
		User user = null;
		
		logger.info("Creating new user");
				
		// Tester l'existance du username
		
		Optional<User> userOpt = userRepository.findByUsername(userTmp.getUsername());
		
		if(userOpt.isPresent()) {
						
			response.put("errorCode","USER_ALREADY_EXIST");
			response.put("message", "user already exist");
			
			logger.info(String.format("User %s already exist", userTmp.getUsername()));
			
		}else {
			
			userTmp.setRole("staff");
			user = userRepository.save(userTmp);
			
			response.put("errorCode", null);
			response.put("message", "user created");
			response.put("user", user);
			
			logger.info(String.format("User %s created", user.getUsername()));
		}
			
		return response;
		
	}
		
	@GetMapping("users")
	public Iterable<User> getAllUser() {
		
		
		return userRepository.findAllByOrderByCreatedAtAsc();
	}
	
	@GetMapping("users/{id}")
	public User getUser(@PathVariable Integer id) {
		return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
	}
	
	@PutMapping("users/{id}")
	public User simpleUpdateUser(@PathVariable Integer id, @RequestBody User userTmp) {
				
		User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
		
		logger.info(String.format("Updating user %s ",id));
		
		user.setUsername(userTmp.getUsername());
		user.setSurname(userTmp.getSurname());
		user.setName(userTmp.getName());
		user.setRole(userTmp.getRole());
		user.setUpdatedAt(new Date());
		userRepository.save(user);
		
		logger.info(String.format("User %s updated",id));
		
		return null;
		
	}
	
	@DeleteMapping("users/{id}")
	public User deleteUser(@PathVariable Integer id) {
				
		User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
		
		userRepository.delete(user);
		
		logger.info(String.format("User %s deleted",id));
		
		return null;
		
	}
	
}
