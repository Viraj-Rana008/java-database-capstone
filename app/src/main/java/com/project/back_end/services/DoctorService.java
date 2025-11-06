package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.AvailableTime;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository,
                        AppointmentRepository appointmentRepository,
                        TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    @Transactional
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        try {
            Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
            if (doctorOpt.isEmpty()) {
                return new ArrayList<>();
            }

            Doctor doctor = doctorOpt.get();
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

            List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                doctorId, startOfDay, endOfDay
            );

            Set<LocalTime> bookedTimes = appointments.stream()
                .map(app -> app.getAppointmentTime().toLocalTime())
                .collect(Collectors.toSet());

            List<String> availableSlots = new ArrayList<>();
            for (AvailableTime availableTime : doctor.getAvailableTimes()) {
                if (!bookedTimes.contains(availableTime.getStartTime())) {
                    availableSlots.add(availableTime.getStartTime().toString());
                }
            }

            return availableSlots;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Transactional
    public int saveDoctor(Doctor doctor) {
        try {
            Doctor existing = doctorRepository.findByEmail(doctor.getEmail());
            if (existing != null) {
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public int updateDoctor(Doctor doctor) {
        try {
            Optional<Doctor> existing = doctorRepository.findById(doctor.getId());
            if (existing.isEmpty()) {
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public List<Doctor> getDoctors() {
        try {
            return doctorRepository.findAll();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Transactional
    public int deleteDoctor(long id) {
        try {
            Optional<Doctor> existing = doctorRepository.findById(id);
            if (existing.isEmpty()) {
                return -1;
            }
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Map<String, String> response = new HashMap<>();
        try {
            Doctor doctor = doctorRepository.findByEmail(login.getIdentifier());
            
            if (doctor == null) {
                response.put("error", "Doctor not found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            if (!doctor.getPassword().equals(login.getPassword())) {
                response.put("error", "Invalid password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = tokenService.generateToken(doctor.getEmail());
            response.put("token", token);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional
    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Doctor> doctors = doctorRepository.findByNameLike(name);
            response.put("doctors", doctors);
        } catch (Exception e) {
            response.put("error", "Failed to find doctors");
        }
        return response;
    }

    @Transactional
    public Map<String, Object> filterDoctorsByNameSpecilityandTime(String name, String specialty, String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
            List<Doctor> filteredDoctors = filterDoctorByTime(doctors, amOrPm);
            response.put("doctors", filteredDoctors);
        } catch (Exception e) {
            response.put("error", "Failed to filter doctors");
        }
        return response;
    }

    @Transactional
    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Doctor> doctors = doctorRepository.findByNameLike(name);
            List<Doctor> filteredDoctors = filterDoctorByTime(doctors, amOrPm);
            response.put("doctors", filteredDoctors);
        } catch (Exception e) {
            response.put("error", "Failed to filter doctors");
        }
        return response;
    }

    @Transactional
    public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specilty) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specilty);
            response.put("doctors", doctors);
        } catch (Exception e) {
            response.put("error", "Failed to filter doctors");
        }
        return response;
    }

    @Transactional
    public Map<String, Object> filterDoctorByTimeAndSpecility(String specilty, String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specilty);
            List<Doctor> filteredDoctors = filterDoctorByTime(doctors, amOrPm);
            response.put("doctors", filteredDoctors);
        } catch (Exception e) {
            response.put("error", "Failed to filter doctors");
        }
        return response;
    }

    @Transactional
    public Map<String, Object> filterDoctorBySpecility(String specilty) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specilty);
            response.put("doctors", doctors);
        } catch (Exception e) {
            response.put("error", "Failed to filter doctors");
        }
        return response;
    }

    @Transactional
    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Doctor> doctors = doctorRepository.findAll();
            List<Doctor> filteredDoctors = filterDoctorByTime(doctors, amOrPm);
            response.put("doctors", filteredDoctors);
        } catch (Exception e) {
            response.put("error", "Failed to filter doctors");
        }
        return response;
    }

    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        return doctors.stream()
            .filter(doctor -> {
                if (doctor.getAvailableTimes() == null || doctor.getAvailableTimes().isEmpty()) {
                    return false;
                }
                
                for (AvailableTime availableTime : doctor.getAvailableTimes()) {
                    LocalTime time = availableTime.getStartTime();
                    int hour = time.getHour();
                    
                    if ("AM".equalsIgnoreCase(amOrPm) && hour >= 0 && hour < 12) {
                        return true;
                    } else if ("PM".equalsIgnoreCase(amOrPm) && hour >= 12 && hour < 24) {
                        return true;
                    }
                }
                return false;
            })
            .collect(Collectors.toList());
    }
}
