package com.example.notification_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private Long id;
    private String recipient;
    private String subject;
    private String message;
    private String type; // EMAIL, SMS, ALERT
    private String status; // SENT, PENDING, FAILED
    private LocalDateTime sentAt;
}
