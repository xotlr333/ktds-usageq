package com.telco.management.api.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfig {
    @Value("${app.queue.partitions:8}")
    private int partitionCount;

    @Bean
    public Queue usageQueue() {
        return QueueBuilder.durable("usage.queue")
//                .withArgument("x-single-active-consumer", true)
                .withArgument("x-dead-letter-exchange", "usage.dlx")
                .withArgument("x-dead-letter-routing-key", "usage.dead")
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

    // 파티션된 큐 생성
    @Bean
    public Queue[] usageQueues() {
        Queue[] queues = new Queue[partitionCount];
        for (int i = 0; i < partitionCount; i++) {
            queues[i] = QueueBuilder.durable("usage.queue." + i)
//                    .withArgument("x-single-active-consumer", true)
                    .withArgument("x-dead-letter-exchange", "usage.dlx")
                    .withArgument("x-dead-letter-routing-key", "usage.dead." + i)
                    .build();
        }
        return queues;
    }

    // 파티션별 바인딩 생성
    @Bean
    public Binding[] queueBindings(Queue[] usageQueues, DirectExchange usageExchange) {
        Binding[] bindings = new Binding[partitionCount];
        for (int i = 0; i < partitionCount; i++) {
            bindings[i] = BindingBuilder.bind(usageQueues[i])
                    .to(usageExchange)
                    .with("usage.update." + i);
        }
        return bindings;
    }

    @Bean
    public Binding usageBinding(Queue usageQueue, DirectExchange usageExchange) {
        return BindingBuilder.bind(usageQueue)
                .to(usageExchange)
                .with("usage.update");
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}