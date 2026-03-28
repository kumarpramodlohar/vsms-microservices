package com.vsms.sales.client;

// AUTO-GENERATED: Feign client — contract between sales-service and customer-service
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;

@FeignClient(name = "customer-service", url = "${services.customer.url:}")
public interface CustomerServiceClient {

    // TODO: replace CustomerResponse with actual DTO once customer-service DTOs are defined
    @GetMapping("/api/v1/customers/{id}")
    Object getCustomer(@PathVariable UUID id);

    @GetMapping("/api/v1/customers/exists/{id}")
    Boolean customerExists(@PathVariable UUID id);
}
