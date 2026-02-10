# Asobo

A full-stack application for bringing people together built with Angular and Spring Boot.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)

## Prerequisites

Before you begin, ensure you have the following installed:

- **Node.js** (v18 or higher) - [Download](https://nodejs.org/)
- **npm** (comes with Node.js)
- **Java JDK 21** - [Download](https://www.oracle.com/java/technologies/downloads/#java21)
- **Maven** (v3.6 or higher) - [Download](https://maven.apache.org/download.cgi)
- **Git** - [Download](https://git-scm.com/downloads)
- **Docker & Docker Compose** – [Download](https://www.docker.com/get-started)

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
- **PostgreSQL** - Database (via Docker)
- **MapStruct** - Object Mapping

## Project Structure
```
asobo/
├── frontend-angular/          # Angular application
│   ├── src/
│   └── package.json
│
├── backend/                   # Spring Boot application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/at/msm/asobo/
│   └── pom.xml
│
├── run.sh                     # Linux/Mac startup script
├── run.bat                    # Windows startup script
└── docker-compose.yml         # Database configuration
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

### Database Configuration (Docker Compose)

1. **Start the Database:**

⚠️ **Attention:** Make sure Docker Desktop is running before executing any Docker commands, such as `docker-compose up -d`.

From the root directory of the project, run:
```bash
docker-compose up -d
```

This will start a PostgreSQL database container with the following credentials:
- **Username:** `eww`
- **Password:** `eww`
- **Database:** `asoboapp`
- **Port:** `5432`

2. **Populate Initial Data:**

   Once the database is running, the Spring application will automatically populate it with initial data from `data.sql` during startup (if configured in your application properties).

3. **Configure Backend:**

   Create or update `backend/src/main/resources/application.properties`:
   ```properties
   # Database Configuration
   spring.datasource.url=jdbc:postgresql://localhost:5432/asoboapp
   spring.datasource.username=eww
   spring.datasource.password=eww
   spring.datasource.driver-class-name=org.postgresql.Driver

   # JPA Configuration
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   spring.jpa.defer-datasource-initialization=true
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
   spring.jpa.properties.hibernate.format_sql=true

   # JWT Configuration
   jwt.secret = ${JWT_SECRET}
   jwt.expiration-ms=how-long-until-token-expires-if-remember-me-was-not-checked
   jwt.remember-me-expiration-ms=how-long-the-remember-me-session-should-last

   # Server Configuration
   server.port=8080

   # File Upload Configuration
   spring.servlet.multipart.max-file-size=10MB
   spring.servlet.multipart.max-request-size=10MB
   
   # Initialize database with data.sql
   spring.sql.init.mode=always
   spring.sql.init.data-locations=classpath:data.sql
   ```

Either set the environment variable `JWT_SECRET` to a secret generated with, e.g.,

`openssl rand -base64 64`

or replace

`jwt.secret = ${JWT_SECRET}` by `jwt.secret=your-secret-key-here-make-it-long-and-secure`

in `application.properties`.

4. **Stop the Database:**

When you're done, stop the container with:
```bash
docker-compose down
```

To also remove the database volume and start fresh next time:
```bash
docker-compose down -v
```

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

### Option 1: Using Run Scripts (Recommended)

⚠️ **Attention:** Before running the project, download the latest `.jar` built from the `master` branch.  
Go to **GitHub → Actions**, open the most recent workflow run with a green checkmark, and download the `asobo-build` artifact.  
Place the `.jar` file in the project root directory.

⚠️ **Attention:** Make sure Docker Desktop is running before running any of the scripts.

The easiest way to run the application is using the provided scripts from the project root directory:

**On Linux/Mac:**
```bash
./run.sh
```

**On Windows:**
```bash
run.bat
```

These scripts will:
1. Ensure the PostgreSQL database is running
2. Start the Spring Boot backend
3. Launch the Angular frontend


### Example User Credentials:

When the application is started using the provided run scripts, an example user is available for local testing.

**Login:**
- **Username:** `batman`
- **Password:** `batman`

These credentials are intended **only** for local development. They are created on startup and must not be used in production.

### Option 2: Manual Execution

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


## Default Credentials

After first run, you may need to create an initial user through the registration endpoint or configure default users in your backend.


## Authors

[@mikeonefive](https://github.com/mikeonefive)

[@seimes](https://github.com/seimes)

## Troubleshooting

### Common Issues

**Maven build failures:**
- Ensure Java 21 is installed: `java -version`
- Clear Maven cache: `mvn clean`

**NPM install failures:**
- Clear npm cache: `npm cache clean --force`
- Delete `node_modules` and `package-lock.json`, then run `npm install` again
