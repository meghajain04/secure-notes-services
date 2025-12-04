Secure Notes API:
A simple Spring Boot REST API that allows users to create, read, update, and delete secure notes stored in an H2 in-memory database.

Design Choices:
Spring Boot + H2

Architecture:
Controller → Service → Repository

Encryption for note content.

Security Measures:
Static token authentication:
Every request must include
     Authorization: <token>

The token value is set in application.properties.

Input validation:
Title cannot be empty
Content cannot be null

Unauthorized requests:
Missing or incorrect token → 401 Unauthorized

Setup Instructions

1.Clone the repository
   git clone <repo-url>
   cd secure-notes

2.Run the application
   mvn spring-boot:run

3.H2 Database Console

   Visit:
   http://localhost:8080/h2-console

   JDBC URL:
   jdbc:h2:mem:testdb

4.Send API Requests
Every request must include the Authorization header. Example:


## API Endpoints

### 1. Create Note
POST /notes
Headers:
Authorization: <token>
Body (JSON):
{
"title": "My Note",
"content": "Secret content"
}

### 2. Get Note by ID
GET /notes/{id}
Headers:
Authorization: <token>

### 3. Get All Notes
GET /notes
Headers:
Authorization: <token>

### 4. Update Note
PUT /notes/{id}
Headers:
Authorization: <token>
Body (JSON):
{
"title": "Updated Title",
"content": "Updated content"
}

### 5. Delete Note
DELETE /notes/{id}
Headers:
Authorization: <token>
