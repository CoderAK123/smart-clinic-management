package com.smartclinic.back_end.controllers;

import com.smartclinic.back_end.models.Prescription;
import com.smartclinic.back_end.services.AppointmentService;
import com.smartclinic.back_end.services.PrescriptionService;
import com.smartclinic.back_end.services.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.smartclinic.back_end.models.Appointment;
import java.util.Map;

@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {
    
// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST API controller.
//    - Use `@RequestMapping("${api.path}prescription")` to set the base path for all prescription-related endpoints.
//    - This controller manages creating and retrieving prescriptions tied to appointments.


// 2. Autowire Dependencies:
//    - Inject `PrescriptionService` to handle logic related to saving and fetching prescriptions.
//    - Inject the shared `Service` class for token validation and role-based access control.
//    - Inject `AppointmentService` to update appointment status after a prescription is issued.


// 3. Define the `savePrescription` Method:
//    - Handles HTTP POST requests to save a new prescription for a given appointment.
//    - Accepts a validated `Prescription` object in the request body and a doctor’s token as a path variable.
//    - Validates the token for the `"doctor"` role.
//    - If the token is valid, updates the status of the corresponding appointment to reflect that a prescription has been added.
//    - Delegates the saving logic to `PrescriptionService` and returns a response indicating success or failure.


// 4. Define the `getPrescription` Method:
//    - Handles HTTP GET requests to retrieve a prescription by its associated appointment ID.
//    - Accepts the appointment ID and a doctor’s token as path variables.
//    - Validates the token for the `"doctor"` role using the shared service.
//    - If the token is valid, fetches the prescription using the `PrescriptionService`.
//    - Returns the prescription details or an appropriate error message if validation fails.


    private final PrescriptionService prescriptionService;
    private final AppointmentService appointmentService;
    private final Services service;

    @Autowired
    public PrescriptionController(PrescriptionService prescriptionService,
                                  AppointmentService appointmentService,
                                  Services service) {
        this.prescriptionService = prescriptionService;
        this.appointmentService = appointmentService;
        this.service = service;
    }

    // 3. Save a new Prescription (POST)
    @PostMapping("/save/{token}")
    public ResponseEntity<Map<String, Object>> savePrescription(
            @RequestBody Prescription prescription,
            @PathVariable String token) {

        ResponseEntity<Map<String, Object>> validationResponse = service.validateToken(token, "doctor");
        Map<String, Object> validation = validationResponse.getBody();
        if (validation == null || !(boolean) validation.get("valid")) {
            return ResponseEntity.status(401).body(validation);
        }

        // Update appointment status
        // Provide the required arguments for getAppointments or implement a method to get by ID
        // Fetch the list and get the first appointment (assuming appointmentId is unique)
        java.util.List<Appointment> appointments = appointmentService.getAppointments(
            prescription.getAppointmentId(), null, null, null
        );
        Appointment appointment = (appointments != null && !appointments.isEmpty()) ? appointments.get(0) : null;
        if (appointment != null) {
            appointmentService.updateAppointment(appointment);
        }

        // Save prescription
        ResponseEntity<Map<String, Object>> saveResponse = prescriptionService.savePrescription(prescription);
        return saveResponse;
    }

    // 4. Get Prescription by Appointment ID (GET)
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<Map<String, Object>> getPrescription(
            @PathVariable Long appointmentId,
            @PathVariable String token) {

        ResponseEntity<Map<String, Object>> validationResponse = service.validateToken(token, "doctor");
        Map<String, Object> validation = validationResponse.getBody();
        if (validation == null || !(boolean) validation.get("valid")) {
            return ResponseEntity.status(401).body(validation);
        }

        return prescriptionService.getPrescription(appointmentId);
    }

}
