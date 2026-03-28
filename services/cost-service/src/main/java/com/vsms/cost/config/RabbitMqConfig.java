package com.vsms.cost.config;

// AUTO-GENERATED: RabbitMQ exchange and queue declarations for cost-service
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE = "vsms.cost";
    public static final String COST_APPROVED_ROUTING_KEY = "cost.approved";
    public static final String DLQ = "vsms.cost.dlq";

    // AUTO-GENERATED: topic exchange for cost domain events
    @Bean
    public TopicExchange costExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    // AUTO-GENERATED: dead-letter queue
    @Bean
    public Queue costDeadLetterQueue() {
        return QueueBuilder.durable(DLQ).build();
    }

    @Bean
    public Binding costDlqBinding() {
        return BindingBuilder.bind(costDeadLetterQueue())
                .to(costExchange())
                .with("cost.#.dlq");
    }
}
