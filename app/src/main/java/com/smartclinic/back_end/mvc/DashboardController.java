package com.smartclinic.back_end.mvc;

import com.smartclinic.back_end.DTO.Login;
import com.smartclinic.back_end.models.Admin;
import com.smartclinic.back_end.models.Doctor;
import com.smartclinic.back_end.models.Patient;
import com.smartclinic.back_end.repo.AdminRepository;
import com.smartclinic.back_end.repo.DoctorRepository;
import com.smartclinic.back_end.repo.PatientRepository;
import com.smartclinic.back_end.services.Services;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class DashboardController {

    @Autowired
    private Services service;

    @Autowired
    private AdminRepository adminRepo;

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private PatientRepository patientRepo;

    @GetMapping("/")
    public String root() {
        return "forward:/pages/defineRole.html";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String role, Model model, HttpSession session) {
        if (role != null && !role.isEmpty()) {
            session.setAttribute("loginRole", role);
        }

        String sessionRole = (String) session.getAttribute("loginRole");

        if (sessionRole == null || sessionRole.isEmpty()) {
            return "redirect:/defineRole.html";
        }

        model.addAttribute("role", sessionRole);
        model.addAttribute("login", new Login());
        return "login";
    }

    @PostMapping("/api/login")
public String processLogin(
        @ModelAttribute("login") Login login,
        @RequestParam(required = false) String role,
        Model model,
        HttpSession session) {

    if (role == null || role.isEmpty()) {
        role = (String) session.getAttribute("loginRole");
    }

    if (role == null || role.isEmpty()) {
        System.out.println("❌ No role provided");
        return "redirect:/defineRole.html";
    }

    System.out.println("➡️ Login attempt: role=" + role + ", email=" + login.getEmail());

    switch (role) {
        case "admin":
            Admin admin = adminRepo.findByUsername(login.getEmail().split("@")[0]);
            if (admin != null && admin.getPassword().equals(login.getPassword())) {
                String token = service.getTokenService().generateToken(admin.getUsername(), "admin");
                session.setAttribute("token", token);
                System.out.println("✅ Admin login successful. Redirecting to /adminDashboard/" + token);
                return "redirect:/adminDashboard/" + token;
            } else {
                System.out.println("❌ Invalid admin credentials.");
            }
            break;

        case "doctor":
            Doctor doctor = doctorRepo.findByEmail(login.getEmail());
            if (doctor != null && doctor.getPassword().equals(login.getPassword())) {
                String token = service.getTokenService().generateToken(doctor.getEmail(), "doctor");
                session.setAttribute("token", token);
                session.setAttribute("doctorId", doctor.getId());
                System.out.println("✅ Doctor login successful. Redirecting to /doctorDashboard/" + token);
                return "redirect:/doctorDashboard/" + token;
            } else {
                System.out.println("❌ Invalid doctor credentials.");
            }
            break;

        case "patient":
    Patient patient = patientRepo.findByEmail(login.getEmail().trim().toLowerCase());
    if (patient != null) {
        System.out.println("🧪 Patient found: email = " + patient.getEmail());
        System.out.println("🧪 DB password: [" + patient.getPassword() + "]");
        System.out.println("🧪 Input password: [" + login.getPassword() + "]");
        System.out.println("🧪 DB password (trimmed): [" + patient.getPassword().trim() + "]");
        
        if (patient.getPassword() != null &&
            patient.getPassword().trim().equals(login.getPassword())) {

            String token = service.getTokenService().generateToken(patient.getEmail(), "patient");
            session.setAttribute("token", token);
            session.setAttribute("patientId", patient.getId());
            System.out.println("✅ Patient login successful. Redirecting to pages/loggedPatientDashboard.html");
            return "loginPatient" ;
        } else {
            System.out.println("❌ Password mismatch.");
        }
    } else {
        System.out.println("❌ Patient not found.");
    }
    break;

    }

    model.addAttribute("error", "Invalid email or password");
    model.addAttribute("role", role);
    model.addAttribute("login", new Login());
    return "login";
}

  @GetMapping("/adminDashboard/{token}")
public String adminDashboard(@PathVariable String token) {
    System.out.println("🔐 Validating admin token: " + token);
    
    boolean isValid = service.getTokenService().validateToken(token, "admin");

    if (isValid) {
        System.out.println("✅ Admin token valid. Loading adminDashboard.html");
        return "admin/adminDashboard";
    }

    System.out.println("❌ Invalid or expired admin token.");
    return "redirect:/";


}


    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {
        if (service.getTokenService().validateToken(token, "doctor")) {
            return "doctor/doctorDashboard";
        }
        return "redirect:/";
    }
    @GetMapping("/patientDashboard/{token}")
    public String patientDashboard(@PathVariable String token) {
        if (service.getTokenService().validateToken(token, "patient")) {
            return "loggedPatient";
        }
        return "redirect:/";
    }
}
