package com.sritel.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.sritel.user.model.User;
import com.sritel.user.repo.UserRepository;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@SpringBootApplication
@RestController
@RequestMapping("/users")
public class UserServiceApplication {
    private final UserRepository userRepository;

    public UserServiceApplication(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @PostMapping("/register")
    public Map<String, String> register(@RequestBody Map<String, String> user) {
        String nic = user.get("nic");
        if (nic == null || nic.isBlank()) {
            throw new RuntimeException("NIC is required");
        }
        if (userRepository.existsById(nic)) {
            throw new RuntimeException("User already exists");
        }
        User newUser = new User(nic, user.get("password"), user.get("email"));
        userRepository.save(newUser);
        System.out.println("[User Service] Registered: " + nic); // Log
        return Map.of("status", "REGISTERED", "userId", nic);
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> credentials) {
        String nic = credentials.get("username");
        String pass = credentials.get("password");

        Optional<User> userOpt = userRepository.findById(nic);
        if (userOpt.isPresent() && pass != null && pass.equals(userOpt.get().getPassword())) {
            System.out.println("[User Service] Login Success: " + nic);
            return Map.of("status", "SUCCESS", "token", "mock-jwt-token-" + nic);
        }
        return Map.of("status", "FAILED");
    }
    
    @GetMapping("/{id}")
    public Map<String, String> getUser(@PathVariable String id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return Map.of("error", "Not Found");
        }
        User user = userOpt.get();
        return Map.of("nic", user.getNic(), "email", user.getEmail());
    }
}
