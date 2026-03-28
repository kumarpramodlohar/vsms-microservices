package com.vsms.fulfilment.client;

// AUTO-GENERATED: Feign client — fulfilment-service → sales-service
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "sales-service", url = "${services.sales.url:}")
public interface SalesServiceClient {

    @GetMapping("/api/v1/sales-orders/by-code/{orderCode}")
    Object getSalesOrderByCode(@PathVariable String orderCode);
}
