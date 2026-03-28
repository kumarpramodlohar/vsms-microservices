package com.vsms.cost.client;

// AUTO-GENERATED: Feign client — cost-service → sales-service
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;

@FeignClient(name = "sales-service", url = "${services.sales.url:}")
public interface SalesServiceClient {

    @GetMapping("/api/v1/sales-orders/by-code/{orderCode}")
    Object getSalesOrderByCode(@PathVariable String orderCode);

    @PatchMapping("/api/v1/sales-orders/by-code/{orderCode}/activate")
    void activateSalesOrder(@PathVariable String orderCode);
}
