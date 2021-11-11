package com.payout.transfert.core.kafka;

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
    public NewTopic transactionsTransfertServiceTopic() {
         return TopicBuilder.name("trx_psp")
 	            .partitions(20)
 	            .build();
    }

}