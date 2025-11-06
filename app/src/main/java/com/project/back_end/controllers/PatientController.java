package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final com.project.back_end.services.Service service;

    @Autowired
    public PatientController(PatientService patientService, com.project.back_end.services.Service service) {
        this.patientService = patientService;
        this.service = service;
    }

    @GetMapping("/{token}")
    public ResponseEntity<?> getPatient(@PathVariable String token) {
        
        // Validate token for patient role
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }
        
        // Get patient details
        return patientService.getPatientDetails(token);
    }

    @PostMapping
    public ResponseEntity<?> createPatient(@RequestBody Patient patient) {
        
        // Validate if patient already exists
        boolean isValid = service.validatePatient(patient);
        
        if (!isValid) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Patient with email id or phone no already exist"));
        }
        
        // Attempt to create patient
        int result = patientService.createPatient(patient);
        
        if (result == 1) {
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Signup successful"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Login login) {
        return service.validatePatientLogin(login);
    }

    @GetMapping("/{id}/{token}")
    public ResponseEntity<?> getPatientAppointment(
            @PathVariable Long id,
            @PathVariable String token) {
        
        // Validate token for patient role
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }
        
        // Get patient appointments
        return patientService.getPatientAppointment(id, token);
    }

    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<?> filterPatientAppointment(
            @PathVariable String condition,
            @PathVariable String name,
            @PathVariable String token) {
        
        // Validate token for patient role
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }
        
        // Filter patient appointments
        return service.filterPatient(condition, name, token);
    }
}


