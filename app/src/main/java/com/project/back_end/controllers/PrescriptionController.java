package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final com.project.back_end.services.Service service;

    @Autowired
    public PrescriptionController(PrescriptionService prescriptionService, 
                                   com.project.back_end.services.Service service) {
        this.prescriptionService = prescriptionService;
        this.service = service;
    }

    @PostMapping("/{token}")
    public ResponseEntity<?> savePrescription(
            @RequestBody Prescription prescription,
            @PathVariable String token) {
        
        // Validate token for doctor role
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "doctor");
        
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }
        
        // Save prescription
        return prescriptionService.savePrescription(prescription);
    }

    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> getPrescription(
            @PathVariable Long appointmentId,
            @PathVariable String token) {
        
        // Validate token for doctor role
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "doctor");
        
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }
        
        // Get prescription by appointment ID
        return prescriptionService.getPrescription(appointmentId);
    }
}
