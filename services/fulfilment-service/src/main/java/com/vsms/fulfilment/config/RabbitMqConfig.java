package com.vsms.fulfilment.config;

// AUTO-GENERATED: RabbitMQ exchange and queue declarations for fulfilment-service
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE = "vsms.fulfilment";
    public static final String INVOICE_GENERATED_ROUTING_KEY = "invoice.generated";
    public static final String DLQ = "vsms.fulfilment.dlq";

    // AUTO-GENERATED: topic exchange for fulfilment domain events
    @Bean
    public TopicExchange fulfilmentExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    // AUTO-GENERATED: dead-letter queue
    @Bean
    public Queue fulfilmentDeadLetterQueue() {
        return QueueBuilder.durable(DLQ).build();
    }

    @Bean
    public Binding fulfilmentDlqBinding() {
        return BindingBuilder.bind(fulfilmentDeadLetterQueue())
                .to(fulfilmentExchange())
                .with("fulfilment.#.dlq");
    }
}
