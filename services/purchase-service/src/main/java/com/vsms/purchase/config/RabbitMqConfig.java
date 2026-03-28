package com.vsms.purchase.config;

// AUTO-GENERATED: RabbitMQ exchange and queue declarations for purchase-service
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE = "vsms.purchase";
    public static final String GRN_APPROVED_ROUTING_KEY = "grn.approved";
    public static final String DLQ = "vsms.purchase.dlq";

    // AUTO-GENERATED: topic exchange for purchase domain events
    @Bean
    public TopicExchange purchaseExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    // AUTO-GENERATED: dead-letter queue
    @Bean
    public Queue purchaseDeadLetterQueue() {
        return QueueBuilder.durable(DLQ).build();
    }

    @Bean
    public Binding purchaseDlqBinding() {
        return BindingBuilder.bind(purchaseDeadLetterQueue())
                .to(purchaseExchange())
                .with("purchase.#.dlq");
    }
}
