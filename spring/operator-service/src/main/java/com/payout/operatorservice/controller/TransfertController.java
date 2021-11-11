package com.payout.operatorservice.controller;

import java.util.concurrent.TimeUnit;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payout.operatorservice.entity.FinalStatusResponse;
import com.payout.operatorservice.helper.RandomInt;

@RestController
public class TransfertController {

	@PostMapping("/transfert")
	public FinalStatusResponse transfert() throws InterruptedException {
		
		FinalStatusResponse finalStatusResponse = null;
				
		Integer waitingTime = RandomInt.getRandomNumber(10,2000);
		
		Integer fakeWaitingTime = RandomInt.getRandomNumber(0,10);
		
		Integer finalStatusIndicator = RandomInt.getRandomNumber(0,100);
		
		//System.out.println("Wait: " + waitingTime + " ms");
		//System.out.println("finalStatusIndicator: " + finalStatusIndicator + "%");
		
		//TimeUnit.MILLISECONDS.sleep(fakeWaitingTime);
		
		if(finalStatusIndicator <= 55) {
			finalStatusResponse = new FinalStatusResponse("SUCCESS","");
		}else {
			finalStatusResponse = new FinalStatusResponse("FAILURE","OPERATOR-ERROR");
		}
		
		return finalStatusResponse;
		
	}
}
