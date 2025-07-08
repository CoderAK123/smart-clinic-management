package com.smartclinic.back_end.services;

import com.smartclinic.back_end.models.Appointment;
import com.smartclinic.back_end.models.Doctor;
import com.smartclinic.back_end.models.Patient;
import com.smartclinic.back_end.repo.AppointmentRepository;
import com.smartclinic.back_end.repo.DoctorRepository;
import com.smartclinic.back_end.repo.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.smartclinic.back_end.services.TokenService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {
// 1. **Add @Service Annotation**:
//    - To indicate that this class is a service layer class for handling business logic.
//    - The `@Service` annotation should be added before the class declaration to mark it as a Spring service component.
//    - Instruction: Add `@Service` above the class definition.

// 2. **Constructor Injection for Dependencies**:
//    - The `AppointmentService` class requires several dependencies like `AppointmentRepository`, `Service`, `TokenService`, `PatientRepository`, and `DoctorRepository`.
//    - These dependencies should be injected through the constructor.
//    - Instruction: Ensure constructor injection is used for proper dependency management in Spring.

// 3. **Add @Transactional Annotation for Methods that Modify Database**:
//    - The methods that modify or update the database should be annotated with `@Transactional` to ensure atomicity and consistency of the operations.
//    - Instruction: Add the `@Transactional` annotation above methods that interact with the database, especially those modifying data.

// 4. **Book Appointment Method**:
//    - Responsible for saving the new appointment to the database.
//    - If the save operation fails, it returns `0`; otherwise, it returns `1`.
//    - Instruction: Ensure that the method handles any exceptions and returns an appropriate result code.

// 5. **Update Appointment Method**:
//    - This method is used to update an existing appointment based on its ID.
//    - It validates whether the patient ID matches, checks if the appointment is available for updating, and ensures that the doctor is available at the specified time.
//    - If the update is successful, it saves the appointment; otherwise, it returns an appropriate error message.
//    - Instruction: Ensure proper validation and error handling is included for appointment updates.

// 6. **Cancel Appointment Method**:
//    - This method cancels an appointment by deleting it from the database.
//    - It ensures the patient who owns the appointment is trying to cancel it and handles possible errors.
//    - Instruction: Make sure that the method checks for the patient ID match before deleting the appointment.

// 7. **Get Appointments Method**:
//    - This method retrieves a list of appointments for a specific doctor on a particular day, optionally filtered by the patient's name.
//    - It uses `@Transactional` to ensure that database operations are consistent and handled in a single transaction.
//    - Instruction: Ensure the correct use of transaction boundaries, especially when querying the database for appointments.

// 8. **Change Status Method**:
//    - This method updates the status of an appointment by changing its value in the database.
//    - It should be annotated with `@Transactional` to ensure the operation is executed in a single transaction.
//    - Instruction: Add `@Transactional` before this method to ensure atomicity when updating appointment status.

private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    //private final Service sharedService;
    private final TokenService tokenService;

    @Autowired
    public AppointmentService(
            AppointmentRepository appointmentRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            //Service sharedService,
            TokenService tokenService
    ) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        //this.sharedService = sharedService;
        this.tokenService = tokenService;
    }

    // 4. Book Appointment
    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // 5. Update Appointment
    @Transactional
    public String updateAppointment(Appointment updatedAppointment) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(updatedAppointment.getId());
        if (optionalAppointment.isEmpty()) {
            return "Appointment not found";
        }

        Appointment existing = optionalAppointment.get();

        if (!existing.getPatient().getId().equals(updatedAppointment.getPatient().getId())) {
            return "Unauthorized: Patient mismatch";
        }

        // Check doctor's availability
        List<Appointment> conflictingAppointments = appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetween(
                        updatedAppointment.getDoctor().getId(),
                        updatedAppointment.getAppointmentTime().minusMinutes(59),
                        updatedAppointment.getAppointmentTime().plusMinutes(59)
                );

        if (!conflictingAppointments.isEmpty() &&
            conflictingAppointments.stream().noneMatch(a -> a.getId().equals(updatedAppointment.getId()))) {
            return "Doctor not available at selected time";
        }

        appointmentRepository.save(updatedAppointment);
        return "Updated successfully";
    }

    // 6. Cancel Appointment
    @Transactional
    public String cancelAppointment(Long id, Long patientId) {
        Optional<Appointment> optional = appointmentRepository.findById(id);
        if (optional.isEmpty()) {
            return "Appointment not found";
        }

        Appointment appointment = optional.get();
        if (!appointment.getPatient().getId().equals(patientId)) {
            return "Unauthorized to cancel this appointment";
        }

        appointmentRepository.deleteById(id);
        return "Appointment cancelled successfully";
    }

    // 7. Get Appointments
    @Transactional(readOnly = true)
    public List<Appointment> getAppointments(Long doctorId, String patientName, LocalDateTime start, LocalDateTime end) {
        if (patientName.equalsIgnoreCase("none")) {
            return appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);
        }
        return appointmentRepository.findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                doctorId, patientName, start, end
        );
    }

    // 8. Change Status
    @Transactional
    public void changeStatus(int status, long appointmentId) {
        appointmentRepository.updateAppointmentStatusById(status, appointmentId);
    }
}
