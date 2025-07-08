package com.smartclinic.back_end.services;

import com.smartclinic.back_end.models.*;
import com.smartclinic.back_end.repo.*;
import com.smartclinic.back_end.DTO.Login;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class Services {
// 1. **@Service Annotation**
// The @Service annotation marks this class as a service component in Spring. This allows Spring to automatically detect it through component scanning
// and manage its lifecycle, enabling it to be injected into controllers or other services using @Autowired or constructor injection.

// 2. **Constructor Injection for Dependencies**
// The constructor injects all required dependencies (TokenService, Repositories, and other Services). This approach promotes loose coupling, improves testability,
// and ensures that all required dependencies are provided at object creation time.

// 3. **validateToken Method**
// This method checks if the provided JWT token is valid for a specific user. It uses the TokenService to perform the validation.
// If the token is invalid or expired, it returns a 401 Unauthorized response with an appropriate error message. This ensures security by preventing
// unauthorized access to protected resources.

// 4. **validateAdmin Method**
// This method validates the login credentials for an admin user.
// - It first searches the admin repository using the provided username.
// - If an admin is found, it checks if the password matches.
// - If the password is correct, it generates and returns a JWT token (using the admin’s username) with a 200 OK status.
// - If the password is incorrect, it returns a 401 Unauthorized status with an error message.
// - If no admin is found, it also returns a 401 Unauthorized.
// - If any unexpected error occurs during the process, a 500 Internal Server Error response is returned.
// This method ensures that only valid admin users can access secured parts of the system.

// 5. **filterDoctor Method**
// This method provides filtering functionality for doctors based on name, specialty, and available time slots.
// - It supports various combinations of the three filters.
// - If none of the filters are provided, it returns all available doctors.
// This flexible filtering mechanism allows the frontend or consumers of the API to search and narrow down doctors based on user criteria.

// 6. **validateAppointment Method**
// This method validates if the requested appointment time for a doctor is available.
// - It first checks if the doctor exists in the repository.
// - Then, it retrieves the list of available time slots for the doctor on the specified date.
// - It compares the requested appointment time with the start times of these slots.
// - If a match is found, it returns 1 (valid appointment time).
// - If no matching time slot is found, it returns 0 (invalid).
// - If the doctor doesn’t exist, it returns -1.
// This logic prevents overlapping or invalid appointment bookings.

// 7. **validatePatient Method**
// This method checks whether a patient with the same email or phone number already exists in the system.
// - If a match is found, it returns false (indicating the patient is not valid for new registration).
// - If no match is found, it returns true.
// This helps enforce uniqueness constraints on patient records and prevent duplicate entries.

// 8. **validatePatientLogin Method**
// This method handles login validation for patient users.
// - It looks up the patient by email.
// - If found, it checks whether the provided password matches the stored one.
// - On successful validation, it generates a JWT token and returns it with a 200 OK status.
// - If the password is incorrect or the patient doesn't exist, it returns a 401 Unauthorized with a relevant error.
// - If an exception occurs, it returns a 500 Internal Server Error.
// This method ensures only legitimate patients can log in and access their data securely.

// 9. **filterPatient Method**
// This method filters a patient's appointment history based on condition and doctor name.
// - It extracts the email from the JWT token to identify the patient.
// - Depending on which filters (condition, doctor name) are provided, it delegates the filtering logic to PatientService.
// - If no filters are provided, it retrieves all appointments for the patient.
// This flexible method supports patient-specific querying and enhances user experience on the client side.

 private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    @Autowired
    public Services(
            TokenService tokenService,
            AdminRepository adminRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            DoctorService doctorService,
            PatientService patientService
    ) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    public ResponseEntity<Map<String, Object>> validateToken(String token, String role) {
        Map<String, Object> res = new HashMap<>();
        try {
            //String email = tokenService.extractUsername(token);
            boolean isValid = tokenService.validateToken(token,role);
            if (!isValid) {
                res.put("message", "Unauthorized: Invalid or expired token");
                return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
            }
            res.put("message", "Valid token");
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            res.put("message", "Token validation error: " + e.getMessage());
            return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
        }
    }

    public ResponseEntity<Map<String, Object>> validateAdmin(Login login) {
        Map<String, Object> res = new HashMap<>();
        try {
            Admin admin = adminRepository.findByUsername(login.getEmail());
            if (admin != null && admin.getPassword().equals(login.getPassword())) {
                String token = tokenService.generateToken(admin.getUsername(), "admin");
                res.put("token", token);
                return new ResponseEntity<>(res, HttpStatus.OK);
            }
            res.put("message", "Invalid username or password");
            return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            res.put("message", "Login error: " + e.getMessage());
            return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<Doctor> filterDoctor(String name, String specialty, String time) {
        if (name != null && specialty != null && time != null) {
            return doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);
        } else if (name != null && specialty != null) {
            return doctorService.filterDoctorByNameAndSpecility(name, specialty);
        } else if (name != null && time != null) {
            return doctorService.filterDoctorByNameAndTime(name, time);
        } else if (specialty != null && time != null) {
            return doctorService.filterDoctorByTimeAndSpecility(specialty, time);
        } else if (name != null) {
            return doctorService.findDoctorByName(name);
        } else if (specialty != null) {
            return doctorService.filterDoctorBySpecility(specialty);
        } else if (time != null) {
            return doctorService.filterDoctorsByTime(time);
        } else {
            return doctorService.getDoctors();
        }
    }

    public int validateAppointment(Long doctorId, LocalDateTime time) {
        Optional<Doctor> docOpt = doctorRepository.findById(doctorId);
        if (docOpt.isEmpty()) return -1;

        Doctor doctor = docOpt.get();
       for (String slotStr : doctor.getAvailableTimes()) {
        LocalDateTime slot = LocalDateTime.parse(slotStr); // adjust formatter if needed
          if (slot.equals(time)) return 1;
         }
        return 0;
    }

    public boolean validatePatient(Patient patient) {
        return patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone()) == null;
    }

    public ResponseEntity<Map<String, Object>> validatePatientLogin(Login login) {
        Map<String, Object> res = new HashMap<>();
        try {
            Patient patient = patientRepository.findByEmail(login.getEmail());
            if (patient != null && patient.getPassword().equals(login.getPassword())) {
                String token = tokenService.generateToken(patient.getEmail(), "patient");
                res.put("token", token);
                return new ResponseEntity<>(res, HttpStatus.OK);
            }
            res.put("message", "Invalid email or password");
            return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            res.put("message", "Login error: " + e.getMessage());
            return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> filterPatient(String token, String condition, String doctorName) {
        try {
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);
            if (patient == null) {
                return new ResponseEntity<>("Patient not found", HttpStatus.NOT_FOUND);
            }

            Long patientId = patient.getId();

            if (condition != null && doctorName != null) {
                return patientService.filterByDoctorAndCondition(doctorName, condition,patientId);
            } else if (condition != null) {
                return patientService.filterByCondition(condition, patientId);
            } else if (doctorName != null) {
                return patientService.filterByDoctor(doctorName, patientId);
            } else {
                return patientService.getPatientAppointment(patientId);
            }

        } catch (Exception e) {
            return new ResponseEntity<>("Error filtering patient appointments: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public TokenService getTokenService() {
        return tokenService;
    }
    public AdminRepository getAdminRepository() {
        return adminRepository;
    }
    public DoctorRepository getDoctorRepository() {
        return doctorRepository;
    }
    public PatientRepository getPatientRepository() {
        return patientRepository;
    }
    public DoctorService getDoctorService() {
        return doctorService;
    }
    public PatientService getPatientService() {
        return patientService;
    }
    /*public void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService;
    }
    public void setAdminRepository(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }
    public void setDoctorRepository(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }
    public void setPatientRepository(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }
    public void setDoctorService(DoctorService doctorService) {
        this.doctorService = doctorService;
    }
    public void setPatientService(PatientService patientService) {
        this.patientService = patientService;
    }*/
    @Override
    public String toString() {
        return "Services{" +
                "tokenService=" + tokenService +
                ", adminRepository=" + adminRepository +
                ", doctorRepository=" + doctorRepository +
                ", patientRepository=" + patientRepository +
                ", doctorService=" + doctorService +
                ", patientService=" + patientService +
                '}';
    }
    public ResponseEntity<Map<String, Object>> validateDoctorLogin(Login login) {
    Map<String, Object> res = new HashMap<>();
    try {
        Doctor doctor = doctorRepository.findByEmail(login.getEmail());
        if (doctor != null && doctor.getPassword().equals(login.getPassword())) {
            String token = tokenService.generateToken(doctor.getEmail(), "doctor");
            res.put("token", token);
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        res.put("message", "Invalid email or password");
        return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
        res.put("message", "Login error: " + e.getMessage());
        return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


}
