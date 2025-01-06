package com.telco.management.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.telco.common.entity")
@EnableJpaRepositories(basePackages = "com.telco.management.api.repository")
@EnableJpaAuditing
@ComponentScan(basePackages = {
        "com.telco.management.api",  // API 모듈의 컴포넌트만 스캔
        "com.telco.common",
        "com.telco.common.dto"       // common 모듈에서 DTO만 스캔
})
public class UsageManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(UsageManagementApplication.class, args);
    }
}
