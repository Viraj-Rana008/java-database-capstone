package com.project.back_end.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Document(collection="prescriptions")
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min=3, max=100)
    @NotNull(message = "Patient name cannot be null")
    private String patientName;

    @NotNull(message = "Appointment ID cannot be null")
    private Long appointmentId;

    @Size(min=3, max=100)
    private String medication;

    @Size(min=3,max=20)
    private String dosage;

    @Size(max=200)
    private String instructions;

    Prescription(String patientName, Long appointmentId) {
        this.patientName = patientName;
        this.appointmentId = appointmentId;
    }

    Prescription(String patientName, Long appointmentId, String medication, String dosage, String instructions) {
        this.patientName = patientName;
        this.appointmentId = appointmentId;
        this.medication = medication;
        this.dosage = dosage;
        this.instructions = instructions;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getPatientName() {
        return patientName;
    }
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
    public Long getAppointmentId() {
        return appointmentId;
    }
    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }
    public String getMedication() {
        return medication;
    }
    public void setMedication(String medication) {
        this.medication = medication;
    }
    public String getDosage() {
        return dosage;
    }
    public void setDosage(String dosage) {
        this.dosage = dosage;
    }
    public String getInstructions() {
        return instructions;
    }
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
