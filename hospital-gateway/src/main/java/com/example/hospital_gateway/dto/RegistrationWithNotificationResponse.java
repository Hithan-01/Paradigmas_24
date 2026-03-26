package com.example.hospital_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationWithNotificationResponse {
    private AuthResponse authResponse;
    private Patient patient;
    private NotificationResponse notificationResponse;
    private String message;
    private boolean success;
}
