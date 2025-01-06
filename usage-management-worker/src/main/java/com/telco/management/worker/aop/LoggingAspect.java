// File: ktds-usageq/usage-management-service/src/main/java/com/telco/management/aop/LoggingAspect.java
package com.telco.management.worker.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* com.telco.management.service.*.*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();

        log.info("Started executing {}", methodName);
        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - start;
            log.info("Finished executing {} in {}ms", methodName, executionTime);

            return result;
        } catch (Exception e) {
            log.error("Error executing {}: {}", methodName, e.getMessage());
            throw e;
        }
    }

}