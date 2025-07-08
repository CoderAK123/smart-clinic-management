package com.smartclinic.back_end.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import jakarta.validation.constraints.*;

@Document(collection = "prescriptions")
public class Prescription {

    @Id
    private String id;

    @NotBlank
    @Size(min = 3, max = 100)
    @Field("patient_name")
    private String patientName;

    @NotNull
    @Field("appointment_id")
    private Long appointmentId;

    @NotBlank
    @Size(min = 3, max = 100)
    private String medication;

    @Size(max = 200)
    @Field("doctor_notes")
    private String doctorNotes;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getDoctorNotes() {
        return doctorNotes;
    }

    public void setDoctorNotes(String doctorNotes) {
        this.doctorNotes = doctorNotes;
    }
}
