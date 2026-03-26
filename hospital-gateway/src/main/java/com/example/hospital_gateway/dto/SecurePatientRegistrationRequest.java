package com.example.hospital_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurePatientRegistrationRequest {
    private LoginRequest credentials;
    private PatientDTO patientData;
}
