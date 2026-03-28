package com.vsms.inventory.config;

// AUTO-GENERATED: RabbitMQ queue declarations for inventory-service (consumer only)
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    // Queues for consuming events from purchase-service and sales-service
    public static final String GRN_APPROVED_QUEUE = "inventory.grn.approved";
    public static final String SALES_ORDER_ACTIVATED_QUEUE = "inventory.sales.order.activated";
    public static final String DLQ = "vsms.inventory.dlq";

    // AUTO-GENERATED: queue for GrnApproved events from vsms.purchase exchange
    @Bean
    public Queue grnApprovedQueue() {
        return QueueBuilder.durable(GRN_APPROVED_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", DLQ)
                .build();
    }

    // AUTO-GENERATED: queue for SalesOrderActivated events from vsms.sales exchange
    @Bean
    public Queue salesOrderActivatedQueue() {
        return QueueBuilder.durable(SALES_ORDER_ACTIVATED_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", DLQ)
                .build();
    }

    // AUTO-GENERATED: dead-letter queue
    @Bean
    public Queue inventoryDeadLetterQueue() {
        return QueueBuilder.durable(DLQ).build();
    }

    // TODO: add Exchange and Binding beans once vsms.purchase and vsms.sales exchanges are confirmed
    // @Bean
    // public Binding grnApprovedBinding(Queue grnApprovedQueue, TopicExchange purchaseExchange) {
    //     return BindingBuilder.bind(grnApprovedQueue).to(purchaseExchange).with("grn.approved");
    // }
}
