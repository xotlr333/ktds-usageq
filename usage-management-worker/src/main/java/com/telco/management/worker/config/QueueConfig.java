package com.telco.management.worker.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.amqp.support.converter.MessageConverter;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class QueueConfig {

    @Value("${spring.rabbitmq.listener.simple.retry.enabled:true}")  // true에서 false로 변경
    private boolean retryEnabled;

    @Value("${spring.rabbitmq.listener.simple.retry.max-attempts:3}")  // 3에서 1로 변경
    private int maxAttempts;

    @Value("${spring.rabbitmq.listener.simple.concurrency:10}")
    private int concurrency;

    @Bean
    public Queue usageQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-single-active-consumer", true);  // 단일 컨슈머 보장
        args.put("x-dead-letter-exchange", "usage.dlx");
        args.put("x-dead-letter-routing-key", "usage.dead");

        return new Queue("usage.queue", true, false, false, args);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable("usage.dlq")
                .withArgument("x-message-ttl", 60000)
                .build();
    }

    @Bean
    public DirectExchange usageExchange() {
        return new DirectExchange("usage.exchange");
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange("usage.dlx");
    }

    @Bean
    public Binding usageBinding(Queue usageQueue, DirectExchange usageExchange) {
        return BindingBuilder.bind(usageQueue)
                .to(usageExchange)
                .with("usage.update");
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with("usage.dead");
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate template = new RetryTemplate();

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);  // 3에서 1로 변경

        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(1000);
        backOffPolicy.setMultiplier(2.0);
        backOffPolicy.setMaxInterval(10000);

        template.setRetryPolicy(retryPolicy);
        template.setBackOffPolicy(backOffPolicy);

        return template;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        template.setConfirmCallback((correlation, ack, reason) -> {
            if (!ack) {
//                log.error("Message sending failed: {}", reason);
            }
        });
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setConcurrentConsumers(1); // 단일 컨슈머로 설정
        factory.setMaxConcurrentConsumers(1);
        factory.setPrefetchCount(1); // 메시지 순차 처리를 위해 1로 설정
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL); // 수동 ACK 모드

        if (retryEnabled) {
            factory.setAdviceChain(RetryInterceptorBuilder.stateless()
                    .maxAttempts(maxAttempts)
                    .recoverer(new RepublishMessageRecoverer(
                            rabbitTemplate(connectionFactory, messageConverter),
                            "usage.dlx",
                            "usage.dead"))
                    .build());
        }

        return factory;
    }
}