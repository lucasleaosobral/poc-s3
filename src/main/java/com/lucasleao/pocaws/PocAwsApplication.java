package com.lucasleao.pocaws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
@ConfigurationPropertiesScan
public class PocAwsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PocAwsApplication.class, args);
    }

}
