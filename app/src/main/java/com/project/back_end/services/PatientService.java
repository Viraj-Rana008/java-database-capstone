package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    @Autowired
    public PatientService(PatientRepository patientRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    @Transactional
    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);

            if (patient == null) {
                response.put("error", "Patient not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if (!patient.getId().equals(id)) {
                response.put("error", "Unauthorized access");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            List<Appointment> appointments = appointmentRepository.findByPatientId(id);
            List<AppointmentDTO> appointmentDTOs = appointments.stream()
                .map(app -> new AppointmentDTO(
                    app.getId(),
                    app.getDoctor().getId(),
                    app.getDoctor().getName(),
                    app.getPatient().getId(),
                    app.getPatient().getName(),
                    app.getPatient().getEmail(),
                    app.getPatient().getPhone(),
                    app.getPatient().getAddress(),
                    app.getAppointmentTime(),
                    app.getStatus()
                ))
                .collect(Collectors.toList());

            response.put("appointments", appointmentDTOs);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Failed to retrieve appointments: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            int status;
            if ("past".equalsIgnoreCase(condition)) {
                status = 1;
            } else if ("future".equalsIgnoreCase(condition)) {
                status = 0;
            } else {
                response.put("error", "Invalid condition. Use 'past' or 'future'");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            List<Appointment> appointments = appointmentRepository.findByPatient_IdAndStatusOrderByAppointmentTimeAsc(id, status);
            List<AppointmentDTO> appointmentDTOs = appointments.stream()
                .map(app -> new AppointmentDTO(
                    app.getId(),
                    app.getDoctor().getId(),
                    app.getDoctor().getName(),
                    app.getPatient().getId(),
                    app.getPatient().getName(),
                    app.getPatient().getEmail(),
                    app.getPatient().getPhone(),
                    app.getPatient().getAddress(),
                    app.getAppointmentTime(),
                    app.getStatus()
                ))
                .collect(Collectors.toList());

            response.put("appointments", appointmentDTOs);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Failed to filter appointments: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Appointment> appointments = appointmentRepository.filterByDoctorNameAndPatientId(name, patientId);
            List<AppointmentDTO> appointmentDTOs = appointments.stream()
                .map(app -> new AppointmentDTO(
                    app.getId(),
                    app.getDoctor().getId(),
                    app.getDoctor().getName(),
                    app.getPatient().getId(),
                    app.getPatient().getName(),
                    app.getPatient().getEmail(),
                    app.getPatient().getPhone(),
                    app.getPatient().getAddress(),
                    app.getAppointmentTime(),
                    app.getStatus()
                ))
                .collect(Collectors.toList());

            response.put("appointments", appointmentDTOs);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Failed to filter appointments by doctor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String name, long patientId) {
        Map<String, Object> response = new HashMap<>();
        try {
            int status;
            if ("past".equalsIgnoreCase(condition)) {
                status = 1;
            } else if ("future".equalsIgnoreCase(condition)) {
                status = 0;
            } else {
                response.put("error", "Invalid condition. Use 'past' or 'future'");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            List<Appointment> appointments = appointmentRepository.filterByDoctorNameAndPatientIdAndStatus(name, patientId, status);
            List<AppointmentDTO> appointmentDTOs = appointments.stream()
                .map(app -> new AppointmentDTO(
                    app.getId(),
                    app.getDoctor().getId(),
                    app.getDoctor().getName(),
                    app.getPatient().getId(),
                    app.getPatient().getName(),
                    app.getPatient().getEmail(),
                    app.getPatient().getPhone(),
                    app.getPatient().getAddress(),
                    app.getAppointmentTime(),
                    app.getStatus()
                ))
                .collect(Collectors.toList());

            response.put("appointments", appointmentDTOs);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Failed to filter appointments: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);

            if (patient == null) {
                response.put("error", "Patient not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response.put("patient", patient);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Failed to retrieve patient details: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
