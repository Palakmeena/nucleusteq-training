# Spring Core Assignment - User Management & Notification System

## Project Overview
A Spring Boot application demonstrating layered architecture principles, dependency injection, and REST API design with constructor-based injection only.

## Technology Stack
- **Java Version:** Java 17
- **Spring Boot:** 3.x
- **Build Tool:** Maven
- **Architecture:** Layered (Controller → Service → Repository)

## Project Structure

```
src/main/java/com/palak/springcoreassignment/
├── controller/
│   ├── UserController.java           (REST endpoints for user management)
│   ├── MessageController.java        (Dynamic message formatting)
│   └── NotificationController.java   (Notification triggers)
├── service/
│   ├── UserService.java              (User business logic)
│   ├── MessageService.java           (Message handling)
│   └── NotificationService.java      (Notification logic)
├── repository/
│   └── UserRepository.java           (Data access layer)
├── model/
│   ├── User.java                     (User entity)
│   ├── NotificationRequest.java      (Notification DTO)
│   └── ApiResponse.java              (API response wrapper)
├── component/
│   └── NotificationComponent.java    (Utility component)
├── formatter/
│   ├── MessageFormatter.java         (Base interface)
│   ├── ShortMessageFormatter.java    (Short format implementation)
│   └── LongMessageFormatter.java     (Long format implementation)
├── exception/
│   ├── UserNotFoundException.java     (Custom exception)
│   └── GlobalExceptionHandler.java   (Global error handling)
└── SpringCoreAssignmentApplication.java (Main app class)
```

## REST APIs

### User Management
- **GET** `/users` - Fetch all users
- **POST** `/users` - Create new user
- **GET** `/users/{id}` - Fetch user by ID

### Message Formatting
- **GET** `/message?type=SHORT` - Get short format message
- **GET** `/message?type=LONG` - Get long format message

### Notifications
- **POST** `/notify` - Trigger notification

## Key Features

### 1. Dependency Injection
- Constructor-based injection only (no field or setter injection)
- Spring's IoC container manages all beans
- Automatic component scanning via `@Component` and `@Service`

### 2. Layered Architecture
- **Controller Layer:** Handles HTTP requests
- **Service Layer:** Contains business logic
- **Repository Layer:** Manages data access
- **Component Layer:** Reusable utilities

### 3. Message Formatter System
- Dynamic formatter selection at runtime
- Strategy pattern implementation
- Service decides formatter type based on request

### 4. Exception Handling
- Custom `UserNotFoundException` for domain-specific errors
- `GlobalExceptionHandler` for centralized error management
- Proper HTTP status codes in responses

### 5. Data Access
- In-memory repository implementation (no database required)
- Dummy data for demonstration

## Running the Application

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run

# Application will start on http://localhost:8080
```

## Example Requests

### Create User
```bash
POST http://localhost:8080/users
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com"
}
```

### Get All Users
```bash
GET http://localhost:8080/users
```

### Format Message (Short)
```bash
GET http://localhost:8080/message?type=SHORT
```

### Send Notification
```bash
POST http://localhost:8080/notify
Content-Type: application/json

{
  "userId": 1,
  "message": "Test notification"
}
```

## Design Patterns Used

1. **Dependency Injection Pattern** - Spring manages dependencies
2. **Strategy Pattern** - Different message formatters
3. **Service Pattern** - Separation of business logic
4. **Repository Pattern** - Data access abstraction

## Concepts Demonstrated

- ✓ **IoC (Inversion of Control)** - Spring manages object lifecycle
- ✓ **Dependency Injection** - Constructor-based injection
- ✓ **Component Scanning** - Automatic bean detection
- ✓ **Layered Architecture** - Clear separation of concerns
- ✓ **Exception Handling** - Global error management
- ✓ **REST API Design** - Proper HTTP methods and responses

## Author
PalakMeena - Java Training Session 2

## License
MIT
