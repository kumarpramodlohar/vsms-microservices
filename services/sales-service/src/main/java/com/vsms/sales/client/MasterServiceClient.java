package com.vsms.sales.client;

// AUTO-GENERATED: Feign client — contract between sales-service and master-service
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "master-service", url = "${services.master.url:}")
public interface MasterServiceClient {

    @GetMapping("/api/v1/master/companies/{id}")
    Object getCompany(@PathVariable Long id);

    @GetMapping("/api/v1/master/items/{id}")
    Object getItem(@PathVariable Long id);
}
