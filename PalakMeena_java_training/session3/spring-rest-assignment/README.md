# Spring REST Assignment - User Management API


### Main Features

1. **Search & Filter Users** - Find users by name, age, or role with flexible filtering
2. **Submit New Users** - Create and validate new user submissions
3. **Delete Users** - Remove users with confirmation requirement
4. **Centralized Exception Handling** - Consistent error responses across all APIs
5. **In-Memory Data Store** - Pre-loaded with 7 dummy users for testing

---

## Technology Stack

- **Framework**: Spring Boot 4.0.5
- **Build Tool**: Maven
- **Java Version**: Java 17
- **Dependency Injection**: Constructor-based (no field/setter injection)
- **Data Storage**: In-memory ArrayList (no external database)

---

## Project Structure

```
spring-rest-assignment/
├── src/main/java/com/palak/springrestassignment/
│   ├── SpringRestAssignmentApplication.java    (Main entry point with @SpringBootApplication)
│   ├── controller/
│   │   └── UserController.java                 (REST API endpoints)
│   ├── service/
│   │   └── UserService.java                    (Business logic layer)
│   ├── repository/
│   │   └── UserRepository.java                 (Data access layer with dummy data)
│   ├── model/
│   │   ├── User.java                           (User entity)
│   │   ├── SubmitRequest.java                  (Request DTO)
│   │   └── ApiResponse.java                    (Generic response wrapper)
│   └── exception/
│       ├── UserNotFoundException.java           (Custom exception)
│       └── GlobalExceptionHandler.java         (Centralized error handling)
├── src/main/resources/
│   └── application.properties                  (Spring configuration)
└── pom.xml                                     (Maven dependencies)
```

---

## REST API Endpoints

### 1. GET /users/search
**Search and filter users by name, age, or role**

**Request Parameters (all optional):**
- `name` (String) - Filter by user name (case-insensitive)
- `age` (Integer) - Filter by exact age
- `role` (String) - Filter by user role (case-insensitive)

**Examples:**
```bash
# Get all users
GET /users/search

# Search by name
GET /users/search?name=Priya

# Search by age
GET /users/search?age=30

# Search by role
GET /users/search?role=USER

# Search with multiple filters (AND condition)
GET /users/search?age=30&role=USER
```

**Response:**
```json
{
  "success": true,
  "message": "Users fetched successfully.",
  "data": [
    {
      "id": 1,
      "name": "Priya Sharma",
      "age": 25,
      "role": "USER",
      "email": "priya@example.com"
    }
  ]
}
```

**Status Code:** 200 OK

---

### 2. POST /submit
**Submit and create a new user with validation**

**Request Body:**
```json
{
  "name": "John Doe",
  "age": 28,
  "email": "john@example.com",
  "role": "USER"
}
```

**Validation Rules:**
- `name`: Must not be empty
- `email`: Must not be empty and contain '@'
- `age`: Must be positive number
- `role`: Defaults to "USER" if not provided

**Success Response (201 Created):**
```json
{
  "success": true,
  "message": "User submitted successfully.",
  "data": {
    "id": 8,
    "name": "John Doe",
    "age": 28,
    "role": "USER",
    "email": "john@example.com"
  }
}
```

**Error Response (400 Bad Request):**
```json
{
  "success": false,
  "message": "Age must be a positive number.",
  "data": null
}
```

---

### 3. DELETE /users/{id}
**Delete a user with confirmation requirement**

**Request Parameters:**
- `confirm` (boolean) - Must be `true` to actually delete (default: false)
- `{id}` (Long) - User ID in URL path

**Examples:**
```bash
# Request without confirmation (no deletion)
DELETE /users/1?confirm=false

# Request with confirmation (deletion happens)
DELETE /users/1?confirm=true
```

**Response When Confirm=False:**
```json
{
  "success": true,
  "message": "Confirmation required. Please send confirm=true to proceed with deletion.",
  "data": null
}
```

**Response When Confirm=True (Success):**
```json
{
  "success": true,
  "message": "User with id 1 has been deleted successfully.",
  "data": null
}
```

**Error Response (404 Not Found):**
```json
{
  "success": false,
  "message": "No user found with id: 999",
  "data": null
}
```

---

## Dummy Users (Pre-loaded Data)

The repository comes with 7 dummy users for testing:

| ID | Name | Age | Role | Email |
|----|------|-----|------|-------|
| 1 | Priya Sharma | 25 | USER | priya@example.com |
| 2 | Aryan Singh | 30 | USER | aryan@example.com |
| 3 | Sneha Patel | 28 | MODERATOR | sneha@example.com |
| 4 | Rahul Verma | 30 | ADMIN | rahul@example.com |
| 5 | Palak Joshi | 22 | USER | palak@example.com |
| 6 | Neha Gupta | 35 | MODERATOR | neha@example.com |
| 7 | Vikram Rao | 30 | USER | vikram@example.com |

---

## How To Build & Run

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Build the Project
```bash
mvn clean package
```

### Run the Application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

---



## Layered Architecture

This project follows a strict **3-layer architecture**:

### **Controller Layer** (UserController)
- Handles HTTP requests and responses
- Only delegates to service layer
- Contains NO business logic
- Routes requests to appropriate service methods

### **Service Layer** (UserService)
- Contains ALL business logic
- Validates input data
- Implements filtering logic
- Manages deletion confirmation
- Calls repository for data access

### **Repository Layer** (UserRepository)
- Handles data access and storage
- Contains only CRUD operations
- In-memory ArrayList for data storage
- No business logic in this layer


---

## Exception Handling

### GlobalExceptionHandler
Centralized exception handling for consistent error responses:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(UserNotFoundException.class)
    // Returns 404 status for user not found
    
    @ExceptionHandler(IllegalArgumentException.class)
    // Returns 400 status for validation errors
    
    @ExceptionHandler(Exception.class)
    // Returns 500 status for unexpected errors
}
```


---





