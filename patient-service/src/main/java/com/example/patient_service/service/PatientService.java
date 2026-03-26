package com.example.patient_service.service;

import com.example.patient_service.dto.PatientDTO;
import com.example.patient_service.model.Patient;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PatientService {

    // Simulación de base de datos en memoria
    private final Map<Long, Patient> patients = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public PatientService() {
        // Pacientes de prueba
        patients.put(1L, new Patient(1L, "Juan", "Pérez", "juan.perez@email.com", "555-1234", 35, "Calle Principal 123", "O+"));
        patients.put(2L, new Patient(2L, "María", "González", "maria.gonzalez@email.com", "555-5678", 28, "Avenida Central 456", "A+"));
        patients.put(3L, new Patient(3L, "Carlos", "Rodríguez", "carlos.rodriguez@email.com", "555-9012", 42, "Boulevard Norte 789", "B+"));
        idGenerator.set(4L);
    }

    public Patient register(PatientDTO dto) {
        Long id = idGenerator.getAndIncrement();
        Patient patient = new Patient(
            id,
            dto.getFirstName(),
            dto.getLastName(),
            dto.getEmail(),
            dto.getPhone(),
            dto.getAge(),
            dto.getAddress(),
            dto.getBloodType()
        );
        patients.put(id, patient);
        return patient;
    }

    public Patient findById(Long id) {
        return patients.get(id);
    }

    public List<Patient> findAll() {
        return new ArrayList<>(patients.values());
    }

    public Patient update(Long id, PatientDTO dto) {
        Patient patient = patients.get(id);
        if (patient != null) {
            patient.setFirstName(dto.getFirstName());
            patient.setLastName(dto.getLastName());
            patient.setEmail(dto.getEmail());
            patient.setPhone(dto.getPhone());
            patient.setAge(dto.getAge());
            patient.setAddress(dto.getAddress());
            patient.setBloodType(dto.getBloodType());
            patients.put(id, patient);
        }
        return patient;
    }

    public boolean delete(Long id) {
        return patients.remove(id) != null;
    }
}
