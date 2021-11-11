package com.payout.backoffice.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;


@Configuration
public class KafkaTopicConfig {
    
	@Value(value = "${KAFKA_URL}")
	private String kafkaUrl;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl);
        return new KafkaAdmin(configs);
    }
    
    /**
     * Topic pour la mise a jour des resources
     * @return
     */
    @Bean
    public NewTopic updateResource() {    	
    	  return TopicBuilder.name("backoffice_update_resources")
    	            .partitions(10)
    	            .build();
    }
    
    @Bean
    public NewTopic systemEventTopic() {
         return new NewTopic("system_events", 1, (short) 1);
    }
    
    @Bean
    public NewTopic backofficeEventTopic() {
    	return new NewTopic("backoffice_events", 1, (short) 1);
    }
    
    @Bean
    public NewTopic backofficeLogsTopic() {
    	return new NewTopic("backoffice_logs", 1, (short) 1);
    }
    
    @Bean
    public NewTopic GatewayTransactionTopic() {
    	return new NewTopic("gateway_transactions_events", 1, (short) 1);
    }
    
    @Bean
    public NewTopic DispatcherTransactionTopic() {
    	return new NewTopic("dispatcher_transactions_events", 1, (short) 1);
    }
    
    @Bean
    public NewTopic TransactionProcessFailedEventLogTopic() {
    	return new NewTopic("transactions_process_failed_eventlog", 1, (short) 1);
    }
    
    @Bean
    public NewTopic TransactionProcessFailedGatewayTopic() {
    	return new NewTopic("transactions_process_failed_gateway", 1, (short) 1);
    }
    
    @Bean
    public NewTopic TransactionFinalStatus() {    	
    	  return TopicBuilder.name("transactions_final_status")
    	            .partitions(1)
    	            .build();
    }
    
    @Bean
    public NewTopic TransactionFinalStatusGateway() {    	
    	  return TopicBuilder.name("transactions_final_status_gateway")
    	            .partitions(1)
    	            .build();
    }
    
    @Bean
    public NewTopic TransactionNotification() {    	
    	  return TopicBuilder.name("transactions_notification")
    	            .partitions(10)
    	            .build();
    }
    
    @Bean
    public NewTopic TransactionFinalStatus2() {    	
    	return new NewTopic("transactions_final_status2", 1, (short) 1);
    }
    
    @Bean
    public NewTopic TransactionRequestFromGatewayToDispatcher() {    	
    	  return TopicBuilder.name("transaction_request_v2")
    	            .partitions(10)
    	            .build();
    }
    

}