# Book Social Network

A full-stack application that enables users to manage and share books, interact with a community, and handle borrowing and returning functionality.

The project features a robust **RESTful API**, **JWT-based authentication**, and is built with **Spring Boot 3.x**.

---

## Features

### Security & Authentication

- **JWT-based Authentication** – Stateless token-based authentication
- **Email Verification** – Secure user registration with email confirmation
- **Role-Based Access Control** – Fine-grained authorization with user roles
- **Password Security** – BCrypt password hashing with configurable strength
- **CSRF Protection** – Built-in protection against cross-site request forgery
- **HTTPS Ready** – Configured for secure communication

### Monitoring & Observability

- **Spring Boot Actuator** – Comprehensive application monitoring
- **Prometheus & Grafana** – Metrics collection and visualization
- **Sentry Integration** – Real-time error tracking and monitoring
- **Elasticsearch** – Advanced search and analytics

### Development & Deployment

- **Docker Compose** – Containerized development environment
- **OpenAPI 3.0** – Interactive API documentation
- **Database Migrations** – Flyway for database version control
- **Testing** – JUnit 5 and MockMvc for unit and integration testing

---

## Tech Stack

- **Backend**: Java 17, Spring Boot 3.x, Spring Security
- **Authentication**: JWT, OAuth2 (extensible)
- **Database**: PostgreSQL with Hibernate ORM
- **Search**: Elasticsearch 8.x
- **Monitoring**: Prometheus, Grafana, Spring Boot Actuator
- **Error Tracking**: Sentry
- **Build Tool**: Maven
- **Containerization**: Docker, Docker Compose
- **Frontend**: Angular, Bootstrap (component-based architecture, lazy loading)

---

## Quick Start

### Prerequisites

- Java 17+
- Docker & Docker Compose
- Maven 3.8+ (or use Maven Wrapper)

### Running with Docker Compose

```bash
mvn clean install -DskipTests

# Build and start all services
docker-compose up -d --build

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

### Access Services

- **Application**: [http://localhost:8080/api/v1](http://localhost:8080/api/v1)
- **Swagger UI**: [http://localhost:8088/api/v1/swagger-ui/index.html](http://localhost:8088/api/v1/swagger-ui/index.html)
- **OpenAPI Docs**: [http://localhost:8088/api/v1/v3/api-docs](http://localhost:8088/api/v1/v3/api-docs)
- **Prometheus**: [http://localhost:9090](http://localhost:9090)
- **Grafana**: [http://localhost:3000](http://localhost:3000) (admin/admin)
- **Elasticsearch**: [http://localhost:9200](http://localhost:9200)
- **MailDev**: [http://localhost:1080](http://localhost:1080)

---

## Configuration

Create a `.env` file in the project root:

```env
# Database
DB_URL=jdbc:postgresql://postgres:5432/book_network
DB_USERNAME=postgres
DB_PASSWORD=postgres

# JWT
JWT_SECRET=your-jwt-secret-key-here
JWT_EXPIRATION=86400000  # 24 hours in milliseconds

# Sentry
SENTRY_DSN=your-sentry-dsn-here
```

---

## API Endpoints

### Authentication

- `POST /api/v1/auth/register` – Register a new user
- `POST /api/v1/auth/authenticate` – Authenticate and retrieve JWT token
- `GET /api/v1/auth/activate-account` – Activate account with token

### Example – Register a new user

```http
POST http://localhost:8088/api/v1/auth/register
Content-Type: application/json

{
  "email": "test@mail.com",
  "password": "PassWord123!",
  "firstName": "YourFirstName",
  "lastName": "YourLastName"
}
```

---

## Monitoring

- **Prometheus**: `/actuator/prometheus` (15s scrape interval)
- **Health Checks**: `/actuator/health` (Database, Disk Space, Ping)

---

## Example – Book Management

### Create a Book

```http
POST http://localhost:8088/api/v1/books
Content-Type: application/json

{
  "title": "Effective Java",
  "authorName": "Joshua Bloch",
  "isbn": "9780134685991",
  "synopsis": "A comprehensive guide to best practices in Java programming.",
  "shareable": true
}
```

### Example Response

```json
{
  "id": 202,
  "title": "Effective Java",
  "authorName": "Joshua Bloch",
  "isbn": "9780134685991",
  "synopsis": "A comprehensive guide to best practices in Java programming.",
  "owner": "string string",
  "cover": null,
  "rate": 0.0,
  "archived": false,
  "shareable": true,
  "message": "Book created successfully with id: 202"
}
```

---

## Business Requirements

- **Book Management** – CRUD operations + archiving
- **Book Sharing** – Mark books as shareable
- **Book Borrowing** – Borrow and return books with availability checks
- **User Authentication** – Registration, login, JWT-secured backend + Angular integration

---

## Backend Highlights

- Spring Boot 3, Spring Security, JWT
- JSR-303 validation with custom error handling
- Service layer with exception management
- Centralized exception handling
- Dockerized for environment-independent deployment

## Frontend Highlights

- Angular with lazy loading & route guards
- OpenAPI Generator for API services
- Bootstrap UI integration

---

## Testing

Run the test suite with Maven:

```bash
./mvnw test
```

This executes all unit and integration tests with **JUnit 5** and **MockMvc**.

---

## Extra Features

- Full CI pipeline
- Keycloak for advanced security and social authentication
- File upload support for book covers
- Future extensions planned for real-life functionality
