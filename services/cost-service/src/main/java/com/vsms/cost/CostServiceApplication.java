package com.vsms.cost;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CostServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CostServiceApplication.class, args);
    }
}
