package com.telco.management.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = {
        "com.telco.management.api",  // API 모듈의 컴포넌트만 스캔
        "com.telco.common.dto"       // common 모듈에서 DTO만 스캔
})
public class UsageManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(UsageManagementApplication.class, args);
    }
}