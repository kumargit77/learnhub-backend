package com.learnhub.controller;

import com.learnhub.model.User;
import com.learnhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // In production configure properly
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> registerStudent(@RequestBody User user) {
        if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email already exists"));
        }
        
        user.setRole("STUDENT");
        // In real app, password must be hashed using BCrypt.
        userRepository.save(user);
        
        return ResponseEntity.ok(Map.of("message", "Account created successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        // Basic Admin Check (Hardcoded as per requirement/schema logic)
        if ("admin".equals(email) && "Admin@2026".equals(password)) {
            Map<String, Object> adminResponse = new HashMap<>();
            adminResponse.put("message", "Admin login successful");
            adminResponse.put("role", "ADMIN");
            return ResponseEntity.ok(adminResponse);
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Basic plain text check - use BCrypt in production
            if (user.getPassword().equals(password)) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Login successful");
                response.put("role", user.getRole());
                response.put("userId", user.getId());
                response.put("name", user.getFirstName() + " " + user.getLastName());
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
    }
}
