package com.vsms.customer.config;

// AUTO-GENERATED: RabbitMQ exchange and queue declarations for customer-service
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE = "vsms.customer";
    public static final String CUSTOMER_APPROVED_ROUTING_KEY = "customer.approved";
    public static final String DLQ = "vsms.customer.dlq";

    // AUTO-GENERATED: topic exchange for customer domain events
    @Bean
    public TopicExchange customerExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    // AUTO-GENERATED: dead-letter queue for unprocessable messages
    @Bean
    public Queue customerDeadLetterQueue() {
        return QueueBuilder.durable(DLQ).build();
    }

    // AUTO-GENERATED: binding — dead-letter queue catches failed messages
    @Bean
    public Binding customerDlqBinding() {
        return BindingBuilder.bind(customerDeadLetterQueue())
                .to(customerExchange())
                .with("customer.#.dlq");
    }
}
