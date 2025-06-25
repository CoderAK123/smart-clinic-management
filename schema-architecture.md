Section 1: Application Architecture Overview:

The Smart Clinic Management System is a full-stack Spring Boot application that follows a hybrid architecture using both MVC (Model-View-Controller) and RESTful APIs. The Admin and Doctor dashboards are rendered using Thymeleaf templates, allowing for dynamic, server-side HTML rendering, while the Patient and Prescription modules rely on RESTful APIs for communication with the frontend. The backend is responsible for processing requests, performing business logic, and interacting with two distinct databases: MySQL, which stores structured data such as user profiles, roles, appointments, and login credentials, and MongoDB, which handles unstructured, flexible data like medical prescriptions.
The backend architecture is layered and modular. All controllers (MVC and REST) route user requests through a centralized service layer, which handles business logic and communicates with Spring Data repositories. These repositories abstract database operations—JPA entities manage MySQL data, while MongoDB document models are used for NoSQL collections. The application uses JWT-based authentication for secure login and role-based access control to restrict access to protected routes and features based on user roles (Admin, Doctor, Patient).

Section 2: Numbered Flow of Data and Control:

1.The user logs into the system or accesses the dashboard through a web browser.
2.Based on the user role (Admin/Doctor/Patient), the frontend either renders a Thymeleaf-based dashboard or interacts with the backend via REST API calls.
3.The request is received by the appropriate controller—either a Spring MVC controller (for server-side rendered pages) or a REST controller (for API-driven requests).
4.The controller delegates the request to a service layer, which handles business logic such as verifying appointments, creating records, or fetching user-specific data.
5.The service layer interacts with the corresponding Spring Data repositories—MySQL repositories for relational data and MongoDB repositories for document-based data.
6.The database returns the requested data (or saves new data), and the service layer returns the result to the controller.
7.The controller either renders a Thymeleaf HTML view (Admin/Doctor Dashboard) or sends a JSON response back to the frontend (e.g., for patient interactions via REST).

