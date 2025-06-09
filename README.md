# 📅 Community Events Platform

A mobile app designed for a small community business that allows users to browse, sign up for, and add events to their Google Calendar. Staff users can log in to create, manage, and view event participation.

---

## 🚀 Features

A demo can be viewed with the youtube link: https://www.youtube.com/watch?v=mGeb2r-gaQ0

Features include:

- 🔐 Secure JWT-based authentication
- 📅 Event creation and management
- 👥 User registration and profile management
- 📧 Email verification system
- 🔄 Password recovery functionality
- 📊 Event participation tracking
- 🔒 Role-based access control
- 📱 RESTful API architecture
  

### 👥 Community Users
- Browse a list of upcoming events
- Sign up for events 
- Add events to Google Calendar after sign-up
- Sign in with Google or email/password

### 👩‍💼 Staff Users
- Secure sign-in to manage events
- Create, edit, and delete events

---

## 🛠 Tech Stack

| Layer        | Technology            |
|-------------|------------------------|
| Frontend    | Android (Kotlin)       |
| UI          | Jetpack Compose or XML |
| Auth        | Google Sign-In |
| Backend     | Spring Boot 3.2.3    |
| Database     | MySQL   |
| API Documentation    | Swagger/OpenAPI    |
| Build Tool     | Maven    |
| Container| Tomcat    |

---

## Prerequisites
- Java 11 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher
- Tomcat 9.x

---

## ⚙️ Setup Instructions

### 1. Database Setup
sql
CREATE DATABASE eventease;


### 2. Backend Configuration
Update src/main/resources/application.properties with your database credentials:
properties
spring.datasource.url=jdbc:mysql://localhost:3306/eventease
spring.datasource.username=your_username
spring.datasource.password=your_password


### 3. Build and Run
bash
# Build the application
mvn clean package

# Run the application
java -jar target/eventease-1.0.0.war

## API Endpoints

### Authentication
- POST /api/auth/register - Register new user
- POST /api/auth/login - User login
- POST /api/auth/forgot-password - Password recovery
- POST /api/auth/reset-password - Reset password

### Events
- GET /api/events - List all events
- POST /api/events - Create new event
- GET /api/events/{id} - Get event details
- PUT /api/events/{id} - Update event
- DELETE /api/events/{id} - Delete event

## Project Structure

src/main/java/com/eventease/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── domain/         # Entity classes
├── dto/            # Data transfer objects
├── repository/     # JPA repositories
├── security/       # Security configurations
└── service/        # Business logic


## Security Features
- JWT-based authentication
- Password encryption
- Email verification
- Role-based authorization
- Secure password reset

## Development

### Running Tests
bash
mvn test


### Code Style
- Follow Java coding conventions
- Use meaningful variable and method names
- Add comments for complex logic
- Keep methods focused and small

## Deployment

### Production Deployment
1. Build the WAR file:
bash
mvn clean package


2. Deploy to Tomcat:
bash
cp target/eventease-1.0.0.war /opt/tomcat/webapps/

3. Configure Tomcat:
- Add memory leak prevention listeners
- Configure MySQL connection cleanup
- Set appropriate JVM options

## Troubleshooting

### Common Issues
1. MySQL Connection Cleanup
   - Add com.mysql.cj.jdbc.AbandonedConnectionCleanupThread=false to system properties
   - Use appropriate MySQL connector version

2. Memory Leaks
   - Monitor Tomcat logs
   - Check for proper resource cleanup
   - Use appropriate JVM memory settings

## Contributing
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request


📝 License
This project is open-source and available under the MIT License.



