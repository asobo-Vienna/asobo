# Asobo

A full-stack application for bringing people together built with Angular and Spring Boot.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)

## Prerequisites

Before you begin, ensure you have the following installed:

- **Node.js** (v18 or higher) - [Download](https://nodejs.org/)
- **npm** (comes with Node.js)
- **Java JDK 21** - [Download](https://www.oracle.com/java/technologies/downloads/#java21)
- **Maven** (v3.6 or higher) - [Download](https://maven.apache.org/download.cgi)
- **Git** - [Download](https://git-scm.com/downloads)
- **NeonDB Account** - [Sign up](https://neon.tech/) (for PostgreSQL database)

## Tech Stack

### Frontend
- **Angular 20**
- **PrimeNG** - UI Component Library
- **Bootstrap 5** - CSS Framework
- **Angular Material** - Material Design Components
- **RxJS** - Reactive Programming
- **JWT Decode** - Token Management

### Backend
- **Spring Boot 3.4.5**
- **Spring Security** - Authentication & Authorization
- **JWT (JSON Web Tokens)** - Token-based Authentication
- **Spring Data JPA** - Database Access
- **PostgreSQL** - Database (via NeonDB)
- **MapStruct** - Object Mapping
- **SpringDoc OpenAPI** - API Documentation

## Project Structure
```
asobo/
├── frontend-angular/          # Angular application
│   ├── src/
│   └── package.json
│
└── backend/           # Spring Boot application
    ├── src/
    │   ├── main/
    │   │   ├── java/at/msm/asobo/
    └── pom.xml
```

## Installation

### 1. Clone the Repository
```bash
git clone https://github.com/asobo-Vienna/asobo.git
cd asobo
```

### 2. Frontend Setup
```bash
cd frontend-angular
npm install
```

### 3. Backend Setup
```bash
cd backend
mvn clean install
```

## Configuration

### Database Configuration (NeonDB)

1. **Create a NeonDB Project:**
   - Sign up at [neon.tech](https://neon.tech/)
   - Create a new project
   - Copy your PostgreSQL connection string

2. **Configure Backend:**

Create or update `backend/src/main/resources/application.properties`:
```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://[your-neondb-host]/[database-name]?sslmode=require
spring.datasource.username=[your-username]
spring.datasource.password=[your-password]
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true

# JWT Configuration
jwt.secret=your-secret-key-here-make-it-long-and-secure
jwt.expiration=86400000

# Server Configuration
server.port=8080

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

**Important:** Replace the placeholders with your actual NeonDB credentials.

### Frontend Configuration

Create or update `frontend/src/environments/environment.ts`:
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  authEndpoint: 'http://localhost:8080/api/auth',
  eventsEndpoint: 'http://localhost:8080/api/events',
  usersEndpoint: 'http://localhost:8080/api/users'
};
```

## Running the Application

### Start Backend Server
```bash
cd backend
mvn spring-boot:run
```

The backend will start on **http://localhost:8080**

### Start Frontend Development Server

Open a new terminal:
```bash
cd frontend
npm start
```

The frontend will start on **http://localhost:4200**

### Access the Application

Open your browser and navigate to:
- **Frontend:** http://localhost:4200
- **Backend API:** http://localhost:8080
- **API Documentation:** http://localhost:8080/swagger-ui.html


## Default Credentials

After first run, you may need to create an initial user through the registration endpoint or configure default users in your backend.


## Authors

**Michael Schwarzinger**
- GitHub: [@mikeonefive](https://github.com/mikeonefive)

**Simon Wolfsteiner**
- GitHub: [@seimes](https://github.com/seimes)

## Troubleshooting

### Common Issues

**Database connection issues:**
- Verify your NeonDB credentials in `application.properties`
- Check if the database connection string includes `?sslmode=require`

**Maven build failures:**
- Ensure Java 21 is installed: `java -version`
- Clear Maven cache: `mvn clean`

**NPM install failures:**
- Clear npm cache: `npm cache clean --force`
- Delete `node_modules` and `package-lock.json`, then run `npm install` again
