package com.sritel.billing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.context.annotation.Bean;
import java.util.*;

@SpringBootApplication
@RestController
@RequestMapping("/billing")
public class BillingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BillingServiceApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    private final RestTemplate restTemplate = new RestTemplate();
    
    // Config
    private String PAYMENT_URL = "http://localhost:8081/payment/process";
    private String PROVISION_URL = "http://localhost:8081/provisioning/activate";
    private String NOTIFICATION_URL = "http://localhost:8084/notify";

    @PostMapping("/pay")
    public Map<String, Object> payBill(@RequestBody Map<String, Object> paymentDetails) {
        // 1. Call External Payment Gateway
        Map<String, Object> paymentResponse = restTemplate.postForObject(PAYMENT_URL, paymentDetails, Map.class);
        
        // 2. If Success, Notify User
        if ("APPROVED".equals(paymentResponse.get("status"))) {
             Map<String, String> notif = new HashMap<>();
             notif.put("userId", (String) paymentDetails.get("userId"));
             notif.put("message", "Payment Successful: " + paymentDetails.get("amount"));
             notif.put("type", "EMAIL");
             
             try {
                restTemplate.postForObject(NOTIFICATION_URL, notif, String.class);
             } catch (Exception e) {
                 System.out.println("Notification failed (Service Down?), but payment processed.");
             }
        }
        
        return paymentResponse;
    }

    @PostMapping("/activate-service")
    public Map<String, Object> activateService(@RequestBody Map<String, String> activationDetails) {
        // Call Provisioning System
        return restTemplate.postForObject(PROVISION_URL, activationDetails, Map.class);
    }
}
