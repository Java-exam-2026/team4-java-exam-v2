# Java Exam System V2

This project is a recreation of the Java Exam System using Thymeleaf for the frontend and SQLite for the database.

## Features

- **Frontend**: Thymeleaf + Bootstrap 5
- **Database**: SQLite
- **Security**: Spring Security (Form Login)
- **Features**:
  - Login (User: testuser / user123, Admin: admin / admin123)
  - Dashboard with Chapter list and Progress
  - Quiz taking interface
  - Result display

## How to Run

1. Build the project:
   ```bash
   ./mvnw clean install
   ```

2. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

3. Access the application at `http://localhost:8080`.

## Database

The application uses a local SQLite database `java-exam.db`.
The database is initialized with data from `src/main/resources/data.sql` on the first run.

## Project Structure

- `src/main/java/com/javaexam`: Backend logic (Controllers, Services, Repositories, Entities)
- `src/main/resources/templates`: Thymeleaf templates (login, dashboard, quiz, result)
- `src/main/resources/application.properties`: Configuration
