package com.telco.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.telco.common.entity")
@EnableJpaRepositories(basePackages = "com.telco.management.repository")
@ComponentScan(basePackages = {"com.telco.management", "com.telco.common"})
public class UsageManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(UsageManagementApplication.class, args);
    }
}