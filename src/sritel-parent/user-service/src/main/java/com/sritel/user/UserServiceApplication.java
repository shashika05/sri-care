package com.sritel.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@SpringBootApplication
@RestController
@RequestMapping("/users")
public class UserServiceApplication {

    // Simple In-Memory DB
    private Map<String, Map<String, String>> userDb = new HashMap<>();

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @PostMapping("/register")
    public Map<String, String> register(@RequestBody Map<String, String> user) {
        String nic = user.get("nic");
        if (userDb.containsKey(nic)) {
            throw new RuntimeException("User already exists");
        }
        userDb.put(nic, user);
        System.out.println("[User Service] Registered: " + nic); // Log
        return Map.of("status", "REGISTERED", "userId", nic);
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> credentials) {
        String nic = credentials.get("username");
        String pass = credentials.get("password");
        
        // Mock password check
        if (userDb.containsKey(nic) && userDb.get(nic).get("password").equals(pass)) {
             System.out.println("[User Service] Login Success: " + nic);
             return Map.of("status", "SUCCESS", "token", "mock-jwt-token-" + nic);
        }
        return Map.of("status", "FAILED");
    }
    
    @GetMapping("/{id}")
    public Map<String, String> getUser(@PathVariable String id) {
        return userDb.getOrDefault(id, Map.of("error", "Not Found"));
    }
}
