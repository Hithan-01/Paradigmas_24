package com.example.hospital_gateway.service;

import com.example.hospital_gateway.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HospitalGatewayService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${service.auth.url}")
    private String authServiceUrl;

    @Value("${service.patient.url}")
    private String patientServiceUrl;

    @Value("${service.notification.url}")
    private String notificationServiceUrl;

    // ==================== AUTH SERVICE ====================

    public AuthResponse login(LoginRequest request) {
        String url = authServiceUrl + "/api/auth/login";
        return restTemplate.postForObject(url, request, AuthResponse.class);
    }

    public Boolean validateToken(String token) {
        String url = authServiceUrl + "/api/auth/validate/" + token;
        return restTemplate.getForObject(url, Boolean.class);
    }

    // ==================== PATIENT SERVICE ====================

    public Patient registerPatient(PatientDTO patientDTO) {
        String url = patientServiceUrl + "/api/patients/register";
        return restTemplate.postForObject(url, patientDTO, Patient.class);
    }

    public Patient getPatientById(Long id) {
        String url = patientServiceUrl + "/api/patients/" + id;
        return restTemplate.getForObject(url, Patient.class);
    }

    public Patient[] getAllPatients() {
        String url = patientServiceUrl + "/api/patients";
        return restTemplate.getForObject(url, Patient[].class);
    }

    // ==================== NOTIFICATION SERVICE ====================

    public NotificationResponse sendNotification(NotificationRequest request) {
        String url = notificationServiceUrl + "/api/notifications/send";
        return restTemplate.postForObject(url, request, NotificationResponse.class);
    }

    public NotificationResponse sendEmail(String recipient, String subject, String message) {
        String url = notificationServiceUrl + "/api/notifications/send/email" +
                     "?recipient=" + recipient +
                     "&subject=" + subject +
                     "&message=" + message;
        return restTemplate.postForObject(url, null, NotificationResponse.class);
    }

    // ==================== COMPOSICIÓN DE SERVICIOS (SOA) ====================

    /**
     * Operación compuesta 1: Autenticación + Registro de paciente
     * Primero autentica al usuario, y si es exitoso, registra al paciente
     */
    public RegistrationWithNotificationResponse securePatientRegistration(SecurePatientRegistrationRequest request) {
        RegistrationWithNotificationResponse response = new RegistrationWithNotificationResponse();

        try {
            // 1. Autenticar usuario
            AuthResponse authResponse = login(request.getCredentials());
            response.setAuthResponse(authResponse);

            if (!authResponse.isAuthenticated()) {
                response.setSuccess(false);
                response.setMessage("Autenticación fallida. No se puede registrar paciente.");
                return response;
            }

            // 2. Registrar paciente
            Patient patient = registerPatient(request.getPatientData());
            response.setPatient(patient);

            // 3. Enviar notificación de bienvenida
            NotificationResponse notificationResponse = sendEmail(
                patient.getEmail(),
                "Bienvenido al Hospital",
                "Estimado/a " + patient.getFirstName() + " " + patient.getLastName() +
                ", su registro ha sido exitoso. ID: " + patient.getId()
            );
            response.setNotificationResponse(notificationResponse);

            response.setSuccess(true);
            response.setMessage("Registro completo: Autenticado, paciente registrado y notificación enviada.");

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error en el proceso: " + e.getMessage());
        }

        return response;
    }

    /**
     * Operación compuesta 2: Consultar paciente + Enviar notificación
     * Obtiene los datos de un paciente y le envía una notificación
     */
    public NotificationResponse notifyPatient(Long patientId, String subject, String message) {
        try {
            // 1. Obtener datos del paciente
            Patient patient = getPatientById(patientId);

            if (patient == null) {
                return new NotificationResponse(null, "FAILED",
                    "Paciente no encontrado", false);
            }

            // 2. Enviar notificación al email del paciente
            String personalizedMessage = "Estimado/a " + patient.getFirstName() +
                                        " " + patient.getLastName() + ", " + message;

            return sendEmail(patient.getEmail(), subject, personalizedMessage);

        } catch (Exception e) {
            return new NotificationResponse(null, "FAILED",
                "Error al notificar paciente: " + e.getMessage(), false);
        }
    }

    /**
     * Operación compuesta 3: Listar pacientes + Notificar a todos
     * Obtiene todos los pacientes y envía una notificación masiva
     */
    public String notifyAllPatients(String subject, String message) {
        try {
            // 1. Obtener todos los pacientes
            Patient[] patients = getAllPatients();

            int successCount = 0;
            int failCount = 0;

            // 2. Enviar notificación a cada paciente
            for (Patient patient : patients) {
                try {
                    String personalizedMessage = "Estimado/a " + patient.getFirstName() +
                                                " " + patient.getLastName() + ", " + message;
                    NotificationResponse response = sendEmail(
                        patient.getEmail(),
                        subject,
                        personalizedMessage
                    );

                    if (response.isSuccess()) {
                        successCount++;
                    } else {
                        failCount++;
                    }
                } catch (Exception e) {
                    failCount++;
                }
            }

            return "Notificaciones enviadas: " + successCount + " exitosas, " + failCount + " fallidas.";

        } catch (Exception e) {
            return "Error al notificar pacientes: " + e.getMessage();
        }
    }
}
