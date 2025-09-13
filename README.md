# Book Network API - JWT Authentication Template

A production-ready, secure RESTful API template featuring JWT-based authentication, built with Spring Boot 3.x. This template provides a solid foundation for building secure, scalable applications with modern authentication practices.

## Features

### Authentication & Security

- **JWT-based Authentication** - Stateless token-based authentication
- **Email Verification** - Secure user registration with email confirmation
- **Role-Based Access Control** - Fine-grained authorization with user roles
- **Password Security** - BCrypt password hashing with strength configuration
- **CSRF Protection** - Built-in protection against Cross-Site Request Forgery
- **HTTPS Ready** - Configured for secure communication

### Monitoring & Observability

- **Spring Boot Actuator** - Comprehensive application monitoring
- **Prometheus & Grafana** - Metrics collection and visualization
- **Sentry Integration** - Real-time error tracking and monitoring
- **Elasticsearch** - Advanced search and analytics capabilities

### Developer Experience

- **Docker Compose** - Containerized development environment
- **OpenAPI 3.0 Documentation** - Interactive API documentation
- **Database Migrations** - Flyway for database version control
- **Testing** - Comprehensive test suite with JUnit 5 and MockMvc

## Tech Stack

- **Backend**: Java 17, Spring Boot 3.x, Spring Security
- **Authentication**: JWT, OAuth2 (extensible)
- **Database**: PostgreSQL with Hibernate ORM
- **Search**: Elasticsearch 8.x
- **Monitoring**: Prometheus, Grafana, Spring Boot Actuator
- **Error Tracking**: Sentry
- **Build Tool**: Maven
- **Containerization**: Docker, Docker Compose

## 🚀 Quick Start

### Prerequisites

- Java 17 or higher
- Docker and Docker Compose
- Maven 3.8+ (optional, using Maven Wrapper)

### Running with Docker Compose

```bash
# Build and start all services
docker-compose up -d --build

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

### Access Services

- **Application**: http://localhost:8080/api/v1
- **Swagger UI**: http://localhost:8088/api/v1/swagger-ui/index.html
- **OpenAPI Docs**: http://localhost:8088/api/v1/v3/api-docs
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)
- **Elasticsearch**: http://localhost:9200

## 🔧 Configuration

### Environment Variables

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

## 📚 API Endpoints

### Authentication

- `POST /api/v1/auth/register` - Register a new user
- `POST /api/v1/auth/authenticate` - Authenticate and get JWT token
- `GET /api/v1/auth/activate-account` - Activate account with token

### User Management

- `GET /api/v1/users/me` - Get current user profile
- `PUT /api/v1/users/me` - Update user profile
- `DELETE /api/v1/users/me` - Delete user account

## 🧪 Testing

Run tests with Maven:

```bash
./mvnw test
```

## 📈 Monitoring

### Prometheus Metrics

- Endpoint: `/actuator/prometheus`
- Scrape interval: 15s (configured in Prometheus)

### Health Checks

- Endpoint: `/actuator/health`
- Includes: Database, Disk Space, Ping

1. **Register a new user**

   ```http
   POST http://localhost:8088/api/v1/auth/register
   Content-Type: application/json

   {
   "email": "test@mail.com",
   "password": "as3dw§$dopskDASD",
   "firstName": "YourFirstName",
   "lastName": "YourLastName"
   }
   ```

2. **Check your email**

   - Access MailDev at http://localhost:1080/#/
   - Find the activation email and copy the activation token

3. **Activate your account**

   ```http
   GET http://localhost:8088/api/v1/auth/activate-account?token=YOUR_ACTIVATION_TOKEN
   ```

4. **Authenticate**

   ```http
   POST http://localhost:8088/api/v1/auth/authenticate
   Content-Type: application/json

   {
     "email": "test1@mail.com",
     "password": "PassWord123!"
   }
   ```

   Example response:

   ```json
   {
     "success": true,
     "data": {
       "token": "eyJhbGciOiJIUzI1NiJ9.eyJmdWxsTmFtZSI6InN0cmluZyBzdHJpbmciLCJlbWFpbCI6Im9uaW9uNEBtYWlsLmNvbSIsImF1dGhvcml0aWVzIjpbIlVTRVIiLCJVU0VSIl0sInN1YiI6Im9uaW9uNEBtYWlsLmNvbSIsImlhdCI6MTc1NzYxOTcyOSwiZXhwIjoxNzU3NjI4MzY5fQ.zmgNVGAWooPqMGUu8m7DZ-3UaHRT8d-wjphcyVajmeY"
     },
     "message": "Authentication successful"
   }
   ```

## Running Tests

To run the tests for this project, use Maven:

```bash
mvn test
```

This will execute all unit and integration tests defined in the project using JUnit and MockMvc.

http://localhost:8088/api/v1/swagger-ui/index.html#/authentication-controller/activate

### 3️⃣ **MailDev (SMTP Dev Mail Server)**

- **Web UI port:** `1080`
- **SMTP port:** `1025`
- **Web URL:**

```
http://localhost:1080
```

- Use this UI to see emails sent by your application (activation emails, password resets, etc.)

---

### 4️⃣ **Prometheus**

- **Port:** `9090`
- **URL:**

```
http://localhost:9090
```

- Check metrics collected from Spring Boot Actuator (`/actuator/prometheus` endpoint).

---

### 5️⃣ **Grafana**

- **Port:** `3000`
- **URL:**

```
http://localhost:3000
```

- **Login:** `admin` / `admin`
- Connect Grafana to Prometheus as a data source to visualize your app metrics.

---

### 6️⃣ **Elasticsearch**

- **Port:** `9200`
- **URL:**

```
http://localhost:9200
```

- You can query with:

```
http://localhost:9200/_cat/indices?v
```

- Good for logging, search, and monitoring purposes if you integrate with something like Kibana or Sentry.
