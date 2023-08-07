# Ambulance Service Provider System

## Description

The Ambulance Service Management System is a web application that manages ambulance services and staff allocation. The system allows users to sign up, log in, and request ambulance services based on their user type. It also enables administrators to invite and manage staff members, such as doctors, attendees, drivers, and employees, who are available for ambulance service allocation. The system ensures efficient allocation of ambulances and staff to fulfill emergency service requests.

## Features

1. **User Signup and Authentication:**
    - Users can sign up using their email, password and other details.
    - Passwords are properly validated, securely encrypted using the latest hashing algorithms.
    - Users can log in using their credentials and receive access and refresh tokens for secure authentication.

2. **User Role-Based Access:**
    - Users are assigned specific roles (e.g., individual, traffic patrol, admin, doctor, employee, attendee, driver) to access relevant features.
    - Restricted user roles are managed by administrators.

3. **User Search:**
    - Users can search for other users based on their first names, last names, or any partial name matches.
    - The system returns a list of users with their full names and user types.

4. **Ambulance Service Request:**
    - Users with roles "individual" and "traffic patrol" can request ambulance services.
    - The system allocates available ambulances and staff members (drivers, doctors, attendees, employees) for the service.
    - Ambulances and staff availability status is updated accordingly.

5. **Admin Functionality:**
    - Administrators can invite new users to the system, specifying their roles and user types.
    - Admins can manage ambulance staff availability and revert unavailable staff to the available status for new service requests.

6. **Notification System:**
    - The system generates notifications for users when they are assigned to ambulance service requests.
    - Users receive notifications in real-time to keep them informed.

## Endpoints

1. Admin Invite Staff: `POST /api/admin/invite-staff`
2. Admin Invite Organization: `POST /api/admin/invite-org`
3. Admin Add Ambulance: `POST /api/admin/add-ambulance`
4. Ambulance Service Request: `POST /api/ambulance/request`
5. Ambulance Service Revert Unavailable: `POST /api/ambulance/revert`
6. User Signup: `POST /api/auth/sign-up`
6. User email verification: `POST /api/auth/verify`
7. User Login: `POST /api/auth/authenticate`
8. Search Users: `GET /api/auth/search?name={partialName}`

## Technologies Used

- Java
- Spring Boot
- Spring Security
- JWT (JSON Web Tokens) for authentication
- Email service for Email verification
- PostgreSQL (or any other preferred database)
- Postman (for API testing)

## Setup Instructions

1. Clone the repository to your local machine.
2. Import the project into IntelliJ or any preferred IDE.
3. Set up your PostgreSQL database and update the database configuration in the application properties.
4. Run the application using the Spring Boot run configuration or command-line.
5. Access the API endpoints using Postman or any API testing tool.

## Contributing

Contributions to the Ambulance Service Management System project are welcome. To contribute, please follow these steps:

1. Fork the repository on GitHub.
2. Create a new branch with a descriptive name for your feature or bug fix.
3. Make your changes in the new branch.
4. Push your branch to your forked repository.
5. Create a pull request from your branch to the main repository's master branch.
6. Wait for review and approval from project maintainers.


### Unified Modeling Language (UML)
![sign-up](src%2Fmain%2Fresources%2Fimages%2FASP UML)

### User sign up and registration
![sign-up](src%2Fmain%2Fresources%2Fimages%2Fimg_7.png)

### Email Verification
![Email Verification](src%2Fmain%2Fresources%2Fimages%2Fimg_5.png)

### User authentication
![authentication](src%2Fmain%2Fresources%2Fimages%2Fimg.png)

### Admin inviting an Organization (e.g. Hospital and Healthcare)
![Organization Invitation](src%2Fmain%2Fresources%2Fimages%2Fimg_1.png)

### Admin inviting a staff (e.g. Doctor, Attendee and Driver)
![Staff Invitation](src%2Fmain%2Fresources%2Fimages%2Fimg_3.png)

### Admin adding an ambulance
![Amin adding an Ambulance](src%2Fmain%2Fresources%2Fimages%2Fimg_2.png)

### Individual Requesting for an Ambulance
![Ambulance Request](src%2Fmain%2Fresources%2Fimages%2Fimg_4.png)

### Username search
![Username search](src%2Fmain%2Fresources%2Fimages%2Fimg_6.png)


## Contact

If you have any questions or need further assistance, please feel free to reach out to me at [bolaoyeks@gmail.com](mailto:bolaoyeks@gmail.com). I appreciate your interest and feedback!
