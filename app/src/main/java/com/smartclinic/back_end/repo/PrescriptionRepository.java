package com.smartclinic.back_end.repo;

import com.smartclinic.back_end.models.Prescription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionRepository extends MongoRepository<Prescription, String>  {
// 1. Extend MongoRepository:
//    - The repository extends MongoRepository<Prescription, String>, which provides basic CRUD functionality for MongoDB.
//    - This allows the repository to perform operations like save, delete, update, and find without needing to implement these methods manually.
//    - MongoRepository is tailored for working with MongoDB, unlike JpaRepository which is used for relational databases.

// Example: public interface PrescriptionRepository extends MongoRepository<Prescription, String> {}

// 2. Custom Query Method:

//    - **findByAppointmentId**:
//      - This method retrieves a list of prescriptions associated with a specific appointment.
//      - Return type: List<Prescription>
//      - Parameters: Long appointmentId
//      - MongoRepository automatically derives the query from the method name, in this case, it will find prescriptions by the appointment ID.
 
// 1. Find all prescriptions by appointment ID
    List<Prescription> findByAppointmentId(Long appointmentId);

}

