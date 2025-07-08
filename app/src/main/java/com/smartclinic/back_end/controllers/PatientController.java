package com.smartclinic.back_end.controllers;

import com.smartclinic.back_end.DTO.Login;
import com.smartclinic.back_end.models.Patient;
import com.smartclinic.back_end.services.PatientService;
import com.smartclinic.back_end.repo.PatientRepository;
import com.smartclinic.back_end.services.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/patient")

public class PatientController {

// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST API controller for patient-related operations.
//    - Use `@RequestMapping("/patient")` to prefix all endpoints with `/patient`, grouping all patient functionalities under a common route.


// 2. Autowire Dependencies:
//    - Inject `PatientService` to handle patient-specific logic such as creation, retrieval, and appointments.
//    - Inject the shared `Service` class for tasks like token validation and login authentication.


// 3. Define the `getPatient` Method:
//    - Handles HTTP GET requests to retrieve patient details using a token.
//    - Validates the token for the `"patient"` role using the shared service.
//    - If the token is valid, returns patient information; otherwise, returns an appropriate error message.


// 4. Define the `createPatient` Method:
//    - Handles HTTP POST requests for patient registration.
//    - Accepts a validated `Patient` object in the request body.
//    - First checks if the patient already exists using the shared service.
//    - If validation passes, attempts to create the patient and returns success or error messages based on the outcome.


// 5. Define the `login` Method:
//    - Handles HTTP POST requests for patient login.
//    - Accepts a `Login` DTO containing email/username and password.
//    - Delegates authentication to the `validatePatientLogin` method in the shared service.
//    - Returns a response with a token or an error message depending on login success.


// 6. Define the `getPatientAppointment` Method:
//    - Handles HTTP GET requests to fetch appointment details for a specific patient.
//    - Requires the patient ID, token, and user role as path variables.
//    - Validates the token using the shared service.
//    - If valid, retrieves the patient's appointment data from `PatientService`; otherwise, returns a validation error.


// 7. Define the `filterPatientAppointment` Method:
//    - Handles HTTP GET requests to filter a patient's appointments based on specific conditions.
//    - Accepts filtering parameters: `condition`, `name`, and a token.
//    - Token must be valid for a `"patient"` role.
//    - If valid, delegates filtering logic to the shared service and returns the filtered result.


private final PatientService patientService;
    private final Services service;
    private final PatientRepository patientRepository;

    @Autowired
    public PatientController(PatientService patientService, Services service, PatientRepository patientRepository) {
        this.patientService = patientService;
        this.service = service;
        this.patientRepository = patientRepository;
    }

    // 3. Get Patient Profile by Token
    @GetMapping("/get/{token}")
    public ResponseEntity<Map<String, Object>> getPatient(@PathVariable String token) {
        Map<String, Object> validation = service.validateToken(token, "patient").getBody();
        if (!(boolean) validation.get("valid")) {
            return ResponseEntity.status(401).body(validation);
        }
        Object patientDetailsBody = patientService.getPatientDetails(token).getBody();
        if (patientDetailsBody instanceof Map) {
            return ResponseEntity.ok((Map<String, Object>) patientDetailsBody);
        } else {
            return ResponseEntity.status(500).body(Map.of("valid", false, "message", "Unexpected response type"));
        }
    }

    // 4. Register New Patient
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createPatient(@RequestBody Patient patient) {
        boolean isValid = service.validatePatient(patient);
        if (!isValid) {
            return ResponseEntity.status(409).body(Map.of("valid", false, "message", "Patient already exists or validation failed"));
        }
        int result = patientService.createPatient(patient);
        if (result == 1) {
            return ResponseEntity.ok(Map.of("valid", true, "message", "Patient created successfully"));
        } else {
            return ResponseEntity.status(500).body(Map.of("valid", false, "message", "Failed to create patient"));
        }
    }

    // 5. Patient Login
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Login login) {
        return service.validatePatientLogin(login);
    }

    // 6. Get Appointments by Patient ID
    @GetMapping("/appointments/{id}/{token}/{user}")
    public ResponseEntity<Map<String, Object>> getPatientAppointment(
            @PathVariable Long id,
            @PathVariable String token,
            @PathVariable String user) {

        Map<String, Object> validation = service.validateToken(token, user).getBody();
        if (!(boolean) validation.get("valid")) {
            return ResponseEntity.status(401).body(validation);
        }

        ResponseEntity<?> response = patientService.getPatientAppointment(id);
        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok(Map.of("valid", true, "appointments", response.getBody()));
        } else {
            return ResponseEntity.status(response.getStatusCode()).body(Map.of("valid", false, "message", response.getBody()));
        }
    }

    // 7. Filter Appointments
    @GetMapping("/filterAppointments/{condition}/{name}/{token}")
    public ResponseEntity<Map<String, Object>> filterPatientAppointment(
            @PathVariable String condition,
            @PathVariable String name,
            @PathVariable String token) {

        Map<String, Object> validation = service.validateToken(token, "patient").getBody();
        if (!(boolean) validation.get("valid")) {
            return ResponseEntity.status(401).body(validation);
        }

        // Extract patient ID from token
        // Use TokenService directly instead of service.getTokenService()
        String email = service.getTokenService() != null
            ? service.getTokenService().extractEmail(token)
            : null;
        Patient patient = patientRepository.findByEmail(email);
        if (patient == null) {
            return ResponseEntity.status(404).body(Map.of("valid", false, "message", "Patient not found"));
        }

        // Filtering logic
        ResponseEntity<?> response;
        if (!condition.equalsIgnoreCase("all") && !name.equalsIgnoreCase("all")) {
            response = patientService.filterByDoctorAndCondition(name, condition, patient.getId());
        } else if (!condition.equalsIgnoreCase("all")) {
            response = patientService.filterByCondition(condition, patient.getId());
        } else if (!name.equalsIgnoreCase("all")) {
            response = patientService.filterByDoctor(name, patient.getId());
        } else {
            response = patientService.getPatientAppointment(patient.getId());
        }

        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok(Map.of("valid", true, "appointments", response.getBody()));
        } else {
            return ResponseEntity.status(response.getStatusCode()).body(Map.of("valid", false, "message", response.getBody()));
        }
    }
}


