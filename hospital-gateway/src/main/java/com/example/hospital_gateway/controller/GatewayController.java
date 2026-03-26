package com.example.hospital_gateway.controller;

import com.example.hospital_gateway.dto.*;
import com.example.hospital_gateway.service.HospitalGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gateway")
public class GatewayController {

    @Autowired
    private HospitalGatewayService gatewayService;

    // ==================== ENDPOINTS INDIVIDUALES ====================

    /**
     * Proxy para autenticación
     */
    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = gatewayService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Proxy para validar token
     */
    @GetMapping("/auth/validate/{token}")
    public ResponseEntity<Boolean> validateToken(@PathVariable String token) {
        Boolean isValid = gatewayService.validateToken(token);
        return ResponseEntity.ok(isValid);
    }

    /**
     * Proxy para registrar paciente
     */
    @PostMapping("/patients/register")
    public ResponseEntity<Patient> registerPatient(@RequestBody PatientDTO patientDTO) {
        Patient patient = gatewayService.registerPatient(patientDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(patient);
    }

    /**
     * Proxy para obtener paciente
     */
    @GetMapping("/patients/{id}")
    public ResponseEntity<Patient> getPatient(@PathVariable Long id) {
        Patient patient = gatewayService.getPatientById(id);
        if (patient != null) {
            return ResponseEntity.ok(patient);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Proxy para obtener todos los pacientes
     */
    @GetMapping("/patients")
    public ResponseEntity<Patient[]> getAllPatients() {
        Patient[] patients = gatewayService.getAllPatients();
        return ResponseEntity.ok(patients);
    }

    /**
     * Proxy para enviar notificación
     */
    @PostMapping("/notifications/send")
    public ResponseEntity<NotificationResponse> sendNotification(@RequestBody NotificationRequest request) {
        NotificationResponse response = gatewayService.sendNotification(request);
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // ==================== ENDPOINTS DE COMPOSICIÓN (SOA) ====================

    /**
     * COMPOSICIÓN 1: Autenticación + Registro de Paciente + Notificación
     * Este endpoint demuestra el principio SOA de composición de servicios
     * Orquesta 3 servicios en una sola operación:
     * 1. Autentica al usuario (auth-service)
     * 2. Registra al paciente (patient-service)
     * 3. Envía notificación de bienvenida (notification-service)
     */
    @PostMapping("/secure-patient-registration")
    public ResponseEntity<RegistrationWithNotificationResponse> securePatientRegistration(
            @RequestBody SecurePatientRegistrationRequest request) {

        RegistrationWithNotificationResponse response = gatewayService.securePatientRegistration(request);

        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * COMPOSICIÓN 2: Consultar Paciente + Enviar Notificación
     * Obtiene los datos de un paciente y le envía una notificación personalizada
     * Combina patient-service y notification-service
     */
    @PostMapping("/notify-patient/{patientId}")
    public ResponseEntity<NotificationResponse> notifyPatient(
            @PathVariable Long patientId,
            @RequestParam String subject,
            @RequestParam String message) {

        NotificationResponse response = gatewayService.notifyPatient(patientId, subject, message);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * COMPOSICIÓN 3: Notificación Masiva a Todos los Pacientes
     * Obtiene todos los pacientes y envía una notificación a cada uno
     * Combina patient-service y notification-service
     */
    @PostMapping("/notify-all-patients")
    public ResponseEntity<String> notifyAllPatients(
            @RequestParam String subject,
            @RequestParam String message) {

        String result = gatewayService.notifyAllPatients(subject, message);
        return ResponseEntity.ok(result);
    }

    /**
     * Endpoint de prueba del gateway
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Hospital Gateway is running on port 8080");
    }
}
