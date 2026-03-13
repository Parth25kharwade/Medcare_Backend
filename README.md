# 🏥 AI-Driven Healthcare Intelligence Platform
**Hackathon Backend — Spring Boot 3 + Java 17 + MySQL + JWT**

---

## 🚀 Quick Start

### Prerequisites
| Tool | Version |
|------|---------|
| Java | 17+ |
| Maven | 3.8+ |
| MySQL | 8.0+ |
| Postman | Any recent version |

---

## 🗄️ Step 1 — Database Setup

```bash
# Log into MySQL
mysql -u root -p

# Run the full schema
source /path/to/medcare/src/main/resources/schema.sql

# Verify tables
USE medcare_db;
SHOW TABLES;
```

Expected tables:
```
roles, users, hospitals, patients, symptoms, vitals,
lab_reports, medical_records, diagnostic_analyses,
icu_beds, staff, equipment, resource_forecasts
```

---

## ⚙️ Step 2 — Configure application.yml

Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/medcare_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root
    password: YOUR_MYSQL_PASSWORD   # ← change this
```

---

## 🔧 Step 3 — Build & Run

```bash
# Clone / navigate to project root
cd medcare

# Build (skip tests for fast start)
mvn clean package -DskipTests

# Run the application
mvn spring-boot:run

# Or run the JAR directly
java -jar target/medcare-platform-1.0.0.jar
```

Server starts at: **http://localhost:8080**

---

## 🧪 Step 4 — Run Tests

```bash
mvn test
```

Test coverage includes:
- `DiagnosticRuleEngineTest` — 5 unit tests for AI rules
- `ResourceStressEngineTest` — 4 unit tests for stress prediction

---

## 📮 Step 5 — Import Postman Collection

1. Open Postman
2. Click **Import** → select `MedCare_API_Collection.postman_collection.json`
3. Collection variables are auto-set on login

### Recommended Test Flow:

```
1. POST /api/auth/register  → Register a Doctor
2. POST /api/auth/register  → Register an Admin
3. POST /api/auth/login     → Login Doctor (token auto-saved)
4. POST /api/auth/login     → Login Admin  (token auto-saved)
5. POST /api/patients       → Create patient
6. POST /api/diagnosis/analyze  → Run AI diagnostic
7. POST /api/resources/predict/1  → Run resource forecast (Admin)
8. GET  /api/dashboard/doctor     → Doctor dashboard
9. GET  /api/dashboard/admin/1    → Admin dashboard
```

---

## 🏗️ Project Structure

```
src/main/java/com/medcare/
├── MedcareApplication.java       # Entry point
├── controller/
│   ├── AuthController.java       # POST /api/auth/**
│   ├── PatientController.java    # CRUD + file upload
│   ├── DiagnosticController.java # AI analysis endpoint
│   ├── ResourceController.java   # ICU, staff, forecast
│   └── DashboardController.java  # Doctor & Admin views
├── service/
│   ├── AuthService.java
│   ├── PatientService.java
│   ├── DiagnosticService.java
│   ├── ResourceService.java
│   └── MedicalRecordService.java
├── repository/                   # Spring Data JPA interfaces
├── entity/                       # JPA entities (12 entities)
├── dto/                          # Request/Response DTOs
├── ai/
│   ├── DiagnosticRuleEngine.java # Clinical rule-based AI
│   └── ResourceStressEngine.java # Resource stress predictor
├── security/
│   ├── JwtUtils.java             # Token generation & validation
│   ├── JwtAuthFilter.java        # Request filter
│   ├── UserDetailsImpl.java      # Spring Security user
│   └── UserDetailsServiceImpl.java
├── config/
│   └── SecurityConfig.java       # Security rules & CORS
└── exception/
    ├── GlobalExceptionHandler.java
    ├── ResourceNotFoundException.java
    └── BadRequestException.java
```

---

## 🔐 Security

| Endpoint Pattern | Allowed Roles |
|-----------------|---------------|
| `POST /api/auth/**` | Public |
| `GET/POST /api/patients/**` | DOCTOR, ADMIN |
| `POST /api/diagnosis/**` | DOCTOR, ADMIN |
| `ALL /api/resources/**` | ADMIN only |
| `GET /api/dashboard/doctor` | DOCTOR only |
| `GET /api/dashboard/admin/**` | ADMIN only |

---

## 🧠 AI Rule Engine — Diagnostic Logic

| Rule | Trigger | Condition |
|------|---------|-----------|
| Dengue | HIGH confidence | Fever >38.5°C + Platelet <150k + symptoms |
| Cardiac | CRITICAL | Systolic BP ≥140 + chest pain |
| Respiratory | CRITICAL | O2 Sat <94% + respiratory symptoms |
| Diabetes | HIGH | Blood sugar >200 + diabetic symptoms |
| Liver | MEDIUM | Bilirubin >2.0 + elevated AST/ALT |
| Kidney | MEDIUM | Creatinine >1.2 |

**Risk Score** → 0–100 (capped): LOW / MEDIUM / HIGH / CRITICAL

---

## 📊 Resource Stress Engine

| Metric | Weight | Threshold |
|--------|--------|-----------|
| ICU Utilization | 40% | ≥90% = CRITICAL |
| Staff Workload | 35% | ≥80% = HIGH |
| Equipment Demand | 25% | ≥75% = HIGH |

Overall score ≥80 → CRITICAL | ≥60 → HIGH | ≥40 → ELEVATED | else NORMAL

---

## 📋 Key API Endpoints

```
POST   /api/auth/register
POST   /api/auth/login

POST   /api/patients
GET    /api/patients/{id}
PUT    /api/patients/{id}
GET    /api/patients/doctor/{doctorId}
GET    /api/patients/{patientId}/history
POST   /api/patients/records
POST   /api/patients/records/{id}/upload

POST   /api/diagnosis/analyze          ← Core AI endpoint
GET    /api/diagnosis/patient/{id}/history
GET    /api/diagnosis/alerts/doctor

PUT    /api/resources/icu
GET    /api/resources/icu/{hospitalId}
PUT    /api/resources/staff
POST   /api/resources/predict/{hospitalId}
GET    /api/resources/forecast/{hospitalId}

GET    /api/dashboard/doctor
GET    /api/dashboard/admin/{hospitalId}
```

---

## 🩺 Sample Diagnosis Request

```json
POST /api/diagnosis/analyze
Authorization: Bearer <doctor_token>

{
  "patientId": 1,
  "symptoms": ["fever", "headache", "rash"],
  "vitals": {
    "temperature": 39.8,
    "systolicBp": 100,
    "oxygenSaturation": 96.5
  },
  "labReport": {
    "plateletCount": 85000,
    "hemoglobin": 11.5,
    "reportDate": "2024-01-15"
  }
}
```

**Response:**
```json
{
  "success": true,
  "message": "Diagnostic analysis complete",
  "data": {
    "riskScore": 35.0,
    "riskLevel": "HIGH",
    "suspectedConditions": ["Dengue Fever (High Confidence)"],
    "diagnosticAlerts": ["⚠️ High fever with low platelet count — Dengue suspected"],
    "recommendations": ["Immediate platelet transfusion evaluation; NS1 antigen test"]
  }
}
```

---

*Built with ❤️ for the Hackathon — Medcare Platform v1.0.0*
