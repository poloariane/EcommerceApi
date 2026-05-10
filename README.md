# Laboratory 9: Securing the API with Sessions & Input Validation

## Overview
A Spring Boot ecommerce API with session-based authentication, role-based authorization, and Bean Validation.

## Features
- User registration and login with HTTP session authentication
- Role-based access control: `ROLE_USER` and `ROLE_ADMIN`
- Product CRUD operations with admin-only protection
- Input validation using Jakarta Bean Validation
- Global exception handling for validation and access errors
- BCrypt password hashing
- MySQL persistence via Spring Data JPA
- Simple frontend pages for login, signup, and landing

## Security Architecture

### Session-Based Authentication
This application uses stateful session authentication with cookies, not JWT.

**Flow:**
1. User registers using `POST /api/v1/auth/register`
2. User logs in using `POST /login`
3. Spring Security creates an HTTP session and sets the `JSESSIONID` cookie
4. The browser automatically sends the cookie on future requests
5. Protected endpoints validate the session on the server
6. User logs out via `POST /logout`

### Key Security Components
- `SecurityConfig`: configures authorization, form login, CSRF, and session management
- `CustomUserDetailsService`: loads users from database for authentication
- `PasswordEncoder` bean: uses `BCryptPasswordEncoder`
- `User` entity: implements `UserDetails`
- `@PreAuthorize`: secures controller methods by role
- `CookieCsrfTokenRepository`: provides CSRF tokens for form submissions

### Authentication vs Authorization
- Authentication verifies user identity
- Authorization checks what an authenticated user is allowed to do

Example:
- login verifies the credentials
- admin-only product creation checks for `ROLE_ADMIN`

## Validation Rules

### RegisterRequest DTO
| Field | Constraints | Message |
|-------|-------------|---------|
| `username` | `@NotBlank`, `@Size(min=8, max=20)` | Username is required and must be 8-20 characters |
| `password` | `@NotBlank`, `@Size(min=8, max=100)` | Password is required and must be at least 8 characters |
| `role` | `@NotBlank`, `@Pattern(...)` | Role must be USER, SHOPPER, SELLER, or ADMIN |

### Product DTOs
Product request validation is enabled on create/update flows using DTOs with:
- `@NotBlank`
- `@Size(...)`
- `@NotNull`
- `@Positive`
- `@Min(0)`

### Global Exception Handling
Validation failures are handled by `GlobalExceptionHandler` and return structured responses.

**Validation response example:**
```json
{
  "timestamp": "2026-05-10T12:00:00",
  "errors": [
    "Field 'name' must not be blank",
    "Field 'price' must be positive"
  ]
}
```

## API Reference

### Public Endpoints
| Method | Path | Description |
|---|---|---|
| `GET` | `/api/v1/products` | List all products |
| `GET` | `/api/v1/products/{id}` | Get product details |
| `GET` | `/api/v1/products/filter?filterType={type}&filterValue={value}` | Filter products by type/value |
| `POST` | `/api/v1/auth/register` | Register a new user |
| `GET` | `/login.html` | Login page |
| `GET` | `/signup.html` | Signup page |

### Authentication Endpoints
| Method | Path | Description |
|---|---|---|
| `POST` | `/login` | Authenticate user with form data and set `JSESSIONID` cookie |
| `POST` | `/logout` | Invalidate session and log out |

### Protected Endpoints
| Method | Path | Required Role | Description |
|---|---|---|---|
| `POST` | `/api/v1/orders` | authenticated | Create an order |
| `POST` | `/api/v1/products` | `ADMIN` | Create a product |
| `PUT` | `/api/v1/products/{id}` | `ADMIN` | Update a product |
| `PATCH` | `/api/v1/products/{id}` | `ADMIN` | Patch product fields |
| `DELETE` | `/api/v1/products/{id}` | `ADMIN` | Delete a product |

### Notes
- `/api/v1/auth/register` is publicly accessible
- `/login` is the Spring Security form login processing URL
- CSRF protection is enabled for form submissions
- Successful login redirects to `/landing.html`

## Frontend Pages
Accessible pages include:
- `http://localhost:8080/signup.html`
- `http://localhost:8080/login.html`
- `http://localhost:8080/landing.html`

The login form submits to `/login` and includes a hidden CSRF token field.

## Error Responses

### Validation Error
```json
{
  "timestamp": "2026-05-10T12:00:00",
  "errors": [
    "Field 'username' Username is required",
    "Field 'password' Password is required"
  ]
}
```

### Access Denied
```json
{
  "timestamp": "2026-05-10T12:00:00",
  "error": "Forbidden",
  "message": "You do not have permission to access this resource"
}
```

### Illegal Argument
```json
{
  "timestamp": "2026-05-10T12:00:00",
  "error": "Bad Request",
  "message": "Username is already taken"
}
```

## Database Schema

### `users`
```sql
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(50) NOT NULL,
  enabled BOOLEAN DEFAULT TRUE,
  account_non_expired BOOLEAN DEFAULT TRUE,
  account_non_locked BOOLEAN DEFAULT TRUE,
  credentials_non_expired BOOLEAN DEFAULT TRUE
);
```

### `products`
```sql
CREATE TABLE products (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(500),
  price DOUBLE NOT NULL,
  category VARCHAR(100) NOT NULL,
  stock_quantity INT NOT NULL,
  image_url VARCHAR(255)
);
```

## Getting Started

### Prerequisites
- Java 25+
- MySQL 5.5+ running on `localhost:3306`
- Gradle 8.0+

### Setup
1. Clone the repository:
```bash
git clone https://github.com/carpiocebuano/EcommerceApi.git
cd EcommerceApi
```
2. Create the database:
```sql
CREATE DATABASE ecommerce_db;
```
3. Configure `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_db
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

### Run the application
```powershell
./gradlew.bat bootRun
```

Open `http://localhost:8080` in your browser.

## Postman Test Flow

1. Register user:
```http
POST http://localhost:8080/api/v1/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "Password123",
  "role": "USER"
}
```

2. Login using form data:
```http
POST http://localhost:8080/login
Content-Type: application/x-www-form-urlencoded

username=testuser&password=Password123
```

3. Create order with session cookie:
```http
POST http://localhost:8080/api/v1/orders
Cookie: JSESSIONID=<cookie>
Content-Type: application/json

{
  "productId": 1,
  "quantity": 2
}
```

4. Attempt admin action as user:
```http
POST http://localhost:8080/api/v1/products
Cookie: JSESSIONID=<cookie>
Content-Type: application/json

{
  "name": "Test Product",
  "price": 50.0,
  "category": "Test",
  "stockQuantity": 10
}
```
Expected: `403 Forbidden`

5. Create invalid product:
```http
POST http://localhost:8080/api/v1/products
Cookie: JSESSIONID=<admin-cookie>
Content-Type: application/json

{
  "name": "x",
  "price": -10,
  "category": "",
  "stockQuantity": -5
}
```
Expected: `400 Bad Request`

6. Logout:
```http
POST http://localhost:8080/logout
```

## Project Structure

- `src/main/java/com/ws101/abundopolo/ecommerceapi/config/SecurityConfig.java`
- `src/main/java/com/ws101/abundopolo/ecommerceapi/controller/AuthController.java`
- `src/main/java/com/ws101/abundopolo/ecommerceapi/controller/ProductController.java`
- `src/main/java/com/ws101/abundopolo/ecommerceapi/controller/OrderController.java`
- `src/main/java/com/ws101/abundopolo/ecommerceapi/dto/RegisterRequest.java`
- `src/main/java/com/ws101/abundopolo/ecommerceapi/dto/CreateProductDto.java`
- `src/main/java/com/ws101/abundopolo/ecommerceapi/dto/UpdateProductDto.java`
- `src/main/java/com/ws101/abundopolo/ecommerceapi/dto/CreateOrderDto.java`
- `src/main/java/com/ws101/abundopolo/ecommerceapi/exception/GlobalExceptionHandler.java`
- `src/main/java/com/ws101/abundopolo/ecommerceapi/model/User.java`
- `src/main/java/com/ws101/abundopolo/ecommerceapi/model/Product.java`
- `src/main/java/com/ws101/abundopolo/ecommerceapi/model/Role.java`
- `src/main/java/com/ws101/abundopolo/ecommerceapi/repository/UserRepository.java`
- `src/main/java/com/ws101/abundopolo/ecommerceapi/service/AuthService.java`
- `src/main/java/com/ws101/abundopolo/ecommerceapi/service/CustomUserDetailsService.java`
- `src/main/resources/application.properties`
- `src/main/resources/static/login.html`
- `src/main/resources/static/signup.html`
- `src/main/resources/static/landing.html`
- `build.gradle`
- `README.md`

## Technologies Used

- Spring Boot
- Spring Security
- Spring Data JPA
- MySQL
- Lombok
- Jakarta Bean Validation
- Gradle
- Java

## Authors
Abundo, Clarissa Mae T.
Polo, Ariane C.

## License
This project is part of the Web Systems 101 course.
