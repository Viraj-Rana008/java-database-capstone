package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@org.springframework.stereotype.Service
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    @Autowired
    public Service(TokenService tokenService,
                   AdminRepository adminRepository,
                   DoctorRepository doctorRepository,
                   PatientRepository patientRepository,
                   DoctorService doctorService,
                   PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        Map<String, String> response = new HashMap<>();
        try {
            boolean isValid = tokenService.validateToken(token, user);
            
            if (!isValid) {
                response.put("error", "Invalid or expired token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Token validation failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();
        try {
            Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());
            
            if (admin == null) {
                response.put("error", "Admin not found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            if (!admin.getPassword().equals(receivedAdmin.getPassword())) {
                response.put("error", "Invalid password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = tokenService.generateToken(admin.getUsername());
            response.put("token", token);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public Map<String, Object> filterDoctor(String name, String specialty, String time) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // All three filters provided
            if (name != null && !name.isEmpty() && specialty != null && !specialty.isEmpty() && time != null && !time.isEmpty()) {
                return doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);
            }
            // Name and specialty
            else if (name != null && !name.isEmpty() && specialty != null && !specialty.isEmpty()) {
                return doctorService.filterDoctorByNameAndSpecility(name, specialty);
            }
            // Name and time
            else if (name != null && !name.isEmpty() && time != null && !time.isEmpty()) {
                return doctorService.filterDoctorByNameAndTime(name, time);
            }
            // Specialty and time
            else if (specialty != null && !specialty.isEmpty() && time != null && !time.isEmpty()) {
                return doctorService.filterDoctorByTimeAndSpecility(specialty, time);
            }
            // Only name
            else if (name != null && !name.isEmpty()) {
                return doctorService.findDoctorByName(name);
            }
            // Only specialty
            else if (specialty != null && !specialty.isEmpty()) {
                return doctorService.filterDoctorBySpecility(specialty);
            }
            // Only time
            else if (time != null && !time.isEmpty()) {
                return doctorService.filterDoctorsByTime(time);
            }
            // No filters - return all doctors
            else {
                response.put("doctors", doctorService.getDoctors());
                return response;
            }
        } catch (Exception e) {
            response.put("error", "Failed to filter doctors");
            return response;
        }
    }

    public int validateAppointment(Long doctorId, java.time.LocalDateTime appointmentTime) {
        try {
            Optional<com.project.back_end.models.Doctor> doctorOpt = doctorRepository.findById(doctorId);
            
            if (doctorOpt.isEmpty()) {
                return -1; // Doctor doesn't exist
            }

            LocalDate appointmentDate = appointmentTime.toLocalDate();
            List<String> availableSlots = doctorService.getDoctorAvailability(doctorId, appointmentDate);
            
            String requestedTime = appointmentTime.toLocalTime().toString();
            
            if (availableSlots.contains(requestedTime)) {
                return 1; // Valid appointment time
            } else {
                return 0; // Time unavailable
            }
        } catch (Exception e) {
            return 0; // Error occurred, treat as unavailable
        }
    }

    public boolean validatePatient(Patient patient) {
        try {
            Patient existingPatient = patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone());
            
            if (existingPatient != null) {
                return false; // Patient exists
            }
            
            return true; // Patient does not exist, valid for registration
        } catch (Exception e) {
            return false;
        }
    }

    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Map<String, String> response = new HashMap<>();
        try {
            Patient patient = patientRepository.findByEmail(login.getIdentifier());
            
            if (patient == null) {
                response.put("error", "Patient not found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            if (!patient.getPassword().equals(login.getPassword())) {
                response.put("error", "Invalid password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = tokenService.generateToken(patient.getEmail());
            response.put("token", token);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {
        try {
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);

            if (patient == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Patient not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            Long patientId = patient.getId();

            // Both condition and doctor name provided
            if (condition != null && !condition.isEmpty() && name != null && !name.isEmpty()) {
                return patientService.filterByDoctorAndCondition(condition, name, patientId);
            }
            // Only condition
            else if (condition != null && !condition.isEmpty()) {
                return patientService.filterByCondition(condition, patientId);
            }
            // Only doctor name
            else if (name != null && !name.isEmpty()) {
                return patientService.filterByDoctor(name, patientId);
            }
            // No filters - return all appointments
            else {
                return patientService.getPatientAppointment(patientId, token);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to filter patient appointments");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
