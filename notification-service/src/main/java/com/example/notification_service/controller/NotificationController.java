package com.example.notification_service.controller;

import com.example.notification_service.dto.NotificationRequest;
import com.example.notification_service.dto.NotificationResponse;
import com.example.notification_service.model.Notification;
import com.example.notification_service.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/send")
    public ResponseEntity<NotificationResponse> send(@RequestBody NotificationRequest request) {
        NotificationResponse response = notificationService.send(request);
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @PostMapping("/send/email")
    public ResponseEntity<NotificationResponse> sendEmail(
            @RequestParam String recipient,
            @RequestParam String subject,
            @RequestParam String message) {
        NotificationResponse response = notificationService.sendEmail(recipient, subject, message);
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @PostMapping("/send/sms")
    public ResponseEntity<NotificationResponse> sendSMS(
            @RequestParam String recipient,
            @RequestParam String message) {
        NotificationResponse response = notificationService.sendSMS(recipient, message);
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @PostMapping("/send/alert")
    public ResponseEntity<NotificationResponse> sendAlert(
            @RequestParam String recipient,
            @RequestParam String message) {
        NotificationResponse response = notificationService.sendAlert(recipient, message);
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notification> getById(@PathVariable Long id) {
        Notification notification = notificationService.findById(id);
        if (notification != null) {
            return ResponseEntity.ok(notification);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAll() {
        return ResponseEntity.ok(notificationService.findAll());
    }

    @GetMapping("/recipient/{recipient}")
    public ResponseEntity<List<Notification>> getByRecipient(@PathVariable String recipient) {
        List<Notification> notifications = notificationService.findByRecipient(recipient);
        return ResponseEntity.ok(notifications);
    }
}
