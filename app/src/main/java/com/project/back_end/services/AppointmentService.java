package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService tokenService;
    private final com.project.back_end.services.Service service;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,
                            PatientRepository patientRepository,
                            DoctorRepository doctorRepository,
                            TokenService tokenService,
                            com.project.back_end.services.Service service) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
        this.service = service;
    }

    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Map<String, String> response = new HashMap<>();
        try {
            Optional<Appointment> existingAppointment = appointmentRepository.findById(appointment.getId());
            
            if (existingAppointment.isEmpty()) {
                response.put("error", "Appointment not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Appointment existing = existingAppointment.get();
            
            if (!existing.getPatient().getId().equals(appointment.getPatient().getId())) {
                response.put("error", "Patient ID mismatch");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            int validationResult = service.validateAppointment(
                appointment.getDoctor().getId(),
                appointment.getAppointmentTime()
            );

            if (validationResult != 1) {
                response.put("error", "Appointment time not available");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            appointmentRepository.save(appointment);
            response.put("message", "Appointment updated successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Failed to update appointment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        Map<String, String> response = new HashMap<>();
        try {
            String patientEmail = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(patientEmail);

            if (patient == null) {
                response.put("error", "Patient not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
            
            if (appointmentOpt.isEmpty()) {
                response.put("error", "Appointment not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Appointment appointment = appointmentOpt.get();

            if (!appointment.getPatient().getId().equals(patient.getId())) {
                response.put("error", "Unauthorized to cancel this appointment");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            appointmentRepository.delete(appointment);
            response.put("message", "Appointment cancelled successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Failed to cancel appointment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional
    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String doctorEmail = tokenService.extractEmail(token);
            Doctor doctor = doctorRepository.findByEmail(doctorEmail);

            if (doctor == null) {
                response.put("error", "Doctor not found");
                return response;
            }

            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

            List<Appointment> appointments;

            if (pname != null && !pname.isEmpty()) {
                appointments = appointmentRepository.findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                    doctor.getId(), pname, startOfDay, endOfDay
                );
            } else {
                appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                    doctor.getId(), startOfDay, endOfDay
                );
            }

            response.put("appointments", appointments);
            return response;

        } catch (Exception e) {
            response.put("error", "Failed to retrieve appointments: " + e.getMessage());
            return response;
        }
    }
}
