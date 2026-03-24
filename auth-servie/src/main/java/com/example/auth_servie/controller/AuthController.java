package com.example.auth_servie.controller;

import com.example.auth_servie.dto.AuthResponse;
import com.example.auth_servie.dto.LoginRequest;
import com.example.auth_servie.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate/{token}")
    public ResponseEntity<Boolean> validateToken(@PathVariable String token) {
        boolean isValid = authService.validate(token);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/user/{token}")
    public ResponseEntity<String> getUserFromToken(@PathVariable String token) {
        String username = authService.getUsernameFromToken(token);
        if (username != null) {
            return ResponseEntity.ok(username);
        }
        return ResponseEntity.notFound().build();
    }
}
