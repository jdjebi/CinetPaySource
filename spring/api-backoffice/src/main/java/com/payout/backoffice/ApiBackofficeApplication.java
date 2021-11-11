package com.payout.backoffice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;

@SpringBootApplication
public class ApiBackofficeApplication {
	
	@Autowired
	private ConfigurableEnvironment env;
	
	@Value("${NOTIFICATION_URL}")
	private String eventLogStr;
	
	@Value(value = "${KAFKA_URL}")
	private String kafkaUrl;

	public static void main(String[] args) {

		SpringApplication.run(ApiBackofficeApplication.class, args);
					
	}
	
	@Bean
    public CommandLineRunner CommandLineRunnerBean() {
        return (args) -> {
        	System.out.println("Kafka: " + kafkaUrl);
        	
        	
        	System.out.println("\n");       	

        	System.out.println(eventLogStr);
        	
        	for (String profileName : env.getActiveProfiles()) {
                System.out.println("Currently active profile - " + profileName);
            }

        };
    }

}
