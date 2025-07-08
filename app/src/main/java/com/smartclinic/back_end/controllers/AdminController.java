
package com.smartclinic.back_end.controllers;

import com.smartclinic.back_end.models.Admin;
import com.smartclinic.back_end.services.Services;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// Add this import if Login class exists in models package
import com.smartclinic.back_end.DTO.Login;

@RestController
@RequestMapping("${api.path}admin")
public class AdminController {

// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to indicate that it's a REST controller, used to handle web requests and return JSON responses.
//    - Use `@RequestMapping("${api.path}admin")` to define a base path for all endpoints in this controller.
//    - This allows the use of an external property (`api.path`) for flexible configuration of endpoint paths.


// 2. Autowire Service Dependency:
//    - Use constructor injection to autowire the `Service` class.
//    - The service handles core logic related to admin validation and token checking.
//    - This promotes cleaner code and separation of concerns between the controller and business logic layer.


// 3. Define the `adminLogin` Method:
//    - Handles HTTP POST requests for admin login functionality.
//    - Accepts an `Admin` object in the request body, which contains login credentials.
//    - Delegates authentication logic to the `validateAdmin` method in the service layer.
//    - Returns a `ResponseEntity` with a `Map` containing login status or messages.
private final Services service;

    public AdminController(Services service) {
        this.service = service;
    }

    // POST /api/admin/login
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> adminLogin(@RequestBody Login login) {
        return service.validateAdmin(login);
    }


}

