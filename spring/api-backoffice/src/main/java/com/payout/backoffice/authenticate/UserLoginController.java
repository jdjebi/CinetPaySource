/**
 * @author Jean-Marc Dje Bi
 * @since 21-07-2021
 * @version 1.0.1
 * @update 17-08-2021
 */

package com.payout.backoffice.authenticate;

import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.payout.backoffice.dao.User;
import com.payout.backoffice.repository.UserRepository;

/**
 * Classe du controller gerant la l'authentification d'un administrateur
 */
@CrossOrigin
@RestController
public class UserLoginController {
	
	@Autowired
	UserRepository userRepository;

	@PostMapping("users/login")
	public HashMap<String,Object> login(@RequestBody HashMap<String,Object> userMap) {	
		
		
		String username = (String) userMap.get("username");
		String password = (String) userMap.get("password");
						
		User user = userRepository.findByUsernameAndPassword(username, password);
		
		HashMap<String,Object> response = new HashMap<String,Object>();
		
		if(user == null){
			response.put("status", false);
		}else {
			
			user.setLastConnexion(new Date());
			
			user = userRepository.save(user);
			
			response.put("status", true);
		}
		
		response.put("user", user);
					
		return response;
	}
	
}
