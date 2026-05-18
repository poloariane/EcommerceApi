# JWT Authentication Implementation Guide

## Overview
Your EcommerceAPI project has been updated to support JWT (JSON Web Token) authentication. This document provides setup instructions and key information about the implementation.

## What Was Added/Updated

### 1. Backend Changes

#### Dependencies (build.gradle)
Added the following JWT libraries:
```groovy
implementation 'io.jsonwebtoken:jjwt-api:0.11.5'
runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.11.5'
runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.11.5'
```

#### New Service: JwtUtil
- Location: `src/main/java/.../service/JwtUtil.java`
- Responsible for:
  - Generating JWT tokens
  - Validating JWT tokens
  - Extracting claims from tokens
  - Managing token expiration

#### New Filter: JwtAuthenticationFilter
- Location: `src/main/java/.../config/JwtAuthenticationFilter.java`
- Intercepts incoming HTTP requests
- Extracts JWT from `Authorization: Bearer <token>` header
- Validates token and sets authentication context

#### New DTOs
- `LoginRequest.java` - Request body for login endpoint
- `AuthResponse.java` - Response from login endpoint with JWT token

#### Updated AuthService
- New `login(LoginRequest)` method that:
  - Authenticates user credentials
  - Generates JWT token
  - Returns token to client

#### Updated AuthController
- New `/api/v1/auth/login` POST endpoint
- Accepts username and password
- Returns JWT token and user info

#### Updated SecurityConfig
- Integrated `JwtAuthenticationFilter`
- Added JWT endpoints to CSRF ignore list
- Configured proper authorization rules

#### Updated application.properties
- Added JWT configuration:
  ```properties
  jwt.secret=your-very-strong-secret-key-that-is-at-least-32-characters-long-for-hs256
  jwt.expiration=86400000
  ```

### 2. Frontend Changes

#### Updated script.js
New JWT-related functions:
- `getJwtToken()` - Retrieves token from localStorage
- `saveJwtToken(token)` - Stores token in localStorage
- `clearJwtToken()` - Removes token from localStorage
- `isJwtTokenValid()` - Checks if token exists
- `loginWithJwt(username, password)` - Performs JWT login
- Updated `authFetch()` - Now includes JWT in Authorization header

#### Updated login.html
- Modified login form submission to use JWT authentication
- Removed CSRF token handling (not needed for stateless API)
- Handles redirect after successful login
- Displays loading state during login

## Setup Instructions

### 1. Configure JWT Secret (IMPORTANT)
In `src/main/resources/application.properties`, update the JWT secret:

```properties
jwt.secret=your-very-strong-secret-key-that-is-at-least-32-characters-long-for-hs256
```

**Recommendations:**
- Use a strong, random string
- Minimum 32 characters for HS256
- Store securely in production (e.g., environment variables)
- Example: `openssl rand -base64 32`

### 2. Update API_BASE_URL (If Needed)
In `script.js`, update the `API_BASE_URL` if your backend is hosted elsewhere:

```javascript
const API_BASE_URL = 'http://localhost:8080'; // Update if needed
```

### 3. Build and Run

```bash
# Build the project
gradle build

# Run the application
gradle bootRun
```

The application will start on `http://localhost:8080`

## JWT Authentication Flow

### 1. User Login
**Request:**
```
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "user@example.com",
  "role": "USER",
  "message": "Login successful"
}
```

### 2. Authenticated Request
**Request:**
```
GET /api/v1/protected-endpoint
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

The JWT is automatically included by the `authFetch()` function.

### 3. Token Validation
- Server validates signature using the secret key
- Server checks token expiration
- Server extracts username and loads user details
- User is authenticated if token is valid

## Important Notes

### Token Storage
- Tokens are stored in `localStorage` with key `jwt_token`
- **Security Note:** For production, consider:
  - HttpOnly cookies (managed by backend) for better XSS protection
  - Refresh token mechanism for token rotation
  - Shorter token expiration times

### Token Expiration
- Default: 24 hours (86400000 milliseconds)
- Configured in: `jwt.expiration` property
- When expired, user must log in again
- To implement refresh tokens, add a `/api/v1/auth/refresh` endpoint

### CORS Configuration
If frontend and backend are on different domains, ensure CORS is configured:

```java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("http://localhost:3000") // Your frontend URL
                    .allowedMethods("GET", "POST", "PUT", "DELETE")
                    .allowCredentials(true);
            }
        };
    }
}
```

## API Endpoints

### Public Endpoints
- `POST /api/v1/auth/register` - Register new user
- `POST /api/v1/auth/login` - Login (returns JWT)
- `GET /api/v1/products` - List all products
- `GET /api/v1/products/{id}` - Get product details

### Protected Endpoints (Require JWT)
- `POST /api/v1/orders` - Create order (authenticated users)
- `DELETE /api/v1/products/{id}` - Delete product (admin only)
- `POST /api/v1/products` - Create product (admin only)
- `PUT /api/v1/products/{id}` - Update product (admin only)

## Testing with Postman

### 1. Login
```
Method: POST
URL: http://localhost:8080/api/v1/auth/login
Body (JSON):
{
  "username": "testuser@example.com",
  "password": "password123"
}
```

### 2. Use Token in Protected Request
```
Method: GET
URL: http://localhost:8080/api/v1/orders
Headers:
Authorization: Bearer <token_from_login_response>
```

## Debugging

### Check Token Contents
Visit https://jwt.io and paste your token to decode it (without verification).

### Common Issues
1. **"Invalid username or password"** - Check credentials in database
2. **"401 Unauthorized"** - Token missing or expired, user needs to log in again
3. **"403 Forbidden"** - User lacks required role/permission for endpoint
4. **"Invalid secret key"** - Ensure `jwt.secret` is 32+ characters

## Future Enhancements

1. **Refresh Token Flow**
   - Short-lived access tokens (15 minutes)
   - Long-lived refresh tokens (7 days)
   - Implement `/api/v1/auth/refresh` endpoint

2. **Token Blacklist/Revocation**
   - Store revoked tokens in cache
   - Check blacklist during token validation

3. **Role-Based Authorization**
   - Enhanced role checking in filters
   - Implement `@PreAuthorize` annotations

4. **OAuth2 Integration**
   - Support for external identity providers
   - Google/GitHub login support

## References
- Assignment: "10. JWT Authentication in Spring Boot"
- JWT Specification: https://tools.ietf.org/html/rfc7519
- JJWT Documentation: https://github.com/jwtk/jjwt
- Spring Security: https://spring.io/projects/spring-security
