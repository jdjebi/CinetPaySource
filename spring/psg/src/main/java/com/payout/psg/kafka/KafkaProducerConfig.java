package com.payout.psg.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.payout.psg.eventlog.entity.Event;
import com.payout.psg.model.TransactionAbstract;
import com.payout.psg.model.User;
import com.payout.psg.transactions.TransactionRequest;

@Configuration
public class KafkaProducerConfig {

    @Value(value = "${KAFKA_URL}")
    private String kafkaUrl;
    
    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);        
        return props;
    }
    
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    } 
    
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<String, String>(producerFactory());
    }
    
    /* Transaction */
    
    @Bean
    public Map<String, Object> producerTransactionConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);        
        return props;
    }
      
    @Bean
    public ProducerFactory<String, TransactionAbstract> producerTransactionFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    } 
    
    @Bean
    public KafkaTemplate<String, TransactionAbstract> kafkaTransactionTemplate() {
        return new KafkaTemplate<String, TransactionAbstract>(producerTransactionFactory());
    }
    
 /* Transaction */
    
    @Bean
    public Map<String, Object> producerUserConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);        
        return props;
    }
      
    @Bean
    public ProducerFactory<String, User> producerUserFactory() {
        return new DefaultKafkaProducerFactory<>(producerUserConfigs());
    } 
    
    @Bean
    public KafkaTemplate<String, User> kafkaUserTemplate() {
        return new KafkaTemplate<String, User>(producerUserFactory());
    }
    
    
/* Transaction */
    
    @Bean
    public ProducerFactory<String, TransactionRequest> producerTransactionRequest() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);        
        return new DefaultKafkaProducerFactory<>(props);
    }
    
    @Bean
    public KafkaTemplate<String, TransactionRequest> kafkaTransactionRequestTemplate() {
        return new KafkaTemplate<String, TransactionRequest>(producerTransactionRequest());
    }
    
    /* Event for Kafka */
    @Bean
    public ProducerFactory<String, Event> producerEvent() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);        
        return new DefaultKafkaProducerFactory<>(props);
    }
    
    @Bean
    public KafkaTemplate<String, Event> kafkaEventTemplate() {
        return new KafkaTemplate<String, Event>(producerEvent());
    }
}