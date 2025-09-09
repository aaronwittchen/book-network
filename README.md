# Spring Boot JWT Authentication Template (for a Book Network API)

A secure RESTful API for user authentication and management with JWT-based authentication, email verification, and role-based access control.

## Features

- **User Authentication**
  - User registration with email verification
  - JWT-based authentication
  - Password encryption
  - Account activation via email
  - Role-based access control (RBAC)

- **Security**
  - JWT token authentication
  - Secure password hashing
  - CSRF protection
  - Role-based authorization
  - Secure session management (stateless)

- **API Documentation**
  - TODO: Swagger/OpenAPI documentation available at `/swagger-ui.html`

## Tech Stack

- **Backend**: Java 17, Spring Boot 3.x
- **Security**: Spring Security, JWT
- **Database**: Configured through properties
- **Email**: JavaMail for email notifications (MailDev for development)
- **Build Tool**: Maven
- **Testing**: JUnit, MockMvc

## Quick Start

1. **Start the application**
   ```bash
   docker-compose up -d
````

2. **Register a new user**

   ```http
   POST http://localhost:8088/api/v1/auth/register
   Content-Type: application/json

   {
     "firstname": "test",
     "lastname": "test",
     "email": "test@mail.com",
     "password": "password11"
   }
   ```

3. **Check your email**

   * Access MailDev at [http://localhost:1080/#/](http://localhost:1080/#/)
   * Find the activation email and copy the activation token

4. **Activate your account**

   ```http
   GET http://localhost:8088/api/v1/auth/activate-account?token=YOUR_ACTIVATION_TOKEN
   ```

5. **Authenticate**

   ```http
   POST http://localhost:8088/api/v1/auth/authenticate
   Content-Type: application/json

   {
     "email": "onion1@mail.com",
     "password": "password"
   }
   ```

   Example response:

   ```json
   {
     "token": "eyJhbGciOiJIUzI1NiJ9.eyJmdWxsTmFtZSI6InRlc3QgdGVzdCIsInN1YiI6Im9uaW9uMUBtYWlsLmNvbSIsImlhdCI6MTc1NzQzOTU0MCwiZXhwIjoxNzU3NDQ4MTgwLCJhdXRob3JpdGllcyI6WyJVU0VSIl19.mqiVwsAHsQHz1QAhEeU-3HsL-nCsRiUnsA0BvOaD8bU"
   }
   ```

## Running Tests

To run the tests for this project, use Maven:

```bash
mvn test
```

This will execute all unit and integration tests defined in the project using JUnit and MockMvc.