# CommuteIQ — Corporate Employee Transportation Optimization Platform

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.2-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" />
  <img src="https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react&logoColor=black" />
  <img src="https://img.shields.io/badge/Tailwind%20CSS-4-06B6D4?style=for-the-badge&logo=tailwindcss&logoColor=white" />
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white" />
  <img src="https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" />
</p>

---

## Overview

CommuteIQ is a full-stack enterprise platform designed to optimize corporate employee transportation. Large organizations often face inefficiency in daily commute management — underutilized vehicles, poor route planning, rising fuel costs, and fragmented safety oversight. CommuteIQ addresses these challenges through an intelligent ride pooling engine, real-time vehicle tracking, automated safety monitoring, and detailed cost analytics.

The platform enables transportation administrators to manage employee commute data, process ride requests, generate optimized daily ride plans, monitor vehicle movements in real time, and track operational costs — all through a unified web interface backed by a secure REST API.

---

## Table of Contents

- [How It Works](#how-it-works)
- [System Architecture](#system-architecture)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Database Design](#database-design)
- [API Reference](#api-reference)
- [Smart Pooling Algorithm](#smart-pooling-algorithm)
- [Security Model](#security-model)
- [Getting Started](#getting-started)
- [License](#license)

---

## How It Works

CommuteIQ operates on a daily cycle that mirrors real-world corporate transportation workflows:

### 1. Employee Registration and Preference Capture

Employees are registered in the system with their home geolocation (latitude/longitude), office location, department, and gender. Each employee can configure a **Commute Preference** that specifies their preferred pickup time window (earliest and latest pickup times), maximum acceptable walk distance to a pickup point, whether they require same-gender pooling, and which days they work from home. These preferences directly influence how the pooling engine groups employees.

### 2. Ride Request Submission

Before each working day, employees (or administrators on their behalf) submit **Ride Requests** for a specific date. A ride request signals that the employee needs transportation on that day. Requests start in a `PENDING` state and can be cancelled before plan generation. Only pending requests are considered during the planning phase.

### 3. Automated Ride Plan Generation

The core of the platform is the **Smart Pooling Engine**. This can be triggered manually by an administrator or run automatically via a scheduled cron job (configured to execute daily at 10 PM). The engine performs the following steps:

- Retrieves all pending ride requests for the target date
- Loads each requesting employee's location and commute preferences
- Clusters employees by geographic proximity using a configurable distance threshold (default: 2 km radius)
- Within each cluster, validates time window overlap and preference compatibility (gender preferences, walk distance limits)
- Assigns available vehicles based on capacity — ensuring no vehicle is assigned more passengers than its seat count
- Pairs each plan with an available driver
- Orders pickup stops within each plan to minimize total travel distance
- Calculates estimated route distance and duration
- Generates a **Cost Record** for each plan, breaking down fuel cost, driver cost, and idle cost into a total expense

The output is a set of `RidePlan` entities, each linked to a vehicle, a driver, and an ordered list of employee stops. All associated ride requests are updated from `PENDING` to `PLANNED`.

### 4. Real-Time Tracking and Safety Monitoring

Once ride plans transition to `IN_PROGRESS`, drivers transmit their GPS coordinates via **WebSocket connections**. The platform processes these location updates in real time and performs continuous safety checks:

- **Route Deviation Detection**: Compares the driver's current position against the planned route. If the deviation exceeds a configurable threshold (default: 500 meters), a safety event is automatically generated.
- **Overspeed Detection**: Calculates instantaneous speed from consecutive location updates. If speed exceeds safe limits, an alert is raised.
- **Panic Alerts**: Drivers or employees can manually trigger panic events through the API.
- **Unsafe Drop-off Detection**: Flags irregular drop-off patterns such as stops in unsafe or unplanned locations.

All safety events are recorded with timestamps, event type, and description, and are surfaced in the Safety Events dashboard.

### 5. Status Management and Completion

Ride plans progress through a lifecycle: `SCHEDULED` → `IN_PROGRESS` → `COMPLETED` (or `CANCELLED`). Administrators update status through the dashboard as rides begin and conclude. Upon completion, the final cost record is finalized and included in analytics.

### 6. Analytics and Reporting

The analytics dashboard aggregates operational data to provide administrators with actionable insights:

- **Total active employees and pending requests** for planning visibility
- **Vehicle occupancy rates** to measure fleet utilization efficiency
- **Cost breakdowns** (fuel, driver, idle) over configurable time ranges
- **Safety event frequency** by type for risk assessment
- **Ride completion rates** to monitor service reliability

Analytics responses are cached using Spring Cache to minimize database load on frequently accessed metrics.

---

## System Architecture

```
+---------------------------------------------------------------+
|                     React Frontend (Port 5173)                 |
|              Vite + React 19 + Tailwind CSS v4                 |
|        Login | Dashboard | Employees | Rides | Safety          |
+-------------------------------+-------------------------------+
                                |
                          REST API / WebSocket
                                |
+-------------------------------+-------------------------------+
|                  Spring Boot Backend (Port 8081)               |
|                                                                |
|   +------------------+   +------------------+                  |
|   | Security Layer   |   | Controller Layer |                  |
|   | JWT Auth Filter  |   | REST Endpoints   |                  |
|   | Role-Based ACL   |   | Validation       |                  |
|   +------------------+   +------------------+                  |
|                                                                |
|   +------------------+   +------------------+                  |
|   | Service Layer    |   | Pooling Engine   |                  |
|   | Business Logic   |   | Proximity Cluster|                  |
|   | DTO Mapping      |   | Route Optimizer  |                  |
|   +------------------+   +------------------+                  |
|                                                                |
|   +------------------+   +------------------+                  |
|   | WebSocket Layer  |   | Scheduler Layer  |                  |
|   | Location Handler |   | Daily Cron Job   |                  |
|   | Safety Detector  |   | Plan Generation  |                  |
|   +------------------+   +------------------+                  |
|                                                                |
|   +------------------+   +------------------+                  |
|   | Repository Layer |   | Cache Layer      |                  |
|   | JPA + Hibernate  |   | Analytics Cache  |                  |
|   +------------------+   +------------------+                  |
+-------------------------------+-------------------------------+
                                |
                          JDBC / HikariCP
                                |
+-------------------------------+-------------------------------+
|                       MySQL 8.0 Database                       |
|                  10 Tables, Indexed Queries                    |
+---------------------------------------------------------------+
```

---

## Technology Stack

### Backend

| Component | Technology | Role |
|-----------|-----------|------|
| Framework | Spring Boot 3.4.2 | Application container, dependency injection, auto-configuration |
| Security | Spring Security + JJWT 0.12.6 | Stateless JWT authentication, role-based authorization filters |
| Persistence | Spring Data JPA + Hibernate 6.6 | ORM mapping, repository abstraction, auto-DDL schema management |
| Connection Pool | HikariCP | High-performance JDBC connection pooling |
| Real-Time | Spring WebSocket | Bi-directional driver location streaming |
| Scheduling | Spring Scheduler | Cron-based automated ride plan generation |
| Caching | Spring Cache (Simple) | In-memory caching for analytics endpoints |
| Mapping | MapStruct 1.5.5 | Compile-time DTO-to-entity mapping code generation |
| Boilerplate | Lombok 1.18 | Annotation-based getter/setter/builder generation |
| Validation | Hibernate Validator | Bean validation on request DTOs |
| Database | MySQL 8.0 | Relational data store |

### Frontend

| Component | Technology | Role |
|-----------|-----------|------|
| Framework | React 19 | Component-based UI rendering |
| Build Tool | Vite 7 | Fast development server with hot module replacement |
| Styling | Tailwind CSS v4 | Utility-first CSS with custom dark theme tokens |
| Routing | React Router v7 | Client-side navigation with auth guards |
| HTTP | Fetch API | Native browser API for REST communication |

---

## Project Structure

```
CommuteIQ/
├── commuteiq/                               # Backend (Spring Boot)
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/commuteiq/
│       │   ├── commuteiq/
│       │   │   └── CommuteiqApplication.java       # Entry point
│       │   └── platform/
│       │       ├── config/                          # Security, WebSocket, CORS configuration
│       │       ├── controller/                      # REST controllers (Auth, Employee, Ride, Safety, Analytics)
│       │       ├── dto/
│       │       │   ├── request/                     # Inbound DTOs with Jakarta validation
│       │       │   └── response/                    # Outbound DTOs including generic ApiResponse
│       │       ├── entity/                          # JPA entities, enums, and Auditable base class
│       │       ├── exception/                       # ResourceNotFoundException, InvalidOperationException, GlobalExceptionHandler
│       │       ├── mapper/                          # MapStruct interfaces for DTO-entity conversion
│       │       ├── repository/                      # Spring Data JPA repositories with custom JPQL queries
│       │       ├── scheduler/                       # RidePlanScheduler for daily plan generation
│       │       ├── security/                        # JwtTokenProvider, JwtAuthenticationFilter, CustomUserDetailsService
│       │       └── service/
│       │           ├── impl/                        # Service implementations including RidePlanningServiceImpl
│       │           └── *Service.java                # Service interfaces
│       └── resources/
│           └── application.yml                      # Database, JWT, pooling, and server configuration
│
├── commuteiq-frontend/                      # Frontend (React)
│   ├── package.json
│   ├── vite.config.js                               # Vite + Tailwind CSS plugin
│   └── src/
│       ├── api/apiService.js                        # Centralized HTTP client with JWT token injection
│       ├── context/AuthContext.jsx                   # Authentication state management
│       ├── components/Sidebar.jsx                    # Navigation sidebar with role display
│       ├── pages/
│       │   ├── LoginPage.jsx                        # Login and registration with role selection
│       │   ├── DashboardPage.jsx                    # Analytics overview with stat cards
│       │   ├── EmployeesPage.jsx                    # Employee CRUD with modal forms
│       │   ├── RideRequestsPage.jsx                 # Request management with status tracking
│       │   ├── RidePlansPage.jsx                    # Plan viewing, generation, and status updates
│       │   └── SafetyPage.jsx                       # Safety event monitoring and reporting
│       ├── App.jsx                                  # Router configuration with protected routes
│       ├── main.jsx                                 # Application entry point
│       └── index.css                                # Tailwind import and custom theme variables
│
└── README.md
```

---

## Database Design

The application manages 10 interconnected entities, all extending an `Auditable` base class that automatically tracks creation and modification timestamps.

```
Users ────┬──── Employees ──── CommutePreferences
          │         │
          │         ├──── RideRequests
          │         │
          │         └──── RidePlanEmployees ────┐
          │                                     │
          └──── Drivers ──── RidePlans ─────────┤
                                │               │
                                ├── SafetyEvents│
                                └── CostRecords │
                                                │
                          Vehicles ─────────────┘
```

**Entity Descriptions:**

| Entity | Description |
|--------|-------------|
| `User` | Authentication account storing username, hashed password, email, role (`ADMIN`, `EMPLOYEE`, `DRIVER`), and active status. Indexed on username and email. |
| `Employee` | Employee profile linked one-to-one with a User. Stores full name, gender, department, home coordinates (latitude/longitude), office location, and active flag for soft deletes. |
| `CommutePreference` | Per-employee commute configuration: earliest/latest pickup times, maximum walk distance in meters, same-gender requirement flag, and work-from-home day schedule. |
| `RideRequest` | A dated request from an employee for transportation. Status transitions: `PENDING` → `PLANNED` → `COMPLETED` or `CANCELLED`. Indexed on request date and status. |
| `Vehicle` | Fleet vehicle record with unique plate number, passenger capacity, vendor name, and active status. |
| `Driver` | Driver profile with name, unique license number, cumulative rating, active status, and link to a User account for authentication. |
| `RidePlan` | An optimized daily ride itinerary linking one vehicle, one driver, a date, estimated distance and duration, and status (`SCHEDULED`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`). |
| `RidePlanEmployee` | Join entity mapping employees to ride plans. Includes a `stopOrder` field that defines the pickup sequence for route optimization. |
| `SafetyEvent` | A recorded safety incident linked to a ride plan. Types include `PANIC`, `ROUTE_DEVIATION`, `OVERSPEED`, and `UNSAFE_DROP`. Stores event timestamp and textual description. |
| `CostRecord` | Per-ride financial breakdown with fuel cost, driver cost, idle cost, and computed total cost. Uses `BigDecimal` with two-decimal precision for financial accuracy. |

---

## API Reference

All endpoints are prefixed with `/api` and require a valid JWT token in the `Authorization: Bearer <token>` header, except for authentication endpoints.

### Authentication

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register a new user account with username, password, email, and role |
| POST | `/api/auth/login` | Authenticate and receive a JWT token with 24-hour expiration |

### Employee Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/employees?page=0&size=20` | List employees with pagination |
| POST | `/api/employees` | Create a new employee record |
| GET | `/api/employees/{id}` | Retrieve an employee by ID |
| PUT | `/api/employees/{id}` | Update employee details |
| DELETE | `/api/employees/{id}` | Soft-delete (deactivate) an employee |

### Ride Request Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/ride-requests?page=0&size=20` | List all ride requests with pagination |
| POST | `/api/ride-requests` | Submit a new ride request for a future date |
| PUT | `/api/ride-requests/{id}/cancel` | Cancel a pending ride request |

### Ride Plan Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/ride-plans?page=0&size=20` | List all ride plans with pagination |
| GET | `/api/ride-plans/{id}` | Retrieve plan details including assigned employees and stop order |
| POST | `/api/ride-plans/generate?date=YYYY-MM-DD` | Trigger the pooling engine to generate optimized plans for a date |
| PUT | `/api/ride-plans/{id}/status?status=IN_PROGRESS` | Update plan lifecycle status |

### Safety Event Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/safety/events?page=0&size=20` | List safety events with pagination |
| POST | `/api/safety/events` | Report a new safety incident |

### Analytics

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/analytics/dashboard` | Retrieve aggregated KPIs: employee count, active plans, costs, occupancy rate, safety event count |

---

## Smart Pooling Algorithm

The ride planning engine in `RidePlanningServiceImpl` implements a proximity-based clustering algorithm to generate optimized ride plans. The process works as follows:

**Step 1 — Request Collection.** The engine queries all ride requests with status `PENDING` for the target date and loads the corresponding employee profiles with their home coordinates and commute preferences.

**Step 2 — Proximity Clustering.** Employees are grouped into clusters using the Haversine formula to calculate great-circle distances between home locations. Two employees are considered eligible for the same vehicle if the distance between their homes is within the configured threshold (defined in `application.yml` as `app.pooling.distance-threshold-km`, default 2.0 km). The algorithm iterates through unassigned employees and builds clusters greedily — each new employee is added to the nearest compatible cluster.

**Step 3 — Preference Validation.** Within each cluster, the engine validates commute preference compatibility. If an employee has enabled the `sameGenderRequired` flag, they are only grouped with employees of the same gender. Time window overlap is checked to ensure all grouped employees have compatible pickup time ranges.

**Step 4 — Vehicle Assignment.** Available vehicles are queried from the fleet. Each cluster is matched to a vehicle whose capacity is sufficient for the group size. If a cluster exceeds the largest available vehicle's capacity, it is split into sub-groups.

**Step 5 — Stop Ordering.** Within each assigned group, employees are ordered by proximity to minimize total route distance. This produces the `stopOrder` field on each `RidePlanEmployee` record, defining the exact pickup sequence.

**Step 6 — Cost Estimation.** For each generated plan, the engine calculates estimated fuel cost (based on distance), driver cost (fixed rate), and idle cost, producing a `CostRecord` attached to the plan.

**Step 7 — Status Update.** All ride requests included in generated plans are updated from `PENDING` to `PLANNED`, and the ride plans are persisted with status `SCHEDULED`.

---

## Security Model

The application implements a layered security architecture:

**Authentication.** Users authenticate via the `/api/auth/login` endpoint with username and password. The `JwtTokenProvider` generates a signed JWT token using HMAC-SHA256 with a configurable secret key. Tokens expire after 24 hours (configurable via `app.jwt.expiration`).

**Request Filtering.** Every incoming HTTP request passes through the `JwtAuthenticationFilter`, which extracts the token from the `Authorization` header, validates its signature and expiration, and sets the authenticated user in the Spring Security context. Invalid or expired tokens result in a 401 response.

**Role-Based Authorization.** The `SecurityConfig` configures endpoint-level access rules. Admin-only endpoints (employee management, plan generation, analytics) require the `ADMIN` role. Employee and driver endpoints are restricted to their respective roles. Authentication endpoints are publicly accessible.

**Password Storage.** User passwords are hashed using BCrypt before persistence. Raw passwords are never stored or logged.

**CORS.** Cross-origin requests from the frontend origin are explicitly permitted in the security configuration to support the separated frontend/backend deployment model.

---

## Getting Started

### Prerequisites

- Java 17 or later (JDK)
- Node.js 18 or later with npm
- MySQL 8.0 running on localhost:3306
- Maven 3.8 or later

### Step 1: Create the Database

```sql
CREATE DATABASE commuteiq;
```

### Step 2: Configure the Backend

Open `commuteiq/src/main/resources/application.yml` and update the database credentials if they differ from the defaults:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/commuteiq?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root
    password: your_password_here
```

### Step 3: Build and Run the Backend

```bash
cd commuteiq
mvn clean compile
mvn spring-boot:run
```

The backend starts on **http://localhost:8081**. Hibernate will automatically create all database tables on first run.

### Step 4: Install and Run the Frontend

```bash
cd commuteiq-frontend
npm install
npm run dev
```

The frontend starts on **http://localhost:5173** with hot module replacement enabled.

### Step 5: Access the Application

1. Open **http://localhost:5173** in a browser
2. Register a new admin account
3. Log in to access the dashboard and begin managing employees, ride requests, and plans

---

## License

This project is developed for educational and enterprise demonstration purposes.

---

<p align="center">
  Built by the CommuteIQ Team
</p>
