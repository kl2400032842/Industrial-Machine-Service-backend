# Industrial Machine Service Backend

Java 21 modular monolith using Spring Boot, Spring Web, Spring Data JPA/Hibernate, Spring Security, JWT (JJWT), MySQL, Maven and OpenAPI.

## Included modules

- Authentication: customer registration, login, BCrypt hashing, JWT issuing/validation
- Users: current profile, profile update, admin user listing/role control
- Machines: customer-owned machine registration and listing
- Tickets: ticket creation, customer listing, ticket lookup, admin status changes, transition validation
- Assignments: admin assignment, engineer assignment listing and status updates
- Attachments: local image/audio/PDF upload, metadata, authorized download
- Work orders: create and update diagnosis, repair details, outcomes
- Service history: create and list records for a machine
- Admin and dashboard: role management, enabled/disabled users, audit logs and ticket counts
- Common: security, API errors, health endpoint and Swagger

## Prerequisites

- JDK 21
- Maven 3.9+
- MySQL 8+
- STS or IntelliJ IDEA

## 1. Create database

```sql
CREATE DATABASE industrial_service CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## 2. Configure credentials

In `src/main/resources/application.properties`, either set your local values directly or use environment variables:
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET` (must be at least 32 UTF-8 bytes)
- `JWT_EXPIRATION_MS` (default 3600000)
- `LOCAL_STORAGE_DIRECTORY` (default `./uploads`)

Never commit real production credentials or JWT secrets. `change_me` is only a placeholder.

## 3. Import and run in STS

1. Extract the ZIP.
2. Select **File → Import → Maven → Existing Maven Projects**.
3. Select the extracted project folder and finish.
4. Confirm the JDK is 21 and MySQL is running.
5. Update database credentials.
6. Run `IndustrialServiceApplication.java` as a Spring Boot App.

Terminal alternative:

```bash
mvn clean test
mvn spring-boot:run
```

Health: `GET http://localhost:8080/api/v1/health`
Swagger: `http://localhost:8080/swagger-ui.html`

Hibernate `ddl-auto=update` is for local development only. Use Flyway/Liquibase migrations in production.

## 4. First account and roles

`POST /api/v1/auth/register` always creates a CUSTOMER. Public registration intentionally cannot set ADMIN or ENGINEER.

Public registration always creates CUSTOMER accounts. For a first local administrator, set these environment variables before starting the app: `BOOTSTRAP_ADMIN_EMAIL`, `BOOTSTRAP_ADMIN_PASSWORD` (at least 12 characters), `BOOTSTRAP_ADMIN_NAME`, and `BOOTSTRAP_ADMIN_CONTACT`. On startup, the app creates that ADMIN only if the email does not already exist. After the account is created, remove these environment variables. Never configure bootstrap credentials in a production deployment. Engineers can then be created by an administrator using `PATCH /api/v1/admin/users/{id}/role`.

Example registration:

```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "name": "Test Customer",
  "email": "customer@example.com",
  "password": "ChangeThis123!",
  "contactNumber": "9876543210"
}
```

Login:

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "customer@example.com",
  "password": "ChangeThis123!"
}
```

Copy the returned token and send `Authorization: Bearer <token>` on protected requests.

## 5. Main endpoints

| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/v1/auth/register` | Public; customer only |
| POST | `/api/v1/auth/login` | Public |
| GET/PATCH | `/api/v1/users/me` | Authenticated |
| GET | `/api/v1/admin/users` | Admin |
| PATCH | `/api/v1/admin/users/{id}/role` | Admin |
| POST | `/api/v1/machines` | Authenticated; creates machine for current user |
| GET | `/api/v1/machines/my` | Authenticated |
| GET | `/api/v1/machines` | Admin |
| POST | `/api/v1/tickets` | Authenticated customer with owned machine |
| GET | `/api/v1/tickets/my` | Authenticated; own tickets |
| GET | `/api/v1/tickets` | Admin |
| GET | `/api/v1/admin/tickets/search?ticketNumber=IMS-&machineSerialNumber=SN123` | Admin |
| GET | `/api/v1/tickets/{id}` | Owner or admin |
| PATCH | `/api/v1/tickets/{id}/status` | Admin |
| POST | `/api/v1/assignments` | Admin |
| GET | `/api/v1/assignments/my` | Engineer |
| PATCH | `/api/v1/assignments/{id}/status` | Assigned engineer or admin |
| POST | `/api/v1/work-orders` | Engineer/admin |
| PATCH | `/api/v1/work-orders/{id}` | Assigned engineer/admin |
| POST | `/api/v1/tickets/{ticketId}/attachments?type=IMAGE` | Ticket owner/admin |
| GET | `/api/v1/tickets/{ticketId}/attachments` | Ticket owner/admin |
| GET | `/api/v1/attachments/{id}/download` | Ticket owner/admin |
| POST | `/api/v1/service-history` | Engineer/admin |
| GET | `/api/v1/service-history/machine/{machineId}` | Machine owner/admin |
| GET | `/api/v1/admin/dashboard/summary` | Admin |
| GET | `/api/v1/admin/audit-logs` | Admin |

`type` for uploads must be `IMAGE`, `AUDIO`, or `DOCUMENT`. Allowed types are JPEG/PNG/WebP for images, supported audio MIME types for audio, and PDF for documents. Uploads are limited to 10 MB per file.

## 6. Important implementation notes

- This is a full starter implementation, but it still needs project-specific review and testing before production.
- The initial administrator must be bootstrapped securely; do not allow public role selection.
- The app stores uploaded files locally. Use private object storage and signed/authorized access in deployment.
- Speech-to-text, push notification delivery, vendor workflows, reviews, refresh tokens, rate limiting, email verification, password reset, and malware scanning are not implemented because they were not part of the final table list or need external provider choices.
- Audit logs cover key ticket, assignment, work-order and admin actions, but should be expanded as the workflow is finalized.
- For production, add Flyway migrations, integration tests, stricter content-signature validation for uploads, structured logging, secret management, rate limits, and security review.
- Confirm whether customers should be allowed to register machines for different companies/sites and whether multiple work orders per ticket are needed.
