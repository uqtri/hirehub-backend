# HireHub Backend

A robust Spring Boot backend service for the HireHub job recruitment platform. Provides RESTful APIs, real-time communication via WebSocket, AI-powered chatbot, and comprehensive job matching algorithms.

## 🚀 Features

### Core Features

- **Authentication & Authorization**
  - JWT-based authentication with refresh tokens
  - OAuth2 integration (Google)
  - Role-based access control (RBAC)
  - Email verification and password reset
  - Redis-based session management

- **User Management**
  - User profile CRUD operations
  - Skills, experience, and education tracking
  - Resume upload and management (Cloudinary)
  - User search and filtering
  - Profile visibility controls

- **Job Management**
  - Job posting creation and management
  - Job search with advanced filtering
  - AI-powered job recommendations
  - Job status workflow (draft, pending, approved, closed)
  - Job embedding for similarity search

- **Application System**
  - Job application submission
  - Resume upload with applications
  - Application status tracking
  - Recruiter application review
  - Application analytics

- **Real-time Communication**
  - WebSocket-based chat system
  - One-on-one and group conversations
  - Message delivery and read receipts
  - Typing indicators
  - File sharing in messages

- **AI-Powered Features**
  - AI chatbot using Google Gemini (LangChain4j)
  - Resume parsing with NER (Named Entity Recognition)
  - Job recommendation engine
  - Job-candidate matching algorithm
  - Semantic job search

- **Notification System**
  - Firebase Cloud Messaging integration
  - Real-time push notifications
  - Email notifications (async with RabbitMQ)
  - In-app notification center
  - Notification preferences

- **Admin Features**
  - User moderation and management
  - Job posting approval workflow
  - Content moderation
  - Report management
  - System analytics

- **Recruiter Features**
  - Company profile management
  - Job posting management
  - Candidate pipeline
  - Application tracking
  - Recruitment analytics

### Technical Features

- **Database**
  - PostgreSQL with JPA/Hibernate
  - Complex entity relationships
  - Database migrations
  - Query optimization with Specifications

- **Caching**
  - Redis for session management
  - Token blacklisting
  - Performance optimization

- **Message Queue**
  - RabbitMQ for async operations
  - Email sending queue
  - Background job processing

- **File Management**
  - Cloudinary integration
  - Resume and image uploads
  - URL generation and transformation

- **Security**
  - Spring Security configuration
  - Password encryption (BCrypt)
  - CORS configuration
  - Request validation
  - SQL injection prevention

- **API Documentation**
  - RESTful API design
  - Consistent response format
  - Error handling
  - Validation messages

## 🛠 Tech Stack

### Core Framework
- **Spring Boot 3.5.6** - Application framework
- **Java 21** - Programming language
- **Maven** - Dependency management

### Database & Persistence
- **PostgreSQL** - Primary database
- **Spring Data JPA** - Data access layer
- **Hibernate** - ORM framework

### Security
- **Spring Security** - Authentication & authorization
- **JWT (jjwt 0.11.5)** - Token-based authentication
- **BCrypt** - Password hashing
- **OAuth2 Client** - Social login

### Caching & Messaging
- **Redis** - In-memory data store
- **RabbitMQ** - Message broker
- **Spring AMQP** - RabbitMQ integration

### Real-time Communication
- **WebSocket** - Real-time bidirectional communication
- **STOMP** - WebSocket messaging protocol
- **SockJS** - WebSocket fallback

### AI & Machine Learning
- **LangChain4j** - AI framework
- **Google AI Gemini** - Language model
- **Apache PDFBox** - PDF parsing for resumes
- **OpenAI API** - Alternative AI integration

### Cloud Services
- **Cloudinary** - Image and file storage
- **Firebase Admin SDK** - Push notifications

### Email & Templating
- **Spring Mail** - Email sending
- **Thymeleaf** - Email templates
- **JavaMailSender** - SMTP client

### Utilities & Tools
- **Lombok** - Boilerplate code reduction
- **MapStruct** - Object mapping
- **Apache Commons Text** - String utilities
- **Dotenv Java** - Environment variable management

### Development & Testing
- **Spring Boot DevTools** - Development utilities
- **Spring Boot Test** - Testing framework
- **Spring Security Test** - Security testing

## 📋 Prerequisites

- **Java 21** or higher
- **Maven 3.8+**
- **PostgreSQL 14+**
- **Redis 6+**
- **RabbitMQ 3.9+**
- **Cloudinary Account** (for file uploads)
- **Firebase Project** (for push notifications)
- **Google Gemini API Key** (for AI features)

## 🚀 Getting Started

### 1. Clone the repository

```bash
git clone <repository-url>
cd hirehub-combined/hirehub-backend
```

### 2. Configure PostgreSQL

Create a database:
```sql
CREATE DATABASE hirehub;
CREATE USER hirehub_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE hirehub TO hirehub_user;
```

### 3. Configure Redis

Ensure Redis is running:
```bash
redis-server
```

### 4. Configure RabbitMQ

Ensure RabbitMQ is running:
```bash
rabbitmq-server
```

### 5. Set up environment variables

Create a `.env` file or update `application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/hirehub
spring.datasource.username=hirehub_user
spring.datasource.password=your_password

# Redis Configuration
spring.data.redis.host=localhost
spring.data.redis.port=6379

# RabbitMQ Configuration
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

# JWT Configuration
jwt.secret-key=your_jwt_secret_key_minimum_256_bits

# Email Configuration
mailer.host=smtp.gmail.com
mailer.port=587
mailer.username=your_email@gmail.com
mailer.password=your_app_password

# Frontend URL
frontend.url=http://localhost:5173

# Cloudinary Configuration
cloudinary.cloud-name=your_cloud_name
cloudinary.api-key=your_api_key
cloudinary.api-secret=your_api_secret

# Google OAuth
google.client-id=your_google_client_id
google.client-secret=your_google_client_secret
google.client-redirect-url=http://localhost:5005/api/auth/google/callback

# Firebase Configuration (place firebase-admin.json in src/main/resources/firebase/)
# Download from Firebase Console

# AI Configuration
gemini.api-key=your_gemini_api_key
openai.api-key=your_openai_api_key
openai.model=gpt-4o-mini

# NER Service (optional)
ai.ner-url=http://localhost:8000
```

### 6. Firebase Setup

1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Download the service account JSON file
3. Place it at `src/main/resources/firebase/firebase-admin.json`

### 7. Build the project

```bash
./mvnw clean install
```

Or on Windows:
```bash
mvnw.cmd clean install
```

### 8. Run the application

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:5005`

## 📦 Build for Production

### Package as JAR

```bash
./mvnw clean package -DskipTests
```

The JAR file will be created in `target/hirehub-0.0.1-SNAPSHOT.jar`

### Run the JAR

```bash
java -jar target/hirehub-0.0.1-SNAPSHOT.jar
```

### Deploy to Server

1. Transfer the JAR file to your server
2. Configure production properties
3. Run with production profile:

```bash
java -jar -Dspring.profiles.active=prod hirehub-0.0.1-SNAPSHOT.jar
```

## 🏗 Project Structure

```
hirehub-backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/example/hirehub/
│   │   │       ├── config/              # Configuration classes
│   │   │       │   ├── AsyncConfig.java
│   │   │       │   ├── CloudinaryConfig.java
│   │   │       │   ├── FirebaseConfig.java
│   │   │       │   ├── GeminiConfig.java
│   │   │       │   ├── MailerConfig.java
│   │   │       │   ├── OpenAiConfig.java
│   │   │       │   ├── RabbitConfig.java
│   │   │       │   ├── SecurityConfig.java
│   │   │       │   └── WebSocketConfig.java
│   │   │       ├── consumer/            # Message consumers
│   │   │       │   └── EmailConsumer.java
│   │   │       ├── controller/          # REST controllers
│   │   │       │   ├── AuthController.java
│   │   │       │   ├── JobController.java
│   │   │       │   ├── UserController.java
│   │   │       │   ├── MessageController.java
│   │   │       │   ├── ChatbotController.java
│   │   │       │   └── ...
│   │   │       ├── dto/                 # Data Transfer Objects
│   │   │       │   ├── auth/
│   │   │       │   ├── job/
│   │   │       │   ├── user/
│   │   │       │   └── ...
│   │   │       ├── entity/              # JPA entities
│   │   │       │   ├── User.java
│   │   │       │   ├── Job.java
│   │   │       │   ├── Message.java
│   │   │       │   └── ...
│   │   │       ├── enums/               # Enumerations
│   │   │       ├── exception/           # Custom exceptions
│   │   │       │   ├── GlobalExceptionHandler.java
│   │   │       │   └── ...
│   │   │       ├── key/                 # Composite keys
│   │   │       ├── mapper/              # MapStruct mappers
│   │   │       │   ├── UserMapper.java
│   │   │       │   ├── JobMapper.java
│   │   │       │   └── ...
│   │   │       ├── message/             # RabbitMQ messages
│   │   │       ├── producer/            # Message producers
│   │   │       │   └── EmailProducer.java
│   │   │       ├── repository/          # JPA repositories
│   │   │       │   ├── UserRepository.java
│   │   │       │   ├── JobRepository.java
│   │   │       │   └── ...
│   │   │       ├── scheduler/           # Scheduled tasks
│   │   │       ├── security/            # Security components
│   │   │       │   ├── JwtAuthenticationFilter.java
│   │   │       │   ├── JwtService.java
│   │   │       │   └── ...
│   │   │       ├── service/             # Business logic
│   │   │       │   ├── AuthService.java
│   │   │       │   ├── JobService.java
│   │   │       │   ├── UserService.java
│   │   │       │   ├── ChatbotService.java
│   │   │       │   └── ...
│   │   │       ├── specification/       # JPA Specifications
│   │   │       ├── util/                # Utility classes
│   │   │       └── HirehubApplication.java
│   │   └── resources/
│   │       ├── application.properties   # Main configuration
│   │       ├── data-init.sql           # Initial data script
│   │       ├── firebase/
│   │       │   └── firebase-admin.json # Firebase credentials
│   │       └── templates/
│   │           └── email/              # Email templates
│   │               ├── activation.html
│   │               └── reset-password.html
│   └── test/                            # Test files
├── scripts/
│   ├── insert-lookup-data.sql          # Lookup data script
│   └── cleanup-lookup-data.sql         # Cleanup script
├── pom.xml                              # Maven configuration
└── README.md
```

## 🔌 API Endpoints

### Authentication
```
POST   /api/auth/register          - Register new user
POST   /api/auth/login             - User login
POST   /api/auth/refresh           - Refresh access token
POST   /api/auth/logout            - User logout
GET    /api/auth/google            - Google OAuth login
GET    /api/auth/google/callback   - Google OAuth callback
POST   /api/auth/verify            - Verify email
POST   /api/auth/resend-verification - Resend verification email
POST   /api/auth/forgot-password   - Request password reset
POST   /api/auth/reset-password    - Reset password
```

### Users
```
GET    /api/users                  - Get all users (paginated)
GET    /api/users/{id}             - Get user by ID
PUT    /api/users/{id}             - Update user
DELETE /api/users/{id}             - Delete user
GET    /api/users/me               - Get current user
PUT    /api/users/me               - Update current user
POST   /api/users/{id}/avatar      - Upload avatar
GET    /api/users/search           - Search users
```

### Jobs
```
GET    /api/jobs                   - Get all jobs (paginated, filtered)
GET    /api/jobs/{id}              - Get job by ID
POST   /api/jobs                   - Create job (Recruiter)
PUT    /api/jobs/{id}              - Update job (Recruiter)
DELETE /api/jobs/{id}              - Delete job (Recruiter/Admin)
POST   /api/jobs/{id}/apply        - Apply for job
GET    /api/jobs/{id}/applications - Get job applications (Recruiter)
GET    /api/jobs/recommendations   - Get job recommendations
GET    /api/jobs/similar/{id}      - Get similar jobs
```

### Job Interactions
```
POST   /api/job-interactions       - Save/unsave job
GET    /api/job-interactions/user/{userId} - Get user's saved jobs
DELETE /api/job-interactions/{id}  - Remove saved job
```

### Skills
```
GET    /api/skills                 - Get all skills
GET    /api/skills/{id}            - Get skill by ID
POST   /api/skills                 - Create skill (Admin)
PUT    /api/skills/{id}            - Update skill (Admin)
DELETE /api/skills/{id}            - Delete skill (Admin)
```

### Experience
```
GET    /api/experiences/user/{userId} - Get user experiences
POST   /api/experiences            - Create experience
PUT    /api/experiences/{id}       - Update experience
DELETE /api/experiences/{id}       - Delete experience
```

### Education
```
GET    /api/studies/user/{userId}  - Get user education
POST   /api/studies                - Create education entry
PUT    /api/studies/{id}           - Update education
DELETE /api/studies/{id}           - Delete education
```

### Languages
```
GET    /api/languages              - Get all languages
POST   /api/languages/user/{userId} - Add language to user
DELETE /api/languages/user/{userId}/{languageId} - Remove language
```

### Conversations & Messages
```
GET    /api/conversations          - Get user conversations
GET    /api/conversations/{id}     - Get conversation by ID
POST   /api/conversations          - Create conversation
DELETE /api/conversations/{id}     - Delete conversation
GET    /api/conversations/{id}/messages - Get conversation messages
POST   /api/messages               - Send message
PUT    /api/messages/{id}          - Update message
DELETE /api/messages/{id}          - Delete message
```

### WebSocket Endpoints
```
CONNECT /ws                        - WebSocket connection
SUBSCRIBE /user/queue/messages     - Subscribe to personal messages
SUBSCRIBE /topic/messages/{conversationId} - Subscribe to conversation
SEND    /app/chat.sendMessage      - Send message
SEND    /app/chat.typing           - Send typing indicator
```

### Notifications
```
GET    /api/notifications          - Get user notifications
PUT    /api/notifications/{id}/read - Mark notification as read
PUT    /api/notifications/read-all - Mark all as read
DELETE /api/notifications/{id}     - Delete notification
```

### FCM Tokens
```
POST   /api/fcm-tokens             - Register FCM token
DELETE /api/fcm-tokens/{token}     - Unregister FCM token
```

### Chatbot
```
POST   /api/chatbot/ask            - Ask chatbot a question
GET    /api/chatbot/history        - Get chat history
POST   /api/chatbot/analyze-resume - Analyze resume with AI
```

### Reports
```
POST   /api/reports                - Create report
GET    /api/reports                - Get all reports (Admin)
PUT    /api/reports/{id}/status    - Update report status (Admin)
```

### Admin Endpoints
```
GET    /api/admin/users            - Get all users with filters
PUT    /api/admin/users/{id}/status - Update user status
GET    /api/admin/jobs             - Get all jobs with filters
PUT    /api/admin/jobs/{id}/approve - Approve job
PUT    /api/admin/jobs/{id}/reject - Reject job
GET    /api/admin/reports          - Get all reports
GET    /api/admin/statistics       - Get platform statistics
```

## 🗄 Database Schema

### Core Entities

- **User** - User accounts and profiles
- **Job** - Job postings
- **JobInteraction** - Job saves and applications
- **Skill** - Technical skills
- **Experience** - Work experience
- **Study** - Education history
- **Language** - Language proficiency
- **Conversation** - Chat conversations
- **Message** - Chat messages
- **Notification** - User notifications
- **Report** - Content reports
- **Role** - User roles
- **Permission** - Access permissions
- **Token** - Refresh tokens
- **FcmToken** - Firebase Cloud Messaging tokens

### Lookup Tables

- **JobType** - Job types (Full-time, Part-time, etc.)
- **JobLevel** - Job levels (Junior, Senior, etc.)
- **WorkType** - Work types (Remote, On-site, Hybrid)
- **CompanyDomain** - Company industry domains
- **University** - Universities

## 🔐 Security

### Authentication Flow

1. User registers or logs in
2. Server validates credentials
3. JWT access token (15 minutes) and refresh token (7 days) generated
4. Access token sent in response
5. Refresh token stored in Redis
6. Client includes access token in Authorization header
7. On expiration, client uses refresh token to get new access token

### Authorization

Role-based access control with three roles:
- **USER** - Regular users (job seekers)
- **RECRUITER** - Company recruiters
- **ADMIN** - System administrators

### Security Features

- Password encryption with BCrypt (strength 10)
- JWT token-based authentication
- Refresh token rotation
- Token blacklisting on logout
- CORS configuration
- Request validation
- SQL injection prevention
- XSS protection

## 🤖 AI Features

### Chatbot Service

Powered by Google Gemini via LangChain4j:
- Job search assistance
- Career advice
- Resume tips
- Interview preparation
- Company information

### Resume Analysis

- PDF parsing with Apache PDFBox
- Named Entity Recognition (NER)
- Skill extraction
- Experience parsing
- Education extraction

### Job Matching

- Semantic similarity using embeddings
- Skill matching algorithm
- Experience level matching
- Location-based filtering
- Personalized recommendations

## 📧 Email Service

### Async Email Sending

- RabbitMQ-based queue system
- Email templates with Thymeleaf
- HTML email support
- Retry mechanism

### Email Types

- Account activation
- Password reset
- Application confirmation
- Application status updates
- Recruiter notifications

## 🔔 Push Notifications

### Firebase Cloud Messaging

- Real-time push notifications
- Background and foreground handling
- Device token management
- Topic-based notifications
- Personalized notifications

### Notification Types

- New messages
- Application updates
- Job recommendations
- System announcements

## 📊 Performance Optimization

- **Redis Caching** - Session and token storage
- **Database Indexing** - Optimized queries
- **Lazy Loading** - JPA relationships
- **Pagination** - Large dataset handling
- **Connection Pooling** - Database connections
- **Async Processing** - Email and notifications

## 🧪 Testing

### Run Tests

```bash
./mvnw test
```

### Test Coverage

```bash
./mvnw jacoco:report
```

Report available at `target/site/jacoco/index.html`

## 🐛 Troubleshooting

### Common Issues

**Port already in use**
```bash
# Find and kill process
lsof -i :5005
kill -9 <PID>
```

**Database connection failed**
- Check PostgreSQL is running
- Verify connection details in application.properties
- Check firewall settings

**Redis connection failed**
- Ensure Redis server is running: `redis-cli ping`
- Check Redis configuration

**RabbitMQ connection failed**
- Verify RabbitMQ is running: `rabbitmqctl status`
- Check username and password

**Firebase initialization failed**
- Verify firebase-admin.json is present
- Check file permissions
- Validate JSON format

## 📚 API Documentation

### Response Format

#### Success Response
```json
{
  "status": "success",
  "data": { ... },
  "message": "Operation successful"
}
```

#### Error Response
```json
{
  "status": "error",
  "message": "Error description",
  "errors": [ ... ]
}
```

### Pagination
```json
{
  "content": [ ... ],
  "totalElements": 100,
  "totalPages": 10,
  "size": 10,
  "number": 0
}
```

## 🔧 Configuration

### Application Profiles

- **default** - Development
- **prod** - Production
- **test** - Testing

Activate profile:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

### Key Configuration Properties

| Property | Description | Default |
|----------|-------------|---------|
| `server.port` | Application port | 5005 |
| `spring.jpa.hibernate.ddl-auto` | Hibernate DDL mode | update |
| `jwt.secret-key` | JWT signing key | - |
| `jwt.expiration` | Token expiration (ms) | 900000 (15 min) |
| `jwt.refresh-expiration` | Refresh token expiration | 604800000 (7 days) |

## 📖 Additional Documentation

- **API Documentation**: See `/docs/api.md` (if available)
- **Database Schema**: See `/docs/schema.md` (if available)
- **Deployment Guide**: See `/docs/deployment.md` (if available)

## 🚀 Deployment

### Docker Deployment (Recommended)

```dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/hirehub-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 5005
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Environment Variables for Production

```bash
export SPRING_PROFILES_ACTIVE=prod
export DATABASE_URL=jdbc:postgresql://your-db-host:5432/hirehub
export REDIS_HOST=your-redis-host
export RABBITMQ_HOST=your-rabbitmq-host
export JWT_SECRET=your-production-secret
```

## 📈 Monitoring & Logging

- Spring Boot Actuator endpoints (if enabled)
- Log files in `/logs` directory
- Application metrics
- Health checks at `/actuator/health`

## 👥 Contributing

1. Create a feature branch
2. Follow Java code conventions
3. Write unit tests
4. Run `./mvnw test`
5. Ensure no linting errors
6. Submit a pull request

## 📝 License

[Add your license information here]

## 🤝 Support

For issues and questions:
- Create an issue in the repository
- Contact the development team
- Check the documentation

---

**Built with ❤️ by the HireHub Team**
