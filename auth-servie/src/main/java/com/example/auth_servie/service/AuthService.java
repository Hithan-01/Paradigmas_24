package com.example.auth_servie.service;

import com.example.auth_servie.dto.AuthResponse;
import com.example.auth_servie.dto.LoginRequest;
import com.example.auth_servie.model.User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthService {

    // Simulación de base de datos en memoria
    private final Map<String, User> users = new HashMap<>();
    private final Map<String, String> tokens = new HashMap<>(); // token -> username

    public AuthService() {
        // Usuarios de prueba
        users.put("admin", new User(1L, "admin", "admin123", "admin@hospital.com", "ADMIN"));
        users.put("doctor", new User(2L, "doctor", "doctor123", "doctor@hospital.com", "DOCTOR"));
        users.put("nurse", new User(3L, "nurse", "nurse123", "nurse@hospital.com", "NURSE"));
    }

    public AuthResponse authenticate(LoginRequest request) {
        User user = users.get(request.getUsername());

        if (user == null) {
            return new AuthResponse(null, request.getUsername(), false, "Usuario no encontrado");
        }

        if (!user.getPassword().equals(request.getPassword())) {
            return new AuthResponse(null, request.getUsername(), false, "Contraseña incorrecta");
        }

        // Generar token simulado (en producción usarías JWT)
        String token = "TOKEN-" + UUID.randomUUID().toString();
        tokens.put(token, user.getUsername());

        return new AuthResponse(token, user.getUsername(), true, "Autenticación exitosa");
    }

    public boolean validate(String token) {
        return tokens.containsKey(token);
    }

    public String getUsernameFromToken(String token) {
        return tokens.get(token);
    }
}
