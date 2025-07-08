# Smart Clinic Managemenrt System
# 🏥 Smart Clinic Management System

A modern, secure, and modular full-stack web application built with **Java Spring Boot**, **MySQL**, **MongoDB**, **JWT Authentication**, and a clean **Thymeleaf + JS frontend**, designed to manage clinic operations such as doctor and patient management, appointments, prescriptions, and role-based dashboards.

---

## 🚀 Features

### ✅ Authentication
- Role-based login: Admin, Doctor, and Patient
- Secure login with JWT token-based validation
- Spring Security configuration for route access control

### 🩺 Admin Module
- Add, update, delete and manage doctors
- View all patients and their records
- View daily appointment report (via stored procedure)
- Identify most active doctor monthly and yearly (via stored procedures)

### 👨‍⚕️ Doctor Module
- View today's appointments
- View and write prescriptions for assigned patients
- Access patient history
- Green-themed modern dashboard UI

### 👤 Patient Module
- Book appointments with available doctors
- Filter doctors by name or specialty
- View appointment history and prescriptions
- Personalized dashboard

---

## 🛠️ Technologies Used

### Backend
- Java 17
- Spring Boot 3
- Spring MVC, JPA, Security
- MySQL + MongoDB
- JWT Token Authentication
- Dockerized MySQL setup
- Stored Procedures

### Frontend
- Thymeleaf
- HTML5, CSS3, JavaScript (Modular)
- FontAwesome Icons
- Responsive dashboards per role

---

## 🗂️ Project Structure

├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/smartclinic/back_end/
│           │       ├── config/
│           │       │   └── WebConfig.java
|           |        |   |__SecurityConfig.java 
│           │       │
│           │       ├── controllers/
│           │       │   ├── AdminController.java
│           │       │   ├── AppointmentController.java
│           │       │   ├── DoctorController.java
│           │       │   ├── PatientController.java
│           │       │   ├── PrescriptionController.java
│           │       │   └── ValidationFailed.java
│           │       │
│           │       ├── dto/
│           │       │   ├── AppointmentDTO.java
│           │       │   └── Login.java
│           │       │
│           │       ├── models/
│           │       │   ├── Admin.java
│           │       │   ├── Appointment.java
│           │       │   ├── Doctor.java
│           │       │   ├── Patient.java
│           │       │   └── Prescription.java
│           │       │
│           │       ├── mvc/
│           │       │   └── DashboardController.java
│           │       │
│           │       ├── repo/
│           │       │   ├── AdminRepository.java
│           │       │   ├── AppointmentRepository.java
│           │       │   ├── DoctorRepository.java
│           │       │   ├── PatientRepository.java
│           │       │   └── PrescriptionRepository.java
│           │       │
│           │       ├── services/
│           │       │   ├── AppointmentService.java
│           │       │   ├── DoctorService.java
│           │       │   ├── PatientService.java
│           │       │   ├── PrescriptionService.java
│           │       │   ├── TokenService.java
│           │       │   └── Service.java
│           │       │
│           │       └── SmartClinicApplication.java
│           │
│           └── resources/
│               ├── static/
│               │   ├── assets/css/
│               │   │   ├── addPrescription.css
│               │   │   ├── adminDashboard.css
│               │   │   ├── doctorDashboard.css
│               │   │   ├── index.css
│               │   │   ├── patientDashboard.css
│               │   │   ├── style.css
│               │   │   └── updateAppointment.css
│               │   │
│               │   └── js/
│               │       ├── components/
│               │       │   ├── appointmentRow.js
│               │       │   ├── doctorCard.js
│               │       │   ├── footer.js
│               │       │   ├── header.js
│               │       │   ├── modals.js
│               │       │   ├── patientRecorRow.js
│               │       │   └── patientRows.js
│               │       │
│               │       ├── config/
│               │       │   └── config.js
│               │       │
│               │       ├── services/
│               │       │   ├── appointmentRecordServices.js
│               │       │   ├── doctorServices.js
│               │       │   ├── index.js
│               │       │   ├── patientServices.js
│               │       │   └── prescriptionServices.js
│               │       │
│               │       ├── addPrecription.js
│               │       ├── adminDashboard.js
│               │       ├── appointmentRecord.js
│               │       ├── doctorDashboard.js
│               │       ├── loggedDashboard.js
│               │       ├── loggedPatient.js
│               │       ├── patientAppointmnet.js
│               │       ├── patientRecordService.js
│               │       ├── render.js
│               │       ├── updateAppointment.js
│               │       └── util.js
│               │
│               ├── pages/
│               │   ├── addPrescription.html
│               │   ├── loggedPatientDashboard.html
│               │   ├── patientAppointment.html
│               │   ├── patientDashboard.html
│               │   ├── patientrecord.html
│               │   └── updateAppointment.html
│               │   |---defineRole.html
|                |---index.html
│               └── templates/
│                   ├── admin/
│                   │   └── adminDashboard.html
│                   └── doctor/
│                   |    └── doctorDashboard.html
│                   |__login.html    
|                   |___loggedPatient.html
└── test/
    └── java/
        └── com/smartclinic/back_end/
            └── SmartclinicApplicationTest.java

---

## 🧪 Stored Procedures Used

- `GetDailyAppointmentReportByDoctor`
- `GetDoctorWithMostPatientsByMonth`
- `GetDoctorWithMostPatientsByYear`

Sample execution:
```sql
CALL GetDailyAppointmentReportByDoctor('2025-07-04');
🔐 JWT Authentication
Each login generates a token (e.g., /doctorDashboard/{token})

Validated on dashboard routes using custom TokenService

🐳 Docker Setup for MySQL

docker run --name mysql-clinic -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=cms -p 3306:3306 -d mysql:8.0
📦 API Endpoints
Method	Endpoint	Role	Description
POST	/api/login	All	Login and redirect with JWT
GET	/admin/doctors	Admin	View all doctors
GET	/doctor/{id}/appointments	Doctor	Get today's appointments
GET	/patient/{id}/appointments	Patient	View booked appointments
GET	/doctors?speciality=xyz	Patient	Search doctors by specialty





📂 Run Locally
1. Clone the Repo

git clone https://github.com/yourusername/smart-clinic-management.git
cd smart-clinic-management
2. Run MySQL in Docker

docker start mysql-clinic  # if already created
3. Start Spring Boot App
./mvnw spring-boot:run
4. Access the App
Define Role: http://localhost:8080/pages/defineRole.html

Login per role

Redirects to respective dashboards

📌 Author
Ashwini Kumar Khatua
IBM Java Developer Capstone Project
© 2025 Smart Clinic Solutions

🧾 License
This project is licensed under the MIT License. See LICENSE for details.