package com.payout.backoffice.webconf.transfert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.payout.backoffice.dao.Service;
import com.payout.backoffice.repository.ServiceRepository;
import com.payout.backoffice.webconf.transfert.entity.ServiceInfoWrapper;

@Component
public class UpdateTransactionService {
	
	@Autowired
	ServiceRepository serviceRepository;
	
	public void updateFromServiceInfoWrapper(Service service, ServiceInfoWrapper wrapper) {
		
		service.setRole(wrapper.getApp().getRole());
		service.setKafkaTopic(wrapper.getApp().getKafkaTopic());
				
	}

}
