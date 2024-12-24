package com.telco.query;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.telco.common.entity")
@EnableJpaRepositories(basePackages = "com.telco.query.repository")
@ComponentScan(basePackages = {"com.telco.query", "com.telco.common"})
public class UsageQueryApplication {
    public static void main(String[] args) {
        SpringApplication.run(UsageQueryApplication.class, args);
    }
}