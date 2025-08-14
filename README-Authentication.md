# Multi-Authentication Spring Boot Application

This Spring Boot application demonstrates multiple authentication methods with role-based access control.

## Authentication Methods

### 1. File-Based Authentication
- **Description**: Username/password authentication using a local properties file
- **Configuration**: `src/main/resources/users.properties`
- **Password Encoding**: BCrypt
- **Default Users**:
  - `admin` / `password123` (ROLE_ADMIN)
  - `moderator` / `password123` (ROLE_MODERATOR)  
  - `viewer` / `password123` (ROLE_VIEWER)

### 2. Google OAuth2 Authentication
- **Description**: Social login using Google accounts
- **Configuration**: Update Google client credentials in `application.properties`
- **Scope**: openid, profile, email
- **User Info**: Extracts name, email, and profile picture

### 3. LDAP Authentication
- **Description**: Enterprise directory authentication
- **Configuration**: Update LDAP server details in `application.properties`
- **Default**: Configured for localhost:389 with base DN `dc=example,dc=com`

## Role-Based Access Control

### Roles and Permissions

| Role | Access Level | Endpoints |
|------|-------------|-----------|
| **ADMIN** | Full access | `/admin/**`, `/moderator/**`, `/viewer/**` |
| **MODERATOR** | Content management | `/moderator/**`, `/viewer/**` |
| **VIEWER** | Read-only | `/viewer/**` |

### Protected Endpoints

#### Admin Only (`ROLE_ADMIN`)
- `GET /admin` - Admin dashboard page
- `GET /admin/api` - Admin API endpoint

#### Moderator + Admin (`ROLE_MODERATOR`, `ROLE_ADMIN`)
- `GET /moderator` - Moderator panel page
- `GET /moderator/api` - Moderator API endpoint

#### All Roles (`ROLE_VIEWER`, `ROLE_MODERATOR`, `ROLE_ADMIN`)
- `GET /viewer` - Viewer area page
- `GET /viewer/api` - Viewer API endpoint

#### Public Endpoints
- `GET /login` - Login page
- `GET /h2-console/**` - Database console
- `GET /oauth2/**` - OAuth2 endpoints

## Usage Examples

### File-Based Login
```
POST /login
Content-Type: application/x-www-form-urlencoded

username=admin&password=password123
```

### Testing Role Access
```bash
# Admin access (should work)
curl -u admin:password123 http://localhost:8080/admin/api

# Moderator trying admin endpoint (should fail with 403)
curl -u moderator:password123 http://localhost:8080/admin/api

# Viewer accessing viewer endpoint (should work)
curl -u viewer:password123 http://localhost:8080/viewer/api
```

### Google OAuth2 Flow
1. Navigate to `/oauth2/authorization/google`
2. Complete Google authentication
3. Redirect back to application with user info

## Configuration

### Adding New Users (File-Based)
Edit `src/main/resources/users.properties`:
```properties
# Format: username={bcrypt}password_hash,ROLE_NAME
newuser={bcrypt}$2a$10$...,ROLE_VIEWER
```

### Generate BCrypt Password Hash
```java
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String hash = encoder.encode("your_password");
System.out.println("{bcrypt}" + hash);
```

### Google OAuth2 Setup
1. Create project in [Google Cloud Console](https://console.cloud.google.com/)
2. Enable Google+ API
3. Create OAuth2 credentials
4. Update `application.properties`:
```properties
spring.security.oauth2.client.registration.google.client-id=your-client-id
spring.security.oauth2.client.registration.google.client-secret=your-client-secret
```

### LDAP Configuration
Update LDAP settings in `application.properties`:
```properties
spring.ldap.urls=ldap://your-ldap-server:389
spring.ldap.base=dc=yourcompany,dc=com
spring.ldap.username=cn=admin,dc=yourcompany,dc=com
spring.ldap.password=your-ldap-password
```

## Security Features

- **Password Encoding**: BCrypt for file-based authentication
- **CSRF Protection**: Disabled for H2 console, enabled elsewhere
- **Session Management**: Spring Security default session handling
- **Authorization**: Method-level security with `@PreAuthorize`
- **Multi-Provider**: Supports multiple authentication providers simultaneously

## Testing

Run the test suite to verify authentication and authorization:
```bash
mvn test
```

Test classes:
- `FileBasedAuthenticationTest` - File-based auth and role access
- `OAuth2AuthenticationTest` - Google OAuth2 authentication
- `LdapAuthenticationTest` - LDAP authentication
- `FileBasedUserDetailsServiceTest` - User service functionality

## Development

### Running the Application
```bash
mvn spring-boot:run
```

### Building
```bash
mvn clean package
```

### Docker Build
```bash
docker build -t your-namespace/sample-proj .
```

## Troubleshooting

### Common Issues

1. **BCrypt Password Issues**: Ensure passwords in `users.properties` are properly BCrypt encoded
2. **Google OAuth2 Redirect**: Check redirect URI in Google Console matches your application URL
3. **LDAP Connection**: Verify LDAP server connectivity and credentials
4. **Role Access Denied**: Check user roles match endpoint requirements

### Debug Logging
Enable debug logging in `application.properties`:
```properties
logging.level.org.springframework.security=DEBUG
logging.level.com.example.sampleproj=DEBUG
```