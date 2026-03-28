# VSMS Common Library

## Overview

The `vsms-common` project is a shared library that provides common functionality, configurations, and utilities across all Java microservices in the VSMS (Vehicle Sales Management System) architecture. It serves as a foundational module that ensures consistency, reduces code duplication, and promotes best practices across the entire microservices ecosystem.

## Role

This library acts as a **common dependency layer** that:
- Provides standardized configurations shared by all Java services
- Implements uniform error handling and response formats
- Supplies reusable exception classes for consistent error management
- Ensures security configurations are identical across services
- Reduces maintenance overhead by centralizing common code

## Responsibilities

### 1. **Security Configuration**
- Provides JWT-based OAuth2 resource server configuration
- Implements consistent security filter chains across all services
- Configures CSRF protection and endpoint authorization rules
- Permits public access to health and actuator endpoints while securing all other endpoints

### 2. **Exception Handling**
- Implements a global exception handler (`GlobalExceptionHandler`) for centralized error management
- Provides custom exception classes for common error scenarios:
  - `ResourceNotFoundException`: For resource not found scenarios (HTTP 404)
  - `ValidationException`: For validation errors (HTTP 400)
- Handles Spring validation errors (`MethodArgumentNotValidException`)
- Catches and handles unexpected exceptions with proper logging

### 3. **API Response Standardization**
- Provides a generic `ApiResponse<T>` wrapper for consistent API response formatting
- Ensures all services return responses in a uniform JSON structure
- Supports both success and error response scenarios
- Includes optional data payload and message fields

### 4. **Dependency Management**
- Centralizes common Spring Boot dependencies
- Manages versions for web, validation, security, and OAuth2 starters
- Includes Lombok for reducing boilerplate code
- Provides Jackson for JSON serialization/deserialization

## Project Structure

```
shared/vsms-common/
├── build.gradle                    # Build configuration and dependencies
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── vsms/
│                   └── common/
│                       ├── config/
│                       │   └── SecurityConfig.java          # JWT security configuration
│                       ├── exception/
│                       │   ├── GlobalExceptionHandler.java  # Centralized exception handler
│                       │   ├── ResourceNotFoundException.java # Resource not found exception
│                       │   └── ValidationException.java     # Validation exception
│                       └── response/
│                           └── ApiResponse.java            # Standardized API response wrapper
└── build/                        # Compiled output
```

## Key Components

### SecurityConfig
- **Purpose**: Configures JWT-based OAuth2 resource server security
- **Features**:
  - Disables CSRF protection (appropriate for stateless REST APIs)
  - Permits unauthenticated access to `/actuator/**` and `/health` endpoints
  - Requires authentication for all other endpoints
  - Configures JWT token validation

### GlobalExceptionHandler
- **Purpose**: Provides centralized exception handling across all services
- **Handlers**:
  - `ResourceNotFoundException` → HTTP 404 Not Found
  - `ValidationException` → HTTP 400 Bad Request
  - `MethodArgumentNotValidException` → HTTP 400 Bad Request (Spring validation)
  - Generic `Exception` → HTTP 500 Internal Server Error
- **Features**:
  - Logs all errors with appropriate log levels
  - Returns consistent `ApiResponse` error format

### ApiResponse
- **Purpose**: Standardizes API response structure across all services
- **Structure**:
  ```json
  {
    "success": true/false,
    "message": "Response message",
    "data": { ... }
  }
  ```
- **Methods**:
  - `success(T data, String message)`: Success response with custom message
  - `success(T data)`: Success response with default "OK" message
  - `error(String message)`: Error response with message

### Custom Exceptions
- **ResourceNotFoundException**: Thrown when a requested resource is not found
- **ValidationException**: Thrown when input validation fails

## Dependencies

| Dependency | Purpose |
|------------|---------|
| `spring-boot-starter-web` | REST API support and embedded server |
| `spring-boot-starter-validation` | Bean validation (JSR-380) |
| `spring-boot-starter-security` | Spring Security framework |
| `spring-boot-starter-oauth2-resource-server` | OAuth2 resource server for JWT validation |
| `lombok` | Boilerplate code reduction (getters, setters, etc.) |
| `jackson-databind` | JSON serialization/deserialization |

## Usage

### Adding as a Dependency

To use this common library in a service, add it to your service's `build.gradle`:

```gradle
dependencies {
    implementation project(':shared:vsms-common')
}
```

### Using ApiResponse

```java
import com.vsms.common.response.ApiResponse;

// Success response with data
return ResponseEntity.ok(ApiResponse.success(customerData, "Customer retrieved successfully"));

// Success response with default message
return ResponseEntity.ok(ApiResponse.success(customerData));

// Error response
return ResponseEntity.badRequest().body(ApiResponse.error("Invalid input"));
```

### Using Custom Exceptions

```java
import com.vsms.common.exception.ResourceNotFoundException;
import com.vsms.common.exception.ValidationException;

// Throw resource not found exception
throw new ResourceNotFoundException("Customer not found with id: " + customerId);

// Throw validation exception
throw new ValidationException("Email format is invalid");
```

### Security Configuration

The `SecurityConfig` is automatically applied to all services that include this library. No additional configuration is needed unless a service requires custom security rules.

## Benefits

1. **Consistency**: All services use the same security, error handling, and response formats
2. **Reduced Duplication**: Common code is centralized, reducing maintenance burden
3. **Best Practices**: Implements proven patterns for security and error handling
4. **Easier Onboarding**: New services can quickly adopt standard patterns
5. **Simplified Updates**: Security and configuration changes can be made in one place

## Build Information

- **Java Version**: 21
- **Spring Boot Version**: 3.4.3
- **Build Tool**: Gradle
- **Artifact Type**: Java Library (JAR)

## Independent Module Setup

### Current Architecture Limitations

**Important**: The current architecture has the following limitations for independent module development:

1. **No Root Multi-Module Setup**: Each service is configured as an independent Gradle project without a root `settings.gradle` that includes all modules
2. **No Published Artifact**: `vsms-common` is not published to a Maven repository (local or remote)
3. **Tight Coupling**: Services cannot independently reference `vsms-common` without the entire repository structure

### How Teams Can Leverage vsms-common

#### Option 1: Multi-Module Gradle Setup (Recommended)

Create a root `settings.gradle` file at the project root:

```gradle
// settings.gradle (at project root)
rootProject.name = 'vsms-microservices'

include ':shared:vsms-common'
include ':services:customer-service'
include ':services:auth-service'
include ':services:cost-service'
include ':services:vsms-hr'  // New HR module
```

Then in each service's `build.gradle`, add:

```gradle
dependencies {
    implementation project(':shared:vsms-common')
    // Remove duplicate dependencies that vsms-common provides
}
```

**Benefits**:
- Teams can work on their module independently
- Changes to vsms-common are immediately available to all services
- Single build command can build entire system
- Dependency versions are managed centrally

#### Option 2: Publish vsms-common as a Maven Artifact

Publish `vsms-common` to a Maven repository (local or remote):

```gradle
// In vsms-common/build.gradle, add:
publishing {
    publications {
        mavenJava(MavenPublication) {
            from components.java
            groupId = 'com.vsms'
            artifactId = 'vsms-common'
            version = '0.0.1-SNAPSHOT'
        }
    }
}
```

Then services can reference it as:

```gradle
dependencies {
    implementation 'com.vsms:vsms-common:0.0.1-SNAPSHOT'
}
```

**Benefits**:
- Complete independence - services can be in separate repositories
- Versioned releases of vsms-common
- No need for entire project structure

#### Option 3: Git Submodule (Hybrid Approach)

Add `vsms-common` as a Git submodule in each service repository:

```bash
# In vsms-hr repository
git submodule add <vsms-common-repo-url> shared/vsms-common
```

Then reference it in `build.gradle`:

```gradle
dependencies {
    implementation project(':shared:vsms-common')
}
```

### Setting Up vsms-hr Module Independently

To set up a new `vsms-hr` module that leverages `vsms-common`:

1. **Create the module structure**:
   ```bash
   mkdir -p services/vsms-hr/src/main/java/com/vsms/hr
   mkdir -p services/vsms-hr/src/main/resources
   ```

2. **Create `build.gradle`**:
   ```gradle
   plugins {
       id 'java'
       id 'org.springframework.boot' version '3.4.3'
       id 'io.spring.dependency-management' version '1.1.7'
   }

   group = 'com.vsms'
   version = '0.0.1-SNAPSHOT'
   java { toolchain { languageVersion = JavaLanguageVersion.of(21) } }

   repositories { mavenCentral() }

   dependencies {
       // Use vsms-common for shared functionality
       implementation project(':shared:vsms-common')
       
       // HR-specific dependencies
       implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
       implementation 'org.springframework.boot:spring-boot-starter-actuator'
       implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'
       runtimeOnly 'com.mysql:mysql-connector-j'
       runtimeOnly 'org.flywaydb:flyway-core'
       testImplementation 'org.springframework.boot:spring-boot-starter-test'
   }
   ```

3. **Create `settings.gradle`**:
   ```gradle
   rootProject.name = 'vsms-hr'
   ```

4. **Use vsms-common components**:
   ```java
   package com.vsms.hr;
   
   import com.vsms.common.response.ApiResponse;
   import com.vsms.common.exception.ResourceNotFoundException;
   
   @RestController
   public class HrController {
       @GetMapping("/employees/{id}")
       public ResponseEntity<ApiResponse<Employee>> getEmployee(@PathVariable Long id) {
           // Use ApiResponse from vsms-common
           return ResponseEntity.ok(ApiResponse.success(employeeService.findById(id)));
       }
   }
   ```

### Recommended Approach for Your Team

For a team working independently on `vsms-hr`:

**If working within the same repository**:
- Use **Option 1** (Multi-Module Gradle Setup)
- Add `vsms-hr` to root `settings.gradle`
- Reference `vsms-common` as a project dependency

**If working in a separate repository**:
- Use **Option 2** (Publish vsms-common as Maven Artifact)
- Publish `vsms-common` to a Maven repository
- Reference it as a standard Maven dependency

### Current Status

**Note**: The current architecture does NOT support independent module setup out-of-the-box. Services are currently duplicating dependencies instead of using `vsms-common`. To enable independent development:

1. Implement one of the options above
2. Update existing services to use `vsms-common` instead of duplicating dependencies
3. Establish a process for updating `vsms-common` across all services

## Notes

- This library is designed to be included as a dependency in all Java microservices
- The security configuration is identical across all services except `auth-service`
- The API response format should be replicated in Node.js and Go services for consistency
- All components are marked as `AUTO-GENERATED` to indicate they are shared across services
