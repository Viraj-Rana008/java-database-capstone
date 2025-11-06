package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final com.project.back_end.services.Service service;

    @Autowired
    public AppointmentController(AppointmentService appointmentService, com.project.back_end.services.Service service) {
        this.appointmentService = appointmentService;
        this.service = service;
    }

    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<?> getAppointments(
            @PathVariable String date,
            @PathVariable String patientName,
            @PathVariable String token) {
        
        // Validate token for doctor role
        ResponseEntity<Map<String, String>> validationResult = service.validateToken(token, "doctor");
        
        if (validationResult.getStatusCode() != HttpStatus.OK) {
            return validationResult;
        }
        
        // Fetch appointments for the given date and patient name
        LocalDate appointmentDate = LocalDate.parse(date);
        Map<String, Object> appointments = appointmentService.getAppointment(patientName, appointmentDate, token);
        
        return ResponseEntity.ok(appointments);
    }

    @PostMapping("/{token}")
    public ResponseEntity<?> bookAppointment(
            @RequestBody Appointment appointment,
            @PathVariable String token) {
        
        // Validate token for patient role
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }
        
        // Validate appointment data
        int appointmentValidation = service.validateAppointment(
            appointment.getDoctor().getId(),
            appointment.getAppointmentTime()
        );
        
        if (appointmentValidation == -1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid doctor ID"));
        } else if (appointmentValidation == 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Appointment slot already booked"));
        }
        
        // Book the appointment
        int result = appointmentService.bookAppointment(appointment);
        
        if (result == 1) {
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Appointment booked successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to book appointment"));
        }
    }

    @PutMapping("/{token}")
    public ResponseEntity<?> updateAppointment(
            @RequestBody Appointment appointment,
            @PathVariable String token) {
        
        // Validate token for patient role
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }
        
        // Update the appointment
        return appointmentService.updateAppointment(appointment);
    }

    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<?> cancelAppointment(
            @PathVariable Long id,
            @PathVariable String token) {
        
        // Validate token for patient role
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }
        
        // Cancel the appointment
        return appointmentService.cancelAppointment(id, token);
    }
}
