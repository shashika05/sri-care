package com.sritel.mocks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@SpringBootApplication
@RestController
public class ExternalMocksApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExternalMocksApplication.class, args);
    }

    // --- Mock Provisioning System ---
    @PostMapping("/provisioning/activate")
    public Map<String, Object> activateService(@RequestBody Map<String, String> payload) {
        System.out.println("[Mock Provisioning] Activating service: " + payload);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("serviceId", payload.get("serviceType"));
        response.put("transactionId", "PROV-" + System.currentTimeMillis());
        return response;
    }

    // --- Mock Payment Gateway ---
    @PostMapping("/payment/process")
    public Map<String, Object> processPayment(@RequestBody Map<String, Object> payload) {
        System.out.println("[Mock Payment] Processing payment: " + payload);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "APPROVED");
        response.put("transactionId", "PAY-" + System.currentTimeMillis());
        response.put("amount", payload.get("amount"));
        return response;
    }
}
