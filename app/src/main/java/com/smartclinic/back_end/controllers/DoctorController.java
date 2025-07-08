package com.smartclinic.back_end.controllers;

import com.smartclinic.back_end.models.Doctor;
import com.smartclinic.back_end.DTO.Login;
import com.smartclinic.back_end.services.DoctorService;
import com.smartclinic.back_end.services.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("${api.path}doctor")

public class DoctorController {

// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST controller that serves JSON responses.
//    - Use `@RequestMapping("${api.path}doctor")` to prefix all endpoints with a configurable API path followed by "doctor".
//    - This class manages doctor-related functionalities such as registration, login, updates, and availability.


// 2. Autowire Dependencies:
//    - Inject `DoctorService` for handling the core logic related to doctors (e.g., CRUD operations, authentication).
//    - Inject the shared `Service` class for general-purpose features like token validation and filtering.


// 3. Define the `getDoctorAvailability` Method:
//    - Handles HTTP GET requests to check a specific doctor’s availability on a given date.
//    - Requires `user` type, `doctorId`, `date`, and `token` as path variables.
//    - First validates the token against the user type.
//    - If the token is invalid, returns an error response; otherwise, returns the availability status for the doctor.


// 4. Define the `getDoctor` Method:
//    - Handles HTTP GET requests to retrieve a list of all doctors.
//    - Returns the list within a response map under the key `"doctors"` with HTTP 200 OK status.


// 5. Define the `saveDoctor` Method:
//    - Handles HTTP POST requests to register a new doctor.
//    - Accepts a validated `Doctor` object in the request body and a token for authorization.
//    - Validates the token for the `"admin"` role before proceeding.
//    - If the doctor already exists, returns a conflict response; otherwise, adds the doctor and returns a success message.


// 6. Define the `doctorLogin` Method:
//    - Handles HTTP POST requests for doctor login.
//    - Accepts a validated `Login` DTO containing credentials.
//    - Delegates authentication to the `DoctorService` and returns login status and token information.


// 7. Define the `updateDoctor` Method:
//    - Handles HTTP PUT requests to update an existing doctor's information.
//    - Accepts a validated `Doctor` object and a token for authorization.
//    - Token must belong to an `"admin"`.
//    - If the doctor exists, updates the record and returns success; otherwise, returns not found or error messages.


// 8. Define the `deleteDoctor` Method:
//    - Handles HTTP DELETE requests to remove a doctor by ID.
//    - Requires both doctor ID and an admin token as path variables.
//    - If the doctor exists, deletes the record and returns a success message; otherwise, responds with a not found or error message.


// 9. Define the `filter` Method:
//    - Handles HTTP GET requests to filter doctors based on name, time, and specialty.
//    - Accepts `name`, `time`, and `speciality` as path variables.
//    - Calls the shared `Service` to perform filtering logic and returns matching doctors in the response.

 private final DoctorService doctorService;
    private final Services service;

    @Autowired
    public DoctorController(DoctorService doctorService, Services service) {
        this.doctorService = doctorService;
        this.service = service;
    }

    // 3. Check Doctor Availability
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<Map<String, Object>> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable String date,
            @PathVariable String token) {

        ResponseEntity<Map<String, Object>> validationResponse = service.validateToken(token, user);
        Map<String, Object> validation = validationResponse.getBody();
        if (validation == null || !(boolean) validation.get("valid")) {
            return ResponseEntity.status(401).body(validation);
        }

        // Call the instance method and wrap the result in a map if necessary
        List<java.time.LocalTime> availabilityList = doctorService.getDoctorAvailability(doctorId, date);
        Map<String, Object> availabilityResult = new java.util.HashMap<>();
        availabilityResult.put("availability", availabilityList);
        return ResponseEntity.ok(availabilityResult);
    }

    // 4. Get All Doctors
    @GetMapping("/get")
    public ResponseEntity<Map<String, Object>> getDoctor() {
        List<Doctor> doctors = doctorService.getDoctors();
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("doctors", doctors);
        return ResponseEntity.ok(response);
    }

    // 5. Register New Doctor
    @PostMapping("/save/{token}")
    public ResponseEntity<Map<String, Object>> saveDoctor(@RequestBody Doctor doctor,
                                                          @PathVariable String token) {

        ResponseEntity<Map<String, Object>> validationResponse = service.validateToken(token, "admin");
        Map<String, Object> validation = validationResponse.getBody();
        if (validation == null || !(boolean) validation.get("valid")) {
            return ResponseEntity.status(401).body(validation);
        }

        int result = doctorService.saveDoctor(doctor);
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("result", result);
        return ResponseEntity.ok(response);
    }

    // 6. Doctor Login
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> doctorLogin(@RequestBody Login login) {
        return ResponseEntity.ok(doctorService.validateDoctor(login.getEmail(), login.getPassword()));
    }

    // 7. Update Doctor
    @PutMapping("/update/{token}")
    public ResponseEntity<Map<String, Object>> updateDoctor(@RequestBody Doctor doctor,
                                                            @PathVariable String token) {

        ResponseEntity<Map<String, Object>> validationResponse = service.validateToken(token, "admin");
        Map<String, Object> validation = validationResponse.getBody();
        if (validation == null || !(boolean) validation.get("valid")) {
            return ResponseEntity.status(401).body(validation);
        }

        int result = doctorService.updateDoctor(doctor);
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("result", result);
        return ResponseEntity.ok(response);
    }

    // 8. Delete Doctor
    @DeleteMapping("/delete/{id}/{token}")
    public ResponseEntity<Map<String, Object>> deleteDoctor(@PathVariable Long id,
                                                            @PathVariable String token) {

        ResponseEntity<Map<String, Object>> validationResponse = service.validateToken(token, "admin");
        Map<String, Object> validation = validationResponse.getBody();
        if (validation == null || !(boolean) validation.get("valid")) {
            return ResponseEntity.status(401).body(validation);
        }

        int result = doctorService.deleteDoctor(id);
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("result", result);
        return ResponseEntity.ok(response);
    }

    // 9. Filter Doctors
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, Object>> filter(@PathVariable String name,
                                                      @PathVariable String time,
                                                      @PathVariable String speciality) {

        List<Doctor> filteredDoctors = service.filterDoctor(name, time, speciality);
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("doctors", filteredDoctors);
        return ResponseEntity.ok(response);
    }

}
