package com.telco.management.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.telco.common.entity")
@EnableJpaRepositories(basePackages = "com.telco.management.worker.repository")
@EnableJpaAuditing
@ComponentScan(basePackages = {
        "com.telco.management.worker.config",
        "com.telco.management.worker.service",
        "com.telco.management.worker.repository",
        "com.telco.management.worker.mapper"
})
public class UsageManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(UsageManagementApplication.class, args);
    }
}