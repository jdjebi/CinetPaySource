package com.payout.backoffice.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class MainController {

	@GetMapping(value="", produces={"application/json"})
	public String hello() {
		return "{servicename:\"API Backoffice\", status:\"OK\", message:\"I'm OK !\"}";
	}
}
