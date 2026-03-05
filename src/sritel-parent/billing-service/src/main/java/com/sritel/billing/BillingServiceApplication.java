package com.sritel.billing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.sritel.billing.model.Payment;
import com.sritel.billing.repo.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.context.annotation.Bean;
import java.math.BigDecimal;
import java.util.*;

@SpringBootApplication
@RestController
@RequestMapping("/billing")
public class BillingServiceApplication {

    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;

    public BillingServiceApplication(PaymentRepository paymentRepository, RestTemplate restTemplate) {
        this.paymentRepository = paymentRepository;
        this.restTemplate = restTemplate;
    }

    public static void main(String[] args) {
        SpringApplication.run(BillingServiceApplication.class, args);
    }

    // Config
    @Value("${PAYMENT_URL:http://localhost:8081/payment/process}")
    private String paymentUrl;

    @Value("${PROVISION_URL:http://localhost:8081/provisioning/activate}")
    private String provisionUrl;

    @Value("${NOTIFICATION_URL:http://localhost:8084/notify}")
    private String notificationUrl;

    @PostMapping("/pay")
    public Map<String, Object> payBill(@RequestBody Map<String, Object> paymentDetails) {
        // 1. Call External Payment Gateway
        Map<String, Object> paymentResponse = restTemplate.postForObject(paymentUrl, paymentDetails, Map.class);
        
        // 2. If Success, Notify User
        if ("APPROVED".equals(paymentResponse.get("status"))) {
             Map<String, String> notif = new HashMap<>();
             notif.put("userId", (String) paymentDetails.get("userId"));
             notif.put("message", "Payment Successful: " + paymentDetails.get("amount"));
             notif.put("type", "EMAIL");
             
             try {
                restTemplate.postForObject(notificationUrl, notif, String.class);
             } catch (Exception e) {
                 System.out.println("Notification failed (Service Down?), but payment processed.");
             }
        }

        // 3. Persist payment to PostgreSQL
        Payment payment = new Payment();
        payment.setUserId((String) paymentDetails.get("userId"));
        Object amount = paymentDetails.get("amount");
        if (amount != null) {
            payment.setAmount(new BigDecimal(amount.toString()));
        }
        payment.setStatus(String.valueOf(paymentResponse.get("status")));
        payment.setTransactionId(String.valueOf(paymentResponse.get("transactionId")));
        paymentRepository.save(payment);
        
        return paymentResponse;
    }

    @PostMapping("/activate-service")
    public Map<String, Object> activateService(@RequestBody Map<String, String> activationDetails) {
        // Call Provisioning System
        return restTemplate.postForObject(provisionUrl, activationDetails, Map.class);
    }
}
