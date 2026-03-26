package com.example.patient_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Integer age;
    private String address;
    private String bloodType;
}
