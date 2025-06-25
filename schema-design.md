# 🗃️ Schema Design – Smart Clinic Management System

This file outlines the database design for the Smart Clinic Management System.

---

## 🛢️ MySQL Database Design

The system manages structured data such as patients, doctors, appointments, and admin users in a relational database (MySQL). Below are the core tables and their definitions:

---

### 1. `users` Table
Stores login and role-related information for all users (patients, doctors, admins).

```sql
CREATE TABLE users (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  email VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  role ENUM('ADMIN', 'DOCTOR', 'PATIENT') NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```
⚙️ Role-based design allows a single table for authentication, simplifying user management.

2. patients Table
Stores personal and contact details of patients.

```sql:
CREATE TABLE patients (
  patient_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  full_name VARCHAR(100) NOT NULL,
  gender ENUM('MALE', 'FEMALE', 'OTHER'),
  dob DATE,
  phone VARCHAR(15),
  address TEXT,
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
```
🧾 Linked to users table for login/auth integration.

3. doctors Table
Holds professional and contact information of doctors.

```sql:
CREATE TABLE doctors (
  doctor_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  full_name VARCHAR(100) NOT NULL,
  specialization VARCHAR(100),
  availability VARCHAR(100), -- e.g. "Mon-Fri, 10am-5pm"
  phone VARCHAR(15),
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
```
🩺 availability can later be expanded to a normalized schedule table.

4. appointments Table
Stores all appointment bookings between patients and doctors.

```sql:
CREATE TABLE appointments (
  appointment_id INT AUTO_INCREMENT PRIMARY KEY,
  doctor_id INT NOT NULL,
  patient_id INT NOT NULL,
  appointment_date DATE NOT NULL,
  appointment_time TIME NOT NULL,
  status ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED') DEFAULT 'PENDING',
  notes TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id),
  FOREIGN KEY (patient_id) REFERENCES patients(patient_id)
);
```
📅 Captures the full scheduling lifecycle with status and notes.

🍃 MongoDB Collection Design
We use MongoDB for flexible, document-based data like prescriptions, which may vary in structure and length between patients.
prescriptions Collection

```json
{
  "_id": ObjectId("662f41d9e0b4b8a8f89e2123"),
  "patient_id": 12,
  "doctor_id": 5,
  "appointment_id": 32,
  "date": "2025-06-21T10:00:00Z",
  "medications": [
    {
      "name": "Paracetamol",
      "dosage": "500mg",
      "frequency": "Twice a day",
      "duration": "5 days"
    },
    {
      "name": "Amoxicillin",
      "dosage": "250mg",
      "frequency": "Three times a day",
      "duration": "7 days"
    }
  ],
  "instructions": "Take medications after meals. Report if fever persists.",
  "created_at": "2025-06-21T10:30:00Z"
}
```
🧾 Prescriptions vary from patient to patient — storing them in MongoDB allows us to keep arrays (like medications) and flexible structures without altering schemas.

✅ Summary of Design Decisions
> MySQL is used for structured, relational data with enforced constraints and relationships.
> MongoDB is chosen for prescriptions due to its flexibility and support for nested data.
> A users table simplifies role-based access control using a unified authentication model.
> appointments table bridges patients and doctors with time-sensitive scheduling and status tracking.
