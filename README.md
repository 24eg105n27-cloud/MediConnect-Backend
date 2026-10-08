# MediConnect - Spring Boot & MySQL Backend

A production-ready Spring Boot backend for the **MediConnect** healthcare web application, powered by **Spring Data JPA** and **MySQL Server 8.0**.

---

## 🚀 Features

- **User Authentication & Profiles**:
  - Secure registration and login for patients.
  - Profile management (name, email, phone, age, gender).
- **Doctor Directory**:
  - Doctor search and filtering by specialty.
  - Pre-seeded with top specialist doctors across hospitals (Apollo, Yashoda, KIMS, CARE).
- **Appointment Booking & Management**:
  - Booking with selected doctor, hospital, date, and time.
  - Appointment list retrieval and cancellation support.
- **Medical Records & Prescriptions**:
  - Prescriptions and diagnosis records added by doctors/administrators.
  - Full retrieval for patient medical history.
- **Dashboard Metrics**:
  - Aggregate endpoint providing real-time stats (total appointments, upcoming visits, doctors count, medical records count).
- **Global CORS Configured**:
  - Connects out of the box with the Vite/React frontend on `http://localhost:5173`.

---

## 🛠 Tech Stack

- **Java**: 21+
- **Spring Boot**: 3.3.4
  - `spring-boot-starter-web`
  - `spring-boot-starter-data-jpa`
  - `spring-boot-starter-validation`
- **Database**: MySQL 8.0 (`mediconnect` database)
- **ORM / Persistence**: Hibernate 6 / Spring Data JPA
- **Build Tool**: Apache Maven

---

## 🗄️ MySQL Database Configuration

The backend connects to MySQL using the following properties defined in `src/main/resources/application.properties`:

```properties
server.port=8080

spring.datasource.url=jdbc:mysql://localhost:3306/mediconnect?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false
```

---

## 🏃 Running the Backend

From the project root:

```bash
cd backend
mvn spring-boot:run
```

The server will start at: **`http://localhost:8080`**

---

## 📡 REST API Documentation

### 1. Authentication (`/api/auth`)

#### **Register**
- **Endpoint**: `POST /api/auth/register`
- **Request Body**:
  ```json
  {
    "name": "John Doe",
    "email": "john@example.com",
    "password": "securePassword123"
  }
  ```
- **Response**: `201 Created`
  ```json
  {
    "success": true,
    "message": "Registration successful!",
    "user": {
      "id": 2,
      "name": "John Doe",
      "email": "john@example.com",
      "role": "PATIENT"
    }
  }
  ```

#### **Login**
- **Endpoint**: `POST /api/auth/login`
- **Request Body**:
  ```json
  {
    "email": "siri@example.com",
    "password": "password123"
  }
  ```
- **Response**: `200 OK`
  ```json
  {
    "success": true,
    "message": "Login successful!",
    "user": {
      "id": 1,
      "name": "Siri",
      "email": "siri@example.com",
      "phone": "9876543210",
      "age": 24,
      "gender": "Female",
      "role": "PATIENT"
    }
  }
  ```

---

### 2. User Profile (`/api/users`)

- **Get Profile**: `GET /api/users/profile?email=siri@example.com`
- **Update Profile**: `PUT /api/users/profile`
  ```json
  {
    "name": "Siri",
    "email": "siri@example.com",
    "phone": "9876543210",
    "age": 25,
    "gender": "Female"
  }
  ```

---

### 3. Doctors (`/api/doctors`)

- **Get All Doctors**: `GET /api/doctors`
- **Filter by Specialty**: `GET /api/doctors?specialty=Cardiologist`
- **Get Doctor by ID**: `GET /api/doctors/{id}`
- **Add Doctor**: `POST /api/doctors`
  ```json
  {
    "name": "Dr. Ramesh Patel",
    "specialty": "Orthopedic",
    "hospital": "Sunshine Hospital",
    "experience": "15 years"
  }
  ```

---

### 4. Appointments (`/api/appointments`)

- **Get All Appointments**: `GET /api/appointments`
- **Get Appointments by Email**: `GET /api/appointments?email=siri@example.com`
- **Book Appointment**: `POST /api/appointments`
  ```json
  {
    "doctor": "Dr. Anjali Sharma",
    "specialty": "Cardiologist",
    "hospital": "Apollo Hospital",
    "date": "2026-10-12",
    "time": "10:00 AM",
    "status": "Confirmed",
    "patientName": "Siri",
    "patientEmail": "siri@example.com"
  }
  ```
- **Cancel Appointment**: `PUT /api/appointments/{id}/cancel`
- **Delete Appointment**: `DELETE /api/appointments/{id}`

---

### 5. Medical Records (`/api/medical-records`)

- **Get All Records**: `GET /api/medical-records`
- **Get Records by Patient**: `GET /api/medical-records?patientName=Siri`
- **Add Record (Admin/Doctor)**: `POST /api/medical-records`
  ```json
  {
    "patientName": "Siri",
    "hospital": "Apollo Hospital",
    "doctor": "Dr. Anjali Sharma",
    "disease": "Mild Hypertension",
    "medicines": "Amlodipine 5mg",
    "visitDate": "2026-10-05",
    "notes": "Follow up in two weeks."
  }
  ```
- **Delete Record**: `DELETE /api/medical-records/{id}`

---

### 6. Dashboard Analytics (`/api/dashboard`)

- **Get Summary Metrics**: `GET /api/dashboard/stats`
  - Response:
    ```json
    {
      "totalAppointments": 1,
      "upcomingVisits": 1,
      "totalDoctors": 4,
      "totalRecords": 1
    }
    ```
