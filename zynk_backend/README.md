# ZYNK HRMS - HRMS for Interns

A Spring Boot-based Human Resource Management System designed for managing intern resources.

## 🚀 Tech Stack

- **Java 21**
- **Spring Boot 4.0.0**
- **Spring Data JPA** - For database operations
- **Spring Security** - For authentication and authorization
- **H2 Database** - For development/testing (in-memory)
- **SQL Server** - For production (configured)
- **Lombok** - For reducing boilerplate code
- **Maven** - Build tool

## 📋 Prerequisites

- Java 21 or higher
- Maven 3.6+ (or use Maven Wrapper included)
- SQL Server (for production) or H2 (for development)

## 🛠️ Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/ROHIT-JOSHI/zynk_backend.git
cd zynk_backend
```

### 2. Build the Project

```bash
./mvnw clean install
```

### 3. Run the Application

```bash
./mvnw spring-boot:run
```

Or using the JAR file:

```bash
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

The application will start on **http://localhost:1234**

## 🧪 Testing the API

### Sample Endpoints

1. **Health Check**
   ```bash
   curl http://localhost:1234/api/health
   ```

2. **Hello Endpoint**
   ```bash
   curl http://localhost:1234/api/hello
   ```

Or open in browser:
- http://localhost:1234/api/health
- http://localhost:1234/api/hello

## 🗄️ Database Configuration

### Development (H2 - In-Memory)
The application is configured to use H2 database by default. Access H2 Console at:
- URL: http://localhost:1234/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (empty)

### Production (SQL Server)
To use SQL Server, update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=your_database;encrypt=true;trustServerCertificate=true
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect
```

## 👥 Team Collaboration

### For Team Members - Getting Started

1. **Clone the repository:**
   ```bash
   git clone https://github.com/ROHIT-JOSHI/zynk_backend.git
   cd zynk_backend
   ```

2. **Create a new branch for your feature:**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Make your changes and commit:**
   ```bash
   git add .
   git commit -m "Description of your changes"
   ```

4. **Push your branch:**
   ```bash
   git push origin feature/your-feature-name
   ```

5. **Create a Pull Request on GitHub** to merge your changes to main branch

### Branch Naming Convention

- `feature/feature-name` - For new features
- `bugfix/bug-description` - For bug fixes
- `hotfix/urgent-fix` - For urgent production fixes

## 📁 Project Structure

```
zynk_backend/
├── src/
│   ├── main/
│   │   ├── java/com/zynk/
│   │   │   ├── Application.java
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java
│   │   │   └── controller/
│   │   │       └── TestController.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/zynk/
│           └── ApplicationTests.java
├── pom.xml
└── README.md
```

## 🔒 Security

- Spring Security is configured
- API endpoints under `/api/**` are publicly accessible for testing
- H2 console is enabled for development (disable in production)

## 📝 Contributing

1. Always create a new branch for your work
2. Write meaningful commit messages
3. Test your changes before pushing
4. Create a Pull Request for review
5. Get approval before merging to main branch

## 🐛 Troubleshooting

### Port Already in Use
If port 1234 is already in use, change it in `application.properties`:
```properties
server.port=8080
```

### Database Connection Issues
- Check if SQL Server is running (for production)
- Verify connection credentials in `application.properties`
- For development, H2 should work automatically

## 📧 Contact

For questions or issues, please create an issue on GitHub or contact the team.

---

**Happy Coding! 🎉**

