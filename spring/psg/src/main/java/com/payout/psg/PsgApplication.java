package com.payout.psg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import com.payout.psg.repository.SystemRepository;

@EnableDiscoveryClient
@SpringBootApplication
public class PsgApplication{
	
	@Autowired SystemRepository systemRepository;
	
	@Value(value = "${KAFKA_URL}")
	private String kafkaUrl;
	
	@Value(value = "${spring.data.mongodb.uri}")
	private String mongoUri;


	public static void main(String[] args) {
		SpringApplication.run(PsgApplication.class, args);
	}
	
	@Bean
    public CommandLineRunner CommandLineRunnerBean() {
        return (args) -> {
        	
        	System.out.println("Kafka: " + kafkaUrl);
        	System.out.println("MongoDB: " + mongoUri);

        };
    }
	


}
