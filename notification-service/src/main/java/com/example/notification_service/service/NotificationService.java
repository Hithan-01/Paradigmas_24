package com.example.notification_service.service;

import com.example.notification_service.dto.NotificationRequest;
import com.example.notification_service.dto.NotificationResponse;
import com.example.notification_service.model.Notification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class NotificationService {

    // Simulación de base de datos en memoria
    private final Map<Long, Notification> notifications = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public NotificationService() {
        // Notificaciones de prueba
        notifications.put(1L, new Notification(1L, "juan.perez@email.com", "Cita confirmada",
            "Su cita ha sido confirmada para el 25/03/2026", "EMAIL", "SENT", LocalDateTime.now()));
        notifications.put(2L, new Notification(2L, "555-1234", "Recordatorio",
            "Recordatorio: Cita médica mañana a las 10:00 AM", "SMS", "SENT", LocalDateTime.now()));
    }

    public NotificationResponse send(NotificationRequest request) {
        try {
            Long id = idGenerator.getAndIncrement();

            // Simulación de envío de notificación
            Notification notification = new Notification(
                id,
                request.getRecipient(),
                request.getSubject(),
                request.getMessage(),
                request.getType(),
                "SENT",
                LocalDateTime.now()
            );

            notifications.put(id, notification);

            // Simular envío según el tipo
            String result = simulateSending(request.getType(), request.getRecipient());

            return new NotificationResponse(id, "SENT", result, true);

        } catch (Exception e) {
            return new NotificationResponse(null, "FAILED",
                "Error al enviar notificación: " + e.getMessage(), false);
        }
    }

    public NotificationResponse sendEmail(String recipient, String subject, String message) {
        NotificationRequest request = new NotificationRequest(recipient, subject, message, "EMAIL");
        return send(request);
    }

    public NotificationResponse sendSMS(String recipient, String message) {
        NotificationRequest request = new NotificationRequest(recipient, "SMS", message, "SMS");
        return send(request);
    }

    public NotificationResponse sendAlert(String recipient, String message) {
        NotificationRequest request = new NotificationRequest(recipient, "Alerta del Sistema", message, "ALERT");
        return send(request);
    }

    public Notification findById(Long id) {
        return notifications.get(id);
    }

    public List<Notification> findAll() {
        return new ArrayList<>(notifications.values());
    }

    public List<Notification> findByRecipient(String recipient) {
        return notifications.values().stream()
            .filter(n -> n.getRecipient().equals(recipient))
            .toList();
    }

    private String simulateSending(String type, String recipient) {
        return switch (type) {
            case "EMAIL" -> "Email enviado exitosamente a " + recipient;
            case "SMS" -> "SMS enviado exitosamente a " + recipient;
            case "ALERT" -> "Alerta enviada exitosamente a " + recipient;
            default -> "Notificación enviada a " + recipient;
        };
    }
}
