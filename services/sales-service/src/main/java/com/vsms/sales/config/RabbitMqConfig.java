package com.vsms.sales.config;

// AUTO-GENERATED: RabbitMQ exchange and queue declarations for sales-service
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE = "vsms.sales";
    public static final String SALES_ORDER_ACTIVATED_ROUTING_KEY = "sales.order.activated";
    public static final String DLQ = "vsms.sales.dlq";

    // AUTO-GENERATED: topic exchange for sales domain events
    @Bean
    public TopicExchange salesExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    // AUTO-GENERATED: dead-letter queue
    @Bean
    public Queue salesDeadLetterQueue() {
        return QueueBuilder.durable(DLQ).build();
    }

    // AUTO-GENERATED: binding — dead-letter queue
    @Bean
    public Binding salesDlqBinding() {
        return BindingBuilder.bind(salesDeadLetterQueue())
                .to(salesExchange())
                .with("sales.#.dlq");
    }
}
