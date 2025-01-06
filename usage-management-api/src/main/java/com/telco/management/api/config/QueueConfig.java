package com.telco.management.api.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfig {
    @Bean
    public Queue usageQueue() {
        return QueueBuilder.durable("usage.queue")
                .withArgument("x-dead-letter-exchange", "usage.dlx")
                .withArgument("x-dead-letter-routing-key", "usage.dead")
                .build();
    }

    @Bean
    public DirectExchange usageExchange() {
        return new DirectExchange("usage.exchange");
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