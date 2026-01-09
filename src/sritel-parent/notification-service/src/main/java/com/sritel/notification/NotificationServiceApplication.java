package com.sritel.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@SpringBootApplication
@RestController
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

    // In a real microservices architecture, this would consume from a Queue (Kafka/RabbitMQ)
    // For this prototype, we mock it with a REST endpoint that acts as the trigger.
    @PostMapping("/notify")
    public String sendNotification(@RequestBody Map<String, String> notification) {
        System.out.println("------------------------------------------------");
        System.out.println("[Notification Service] SENDING ALERT");
        System.out.println("To: " + notification.get("userId"));
        System.out.println("Type: " + notification.get("type"));
        System.out.println("Message: " + notification.get("message"));
        System.out.println("------------------------------------------------");
        return "QUEUED";
    }
}
