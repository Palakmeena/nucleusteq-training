# Interview Tracker — Backend

Spring Boot backend for Interview Tracker. Implements REST APIs for authentication, candidate management, interviews, panel members, feedback and file uploads.

## Prerequisites
- Java 17 (or compatible JVM)
- Maven (the project includes the Maven Wrapper `mvnw`/`mvnw.cmd`)
- PostgreSQL (or update `application.properties` for another DB)

## Quick start
1. Create a PostgreSQL database and user. Update `src/main/resources/application.properties` or provide environment variables for datasource:

- `spring.datasource.url` (e.g. `jdbc:postgresql://localhost:5432/interviewtracker`)
- `spring.datasource.username`
- `spring.datasource.password`

2. Run the app with the bundled wrapper:

```bash
cd backend
./mvnw spring-boot:run       # on Unix/macOS
\mvnw.cmd spring-boot:run   # on Windows (PowerShell/CMD)
```

3. The API root is `http://localhost:8080/api` by default.

## Build & tests
- Run unit/integration tests:

```bash
./mvnw test
```

- Build a jar:

```bash
./mvnw package
java -jar target/*.jar
```

## Configuration & secrets
- JWT secret, mail and Google Drive credentials are set in `application.properties` or via environment variables. Sensitive files such as `client_secret.json` are expected under `backend/src/main/resources/` or an external secure path — update config accordingly.

## Common troubleshooting
- Port in use: If the app fails to start due to port 8080 being busy, either free the port or change `server.port` in `application.properties`.
- DB connection errors: verify the DB is reachable and credentials are correct.
- Deleting panel members: the service prevents deleting a panel member with existing feedback — remove related feedback first (HTTP 409 conflict will be returned).

## Useful endpoints (examples)
- `POST /api/auth/login` — obtain JWT token
- `GET /api/hr/panels` — list panel members
- `DELETE /api/hr/panel/{id}` — delete panel member (subject to safe-delete checks)

## Tests & code quality
- Project includes unit tests and quality plugins (JaCoCo, SpotBugs, PMD, Checkstyle). Use `./mvnw test` and the configured Maven goals for reports.

## Contact
If you need environment setup help (DB, SMTP, Google Drive), ask and include error logs.
