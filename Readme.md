# CommuteIQ

## Intelligent Corporate Transport Optimization Platform

CommuteIQ is a backend system designed to manage and optimize corporate employee transportation operations.
The platform automates ride planning, pooling, vehicle allocation, safety monitoring, and cost analytics to improve operational efficiency and employee commute experience.

This project simulates a real-world enterprise mobility platform similar to modern corporate transport management SaaS systems.

---

## Core Capabilities

### Ride Management

* Employee ride requests
* Automatic ride planning
* Vehicle and driver assignment
* Daily schedule generation

### Smart Pooling Engine

* Groups employees based on location proximity
* Overlapping pickup windows
* Vehicle capacity constraints
* Gender safety constraints

### Safety Monitoring

* Panic events
* Route deviation detection
* Unsafe drop identification
* Real-time ride tracking

### Real-Time Tracking

* Driver location updates via WebSocket
* ETA recalculation
* Live ride monitoring

### Cost Optimization

* Fuel cost calculation
* Driver operational cost
* Idle cost estimation
* Daily transport cost reporting

### Analytics

* Occupancy percentage
* Safety violations
* Ride statistics
* Daily cost summary

---

## Architecture

Layered architecture following SOLID principles.

```
Controller → Service → Repository → Database
           ↘ Mapper ↘ DTO ↘ Exception ↘ Security
```

Packages:

* controller – REST APIs
* service – business logic
* repository – database access
* entity – database models
* dto – request and response objects
* mapper – entity to DTO mapping
* security – authentication and authorization
* scheduler – automated background jobs
* websocket – real-time communication
* exception – global error handling
* config – application configuration

---

## Technology Stack

* Java 17
* Spring Boot 3
* Spring Security (JWT)
* Spring Data JPA + Hibernate
* PostgreSQL
* MapStruct
* WebSocket
* Spring Scheduling
* Spring Cache
* Spring AOP
* Maven

---

## Database Setup

Create database:

```
CREATE DATABASE commuteiq;
```

Update `application.yml` with credentials:

```
spring.datasource.url=jdbc:postgresql://localhost:5432/commuteiq
spring.datasource.username=postgres
spring.datasource.password=postgres
```

---

## Running the Application

Build project:

```
mvn clean install
```

Run:

```
mvn spring-boot:run
```

Server starts at:

```
http://localhost:8080
```

---

## Authentication

Roles:

* ADMIN
* EMPLOYEE
* DRIVER

Endpoints:

```
POST /auth/register
POST /auth/login
```

All secured endpoints require JWT token in header:

```
Authorization: Bearer <token>
```

---

## Scheduler

Automatic ride planning runs daily:

```
22:00 – Generates next day ride plans from pending requests
```

---

## WebSocket

Endpoint:

```
ws://localhost:8080/ws/location
```

Used by drivers to send location updates for real-time monitoring.

---

## Analytics APIs

```
GET /analytics/occupancy
GET /analytics/safety-count
GET /analytics/daily-cost
```

---

## Example Workflow

1. Employee registers
2. Employee creates ride request
3. Scheduler triggers planning
4. System assigns vehicle and driver
5. Driver sends live location
6. Safety events recorded
7. Cost calculated and analytics generated

---

## Future Improvements

* External maps integration
* Machine learning based demand prediction
* Multi-office routing
* Vendor billing module
* Admin dashboard UI

---

## License

This project is developed for educational and demonstration purposes.
