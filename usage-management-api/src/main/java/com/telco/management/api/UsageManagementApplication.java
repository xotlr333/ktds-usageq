package com.telco.management.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "com.telco.management.api",
        "com.telco.common"
})
@EntityScan(basePackages = "com.telco.common.entity")
@EnableJpaRepositories(basePackages = "com.telco.management.api.repository")
@EnableJpaAuditing
public class UsageManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(UsageManagementApplication.class, args);
    }
}