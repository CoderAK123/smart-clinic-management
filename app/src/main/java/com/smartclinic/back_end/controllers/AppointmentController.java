package com.smartclinic.back_end.controllers;

import com.smartclinic.back_end.models.Appointment;
import com.smartclinic.back_end.services.AppointmentService;
import com.smartclinic.back_end.services.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST API controller.
//    - Use `@RequestMapping("/appointments")` to set a base path for all appointment-related endpoints.
//    - This centralizes all routes that deal with booking, updating, retrieving, and canceling appointments.


// 2. Autowire Dependencies:
//    - Inject `AppointmentService` for handling the business logic specific to appointments.
//    - Inject the general `Service` class, which provides shared functionality like token validation and appointment checks.


// 3. Define the `getAppointments` Method:
//    - Handles HTTP GET requests to fetch appointments based on date and patient name.
//    - Takes the appointment date, patient name, and token as path variables.
//    - First validates the token for role `"doctor"` using the `Service`.
//    - If the token is valid, returns appointments for the given patient on the specified date.
//    - If the token is invalid or expired, responds with the appropriate message and status code.


// 4. Define the `bookAppointment` Method:
//    - Handles HTTP POST requests to create a new appointment.
//    - Accepts a validated `Appointment` object in the request body and a token as a path variable.
//    - Validates the token for the `"patient"` role.
//    - Uses service logic to validate the appointment data (e.g., check for doctor availability and time conflicts).
//    - Returns success if booked, or appropriate error messages if the doctor ID is invalid or the slot is already taken.


// 5. Define the `updateAppointment` Method:
//    - Handles HTTP PUT requests to modify an existing appointment.
//    - Accepts a validated `Appointment` object and a token as input.
//    - Validates the token for `"patient"` role.
//    - Delegates the update logic to the `AppointmentService`.
//    - Returns an appropriate success or failure response based on the update result.


// 6. Define the `cancelAppointment` Method:
//    - Handles HTTP DELETE requests to cancel a specific appointment.
//    - Accepts the appointment ID and a token as path variables.
//    - Validates the token for `"patient"` role to ensure the user is authorized to cancel the appointment.
//    - Calls `AppointmentService` to handle the cancellation process and returns the result.

 private final AppointmentService appointmentService;
    private final Services service;

    @Autowired
    public AppointmentController(AppointmentService appointmentService, Services service) {
        this.appointmentService = appointmentService;
        this.service = service;
    }

    // GET /appointments/{date}/{name}/{token}
    @GetMapping("/{date}/{name}/{token}")
    public ResponseEntity<Map<String, Object>> getAppointments(@PathVariable String date,
                                                               @PathVariable String name,
                                                               @PathVariable String token) {
        Object validationObj = service.validateToken(token, "doctor");
        Map<String, Object> validation;
        if (validationObj instanceof ResponseEntity) {
            // If validateToken returns a ResponseEntity, extract the body
            validation = ((ResponseEntity<Map<String, Object>>) validationObj).getBody();
        } else {
            validation = (Map<String, Object>) validationObj;
        }
        if (!(boolean) validation.get("valid")) {
            return ResponseEntity.status(401).body(validation);
        }

        // Example: parse doctorId from name or another source, and parse date to LocalDateTime
        Long doctorId = null; // You need to determine how to get doctorId (e.g., from name or another parameter)
        String patientName = name;
        java.time.LocalDateTime startDateTime = java.time.LocalDate.parse(date).atStartOfDay();
        java.time.LocalDateTime endDateTime = startDateTime.plusDays(1).minusSeconds(1);

        java.util.List<Appointment> appointments = appointmentService.getAppointments(doctorId, patientName, startDateTime, endDateTime);
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("appointments", appointments);
        return ResponseEntity.ok(response);
    }

    // POST /appointments/book/{token}
    @PostMapping("/book/{token}")
    public ResponseEntity<Map<String, Object>> bookAppointment(@RequestBody Appointment appointment,
                                                               @PathVariable String token) {
        Object validationObj = service.validateToken(token, "patient");
        Map<String, Object> validation;
        if (validationObj instanceof ResponseEntity) {
            validation = ((ResponseEntity<Map<String, Object>>) validationObj).getBody();
        } else {
            validation = (Map<String, Object>) validationObj;
        }
        if (!(boolean) validation.get("valid")) {
            return ResponseEntity.status(401).body(validation);
        }

        Integer result = appointmentService.bookAppointment(appointment);
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("result", result);
        return ResponseEntity.ok(response);
    }

    // PUT /appointments/update/{token}
    @PutMapping("/update/{token}")
    public ResponseEntity<Map<String, Object>> updateAppointment(@RequestBody Appointment appointment,
                                                                 @PathVariable String token) {
        Object validationObj = service.validateToken(token, "patient");
        Map<String, Object> validation;
        if (validationObj instanceof ResponseEntity) {
            validation = ((ResponseEntity<Map<String, Object>>) validationObj).getBody();
        } else {
            validation = (Map<String, Object>) validationObj;
        }
        if (!(boolean) validation.get("valid")) {
            return ResponseEntity.status(401).body(validation);
        }

        String updateResult = appointmentService.updateAppointment(appointment);
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("message", updateResult);
        return ResponseEntity.ok(response);
    }

    // DELETE /appointments/cancel/{appointmentId}/{token}
    @DeleteMapping("/cancel/{appointmentId}/{token}")
    public ResponseEntity<Map<String, Object>> cancelAppointment(@PathVariable Long appointmentId,
                                                                 @PathVariable String token) {
        Object validationObj = service.validateToken(token, "patient");
        Map<String, Object> validation;
        if (validationObj instanceof ResponseEntity) {
            validation = ((ResponseEntity<Map<String, Object>>) validationObj).getBody();
        } else {
            validation = (Map<String, Object>) validationObj;
        }
        if (!(boolean) validation.get("valid")) {
            return ResponseEntity.status(401).body(validation);
        }

        // Assuming the patientId is available in the validation map after token validation
        Long patientId = validation.get("userId") != null ? Long.valueOf(validation.get("userId").toString()) : null;
        String cancelResult = appointmentService.cancelAppointment(appointmentId, patientId);
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("message", cancelResult);
        return ResponseEntity.ok(response);
    }

}
