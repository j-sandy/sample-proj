# Sample Spring Boot Project

A simple Spring Boot REST API project demonstrating basic CRUD operations with user management.

## Features

- RESTful API for user management
- In-memory H2 database for development
- Bean validation
- Unit tests with MockMvc
- Spring Boot DevTools for development
- Cross-origin resource sharing (CORS) support

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.1**
- **Spring Web** - REST API endpoints
- **Spring Data JPA** - Data persistence
- **H2 Database** - In-memory database for development
- **Spring Boot Validation** - Input validation
- **Spring Boot DevTools** - Development tools
- **JUnit 5** - Unit testing

## Project Structure

```
src/
├── main/
│   ├── java/com/example/sampleproj/
│   │   ├── SampleProjApplication.java     # Main application class
│   │   ├── controller/
│   │   │   ├── HelloController.java       # Basic endpoints
│   │   │   └── UserController.java        # User CRUD endpoints
│   │   ├── model/
│   │   │   └── User.java                  # User entity
│   │   └── service/
│   │       └── UserService.java           # User business logic
│   └── resources/
│       └── application.properties         # Configuration
└── test/
    └── java/com/example/sampleproj/
        ├── SampleProjApplicationTests.java
        └── controller/
            └── UserControllerTest.java
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+

### Running the Application

1. **Navigate to project directory:**
   ```bash
   cd /home/ubuntu/F/ai/sample-proj
   ```

2. **Run with Maven:**
   ```bash
   mvn spring-boot:run
   ```

3. **Or build and run:**
   ```bash
   mvn clean package
   java -jar target/sample-proj-0.0.1-SNAPSHOT.jar
   ```

The application will start on `http://localhost:8080`

### Running Tests

```bash
mvn test
```

## API Endpoints

### Basic Endpoints
- `GET /` - Welcome message
- `GET /hello?name=YourName` - Personalized greeting
- `GET /health` - Health check

### User Management
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users` - Create new user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user
- `GET /api/users/count` - Get user count

### Sample User JSON
```json
{
  "name": "John Doe",
  "email": "john.doe@example.com"
}
```

## Database Access

The application uses an in-memory H2 database. You can access the H2 console at:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`

## Development Features

- **Hot Reload:** Spring Boot DevTools enables automatic restart on code changes
- **Database Console:** H2 console for inspecting data during development
- **Validation:** Bean validation for user input
- **CORS:** Cross-origin requests enabled for frontend integration

## Example Usage

### Create a user:
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name": "Alice Johnson", "email": "alice@example.com"}'
```

### Get all users:
```bash
curl http://localhost:8080/api/users
```

### Get user by ID:
```bash
curl http://localhost:8080/api/users/1
```

## Configuration

Key configuration properties in `application.properties`:
- Server port: `8080`
- Database: H2 in-memory
- JPA: Auto-create tables, show SQL queries
- Logging: Debug level for application packages

## Next Steps

This is a basic template. Consider adding:
- Database migrations (Flyway/Liquibase)
- Security (Spring Security)
- API documentation (OpenAPI/Swagger)
- Docker support
- Production database (PostgreSQL/MySQL)
- Caching (Redis)
- Message queues (RabbitMQ/Kafka)