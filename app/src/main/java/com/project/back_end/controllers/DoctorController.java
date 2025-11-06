package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final com.project.back_end.services.Service service;

    @Autowired
    public DoctorController(DoctorService doctorService, com.project.back_end.services.Service service) {
        this.doctorService = doctorService;
        this.service = service;
    }

    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<?> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable String date,
            @PathVariable String token) {
        
        // Validate token
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, user);
        
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }
        
        // Get doctor availability
        LocalDate appointmentDate = LocalDate.parse(date);
        List<String> availability = doctorService.getDoctorAvailability(doctorId, appointmentDate);
        
        Map<String, Object> response = new HashMap<>();
        response.put("availableSlots", availability);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctor() {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorService.getDoctors();
        response.put("doctors", doctors);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{token}")
    public ResponseEntity<?> saveDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token) {
        
        // Validate token for admin role
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "admin");
        
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }
        
        // Attempt to save doctor
        int result = doctorService.saveDoctor(doctor);
        
        if (result == 1) {
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Doctor added to db"));
        } else if (result == -1) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Doctor already exists"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Some internal error occurred"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> doctorLogin(@RequestBody Login login) {
        return doctorService.validateDoctor(login);
    }

    @PutMapping("/{token}")
    public ResponseEntity<?> updateDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token) {
        
        // Validate token for admin role
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "admin");
        
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }
        
        // Attempt to update doctor
        int result = doctorService.updateDoctor(doctor);
        
        if (result == 1) {
            return ResponseEntity.ok(Map.of("message", "Doctor updated"));
        } else if (result == -1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Doctor not found"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Some internal error occurred"));
        }
    }

    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<?> deleteDoctor(
            @PathVariable Long id,
            @PathVariable String token) {
        
        // Validate token for admin role
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "admin");
        
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }
        
        // Attempt to delete doctor
        int result = doctorService.deleteDoctor(id);
        
        if (result == 1) {
            return ResponseEntity.ok(Map.of("message", "Doctor deleted successfully"));
        } else if (result == -1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Doctor not found with id " + id));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Some internal error occurred"));
        }
    }

    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, Object>> filter(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String speciality) {
        
        Map<String, Object> response = service.filterDoctor(name, time, speciality);
        return ResponseEntity.ok(response);
    }
}