package com.vsms.inventory.client;

// AUTO-GENERATED: Feign client — inventory-service → master-service
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "master-service", url = "${services.master.url:}")
public interface MasterServiceClient {

    @GetMapping("/api/v1/master/items/{id}")
    Object getItem(@PathVariable Long id);

    @GetMapping("/api/v1/master/uom/{id}")
    Object getUom(@PathVariable Long id);
}
