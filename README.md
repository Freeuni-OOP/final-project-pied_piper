# LecturBoxd - Letterboxd-style Lecture Review Platform

A full-stack web application for reviewing and logging university lectures, similar to Letterboxd but for academic content.

## Table of Contents

- [Project Overview](#project-overview)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Features & Testing Flow](#features--testing-flow)
- [Troubleshooting](#troubleshooting)

---

## Project Overview

LecturBoxd allows university students to:

- 📚 Browse lecture syllabi organized by Faculty → Semester → Subject → Lecture
- ⭐ Write and read reviews of lectures with star ratings
- 📝 Log attendance/completion of lectures
- 👥 Follow other students to see their activity in a social feed
- 💬 Real-time messaging with other users via WebSocket
- 📊 View profile statistics (reviews, logged lectures, followers)

---

## Tech Stack

### Backend
- **Java 17** with Spring Boot 3.3.5
- **PostgreSQL** (Supabase for cloud deployment)
- **Spring Security** with JWT authentication
- **Spring WebSocket** + STOMP for real-time messaging
- **Flyway** for database migrations
- **Maven** for dependency management

### Frontend
- **React 18+** with TypeScript
- **Vite** for fast dev server and builds
- **React Router** for navigation
- **Axios** for REST API calls
- **@stomp/stompjs** + **sockjs-client** for WebSocket messaging
- **Tailwind CSS** for styling

---

## Prerequisites

### System Requirements
- **Node.js** 16+ (for frontend)
- **Java 17+** (for backend)
- **Maven 3.6+** (Maven wrapper included)
- **PostgreSQL 12+** (or Supabase account)

### Accounts Needed (for cloud)
- Supabase account (database) — or local PostgreSQL
- Gmail account (SMTP for OTP email sending)

---

## Getting Started

### 1. Database Setup

#### Option A: Local PostgreSQL
```bash
# Create a local database
createdb lecturboxd_db
```

#### Option B: Supabase (Cloud)
1. Create a Supabase project at https://supabase.com
2. Copy the connection string
3. Run migrations (see Backend Setup below)

### 2. Backend Setup

```bash
cd backend

# Copy and configure local properties
cp src/main/resources/application-local.properties.example src/main/resources/application-local.properties

# Edit application-local.properties with your database/email credentials:
# spring.datasource.url=jdbc:postgresql://localhost:5432/lecturboxd_db
# spring.datasource.username=postgres
# spring.datasource.password=<your-password>
# spring.mail.username=<your-gmail>
# spring.mail.password=<app-specific-password>
# jwt.secret=<random-256-bit-secret>

# Build and run the backend
./mvnw clean spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"

# Backend will be available at: http://localhost:8081
```

**Note:** Flyway automatically runs migrations from `database/migrations/` on startup.

### 3. Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Create environment file
cp .env.example .env.local

# Edit .env.local if your backend runs on a different port:
# VITE_API_BASE_URL=http://localhost:8081

# Start the dev server
npm run dev

# Frontend will be available at: http://localhost:5173
```

---

## Project Structure

```
final-project-pied_piper/
├── backend/                          # Spring Boot Java backend
│   ├── src/main/java/com/lecturboxd/
│   │   ├── entity/                  # JPA entities (User, Lecture, Review, ChatMessage, etc.)
│   │   ├── repository/              # Spring Data JPA repositories
│   │   ├── service/                 # Business logic layer
│   │   ├── controller/              # REST + WebSocket endpoints
│   │   ├── dto/                     # Data Transfer Objects (request/response)
│   │   ├── mapper/                  # Entity → DTO mappers
│   │   ├── config/                  # Spring configs (Security, CORS, WebSocket, JWT)
│   │   ├── auth/                    # JWT & authentication components
│   │   ├── exception/               # Global exception handling
│   │   └── websocket/               # WebSocket handlers & interceptors
│   ├── src/main/resources/
│   │   ├── application.yml          # Main config
│   │   ├── application-local.properties
│   │   ├── application-dev.yml
│   │   └── application-prod.yml
│   └── database/
│       ├── migrations/              # Flyway SQL migrations (V001-V008)
│       └── seeds/                   # Optional seed data
├── frontend/                         # React + TypeScript frontend
│   ├── src/
│   │   ├── api/                     # API client functions (axios)
│   │   ├── types/                   # TypeScript interfaces
│   │   ├── auth/                    # Authentication context & hooks
│   │   ├── components/              # Reusable UI components
│   │   ├── features/                # Feature modules (auth, chat, reviews, etc.)
│   │   ├── hooks/                   # Custom React hooks (useWebSocket, useChat, etc.)
│   │   ├── routes/                  # Router configuration
│   │   └── utils/                   # Helper functions
│   └── .env.local                   # Environment variables (not committed)
└── database/
    └── migrations/                  # SQL migration scripts (Flyway format)
```

---

## Features & Testing Flow

### Complete Testing Checklist

#### 1. **Register & OTP Verification** ✅
```
Flow: Register → Verify OTP → Login
1. Navigate to /register
2. Fill in: Name, Email (use Gmail for OTP), Password
3. Click "Register"
4. Check your Gmail for OTP code
5. Navigate to /verify and enter the OTP
6. Should redirect to login after successful verification
7. Login with your credentials
```

#### 2. **Browse Lecture Syllabus** ✅
```
Flow: /lectures
1. Select a Faculty (e.g., "Computer Science")
2. Select a Semester (e.g., "Fall 2023")
3. Select a Subject (e.g., "Data Structures")
4. View lectures in the subject
5. Click a lecture to view details
```

#### 3. **Write & Read Reviews** ✅
```
Flow: /lectures/{lectureId}/reviews
1. Logged in (required)
2. Fill in rating (1-5 stars) and review text
3. Click "Submit Review"
4. See your review in the "Community reviews" section
5. View other users' reviews
```

#### 4. **Log a Lecture** ✅
```
Flow: /lectures/{lectureId}/log
1. On a lecture detail page, click "Log Lecture"
2. Optionally add notes
3. Click "Log Lecture"
4. Should see it recorded in your profile stats
```

#### 5. **View Activity Feed** ✅
```
Flow: /feed (requires login)
1. Follow another user first (/profile/{userId} → Follow button)
2. Once you follow someone, their activities appear in your feed
3. Feed shows: Reviews they wrote, Lectures they logged
4. Can paginate through older activities
```

#### 6. **User Profiles & Following** ✅
```
Flow: /profile (own) or /profile/{userId} (others)
1. View profile stats (reviews, lecture logs, followers, following)
2. On other users' profiles, click "Follow" or "Unfollow"
3. See follower/following lists
4. Recent logged lectures displayed
```

#### 7. **Real-time Chat** ✅
```
Flow: /chat (requires login + 2 different users)
SETUP: 
  a) Register 2 different test users (User A and User B)
  b) Both must be logged in (in different browser windows)

USER A:
  1. Navigate to /chat
  2. Start a conversation by navigating to User B's profile
  3. Wait for User B to see the conversation

USER B:
  1. Navigate to /chat
  2. Should see User A in conversations list
  3. Click to open the chat
  4. Send a message

USER A:
  1. Message from User B appears in real-time ✨
  2. Click the message to mark it as read
  
Verify:
  - Messages appear immediately (no page refresh needed)
  - Read status changes (checkmark appears)
  - Unread count in conversation list updates
```

#### 8. **Protected Routes** ✅
```
Try accessing auth-required pages without logging in:
- /profile → Should redirect to /login
- /chat → Should redirect to /login
- /feed → Should redirect to /login
```

---

## Troubleshooting

### Frontend won't start (`npm run dev`)
```bash
# Clear node_modules and reinstall
rm -rf node_modules package-lock.json
npm install

# Check port 5173 isn't already in use
# Vite will auto-select a different port if needed
```

### Backend won't start
```bash
# Check if port 8081 is in use
netstat -ano | findstr :8081  # Windows
lsof -i :8081                  # macOS/Linux

# Verify database connection in application-local.properties
mvn clean compile

# Check logs for error details
tail -f target/spring-boot-application.log
```

### WebSocket connection fails
```
Symptoms: Chat page loads but shows "Connecting…" forever
Solutions:
1. Verify backend is running on correct port (8081)
2. Check browser console for error messages
3. Ensure JWT token is valid (not expired)
4. Verify .env.local has correct VITE_API_BASE_URL
5. Check backend WebSocket logs: Connection refused, CORS issues
```

### CORS errors
```
Symptoms: Frontend API calls fail with "CORS policy" error
Solutions:
1. Ensure backend is running
2. Check application-local.properties for correct CORS origins
3. Default allowed origin: http://localhost:5173
4. If frontend is on different port, add it to lecturboxd.cors.allowed-origins
```

### OTP Email not received
```
Solutions:
1. Check Gmail app password (not regular password)
2. Verify email in application-local.properties:
   spring.mail.username=your-gmail@gmail.com
   spring.mail.password=<16-char app-specific password>
3. Check spam folder
4. For testing only: backend logs will show generated OTP
```

### Database migrations not running
```
Symptoms: Tables don't exist when app starts
Solutions:
1. Check database connection is correct
2. Verify migrations/ folder exists and has V00X__*.sql files
3. Check Spring logs for Flyway output
4. Ensure database user has CREATE TABLE permissions
```

---

## Development Workflow

```bash
# Terminal 1: Backend
cd backend
./mvnw clean spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"

# Terminal 2: Frontend
cd frontend
npm run dev

# Visit http://localhost:5173
```

---

## Building for Production

### Backend
```bash
cd backend
./mvnw clean package -DskipTests
# JAR file: backend/target/lecturboxd-backend-0.0.1-SNAPSHOT.jar

# Deploy to server:
java -jar lecturboxd-backend-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --spring.datasource.url=jdbc:postgresql://<prod-db-host>:5432/lecturboxd_db \
  --spring.datasource.username=<db-user> \
  --spring.datasource.password=<db-pass>
```

### Frontend
```bash
cd frontend
npm run build
# Static files: frontend/dist/

# Deploy to CDN/static host (Netlify, Vercel, S3, etc.)
```

---

## License

Private project for university coursework.

