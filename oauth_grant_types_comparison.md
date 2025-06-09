# OAuth 2.0 Grant Types Comparison

## Comparison Table

| Aspect | Client Credentials | Authorization Code | Resource Owner Password |
|--------|-------------------|-------------------|------------------------|
| **Flow Type** | Machine-to-machine | User-centric with redirection | Direct user credentials |
| **User Involvement** | None | Interactive consent | Provides credentials directly |
| **Client Type** | Confidential | Public or confidential | Confidential |
| **Token Delivery** | Direct to client | Via browser redirect | Direct to client |
| **Security Level** | High | Highest | Medium |
| **Use Cases** | Backend services, APIs | Web apps, mobile apps, SPAs | First-party apps with high trust |
| **User Credentials** | Not involved | Not exposed to client | Exposed to client |
| **Refresh Token** | Typically not issued | Commonly issued | Commonly issued |

## Flow Diagrams

### Client Credentials
```mermaid
sequenceDiagram
    participant C as Client
    participant AS as Authorization Server
    participant RS as Resource Server

    C->>AS: 1. POST /token<br>grant_type=client_credentials<br>client_id=id&client_secret=secret
    AS->>AS: 2. Validates client credentials
    AS->>C: 3. Returns access token
    C->>RS: 4. API request with access token
    RS->>RS: 5. Validates token
    RS->>C: 6. Returns protected resource
```

### Authorization Code
```mermaid
sequenceDiagram
    participant U as User
    participant UA as User Agent (Browser)
    participant C as Client
    participant AS as Authorization Server
    participant RS as Resource Server

    U->>C: 1. Initiates flow
    C->>UA: 2. Redirects to AS<br>/authorize?response_type=code&client_id=id&redirect_uri=uri&scope=scope&state=state
    UA->>AS: 3. Redirects to authorization endpoint
    AS->>U: 4. Authentication & consent
    U->>AS: 5. Approves authorization
    AS->>UA: 6. Redirects with authorization code<br>redirect_uri?code=code&state=state
    UA->>C: 7. Returns authorization code
    C->>AS: 8. POST /token<br>grant_type=authorization_code&code=code&redirect_uri=uri&client_id=id&client_secret=secret
    AS->>C: 9. Returns access token
    C->>RS: 10. API request with access token
    RS->>C: 11. Returns protected resource
```

### Resource Owner Password
```mermaid
sequenceDiagram
    participant RO as Resource Owner (User)
    participant C as Client
    participant AS as Authorization Server
    participant RS as Resource Server

    RO->>C: 1. Provides username & password
    C->>AS: 2. POST /token<br>grant_type=password<br>username=user&password=pwd<br>client_id=id&client_secret=secret
    AS->>AS: 3. Validates credentials
    AS->>C: 4. Returns access token
    C->>RS: 5. API request with access token
    RS->>C: 6. Returns protected resource
```

## Key Differences

1. **Client Credentials**
   - No user involvement - purely for machine-to-machine communication
   - Client authenticates itself to get access to its own resources
   - Simplest flow with fewest steps
   - No refresh tokens typically issued

2. **Authorization Code**
   - Most secure flow with complete separation of concerns
   - User explicitly consents to authorization
   - Front-channel and back-channel communication
   - Supports PKCE extension for public clients
   - Most complex flow with most steps

3. **Resource Owner Password**
   - Client directly collects user credentials
   - No redirection or user consent UI
   - Requires high trust between user and client
   - Not recommended for third-party applications

## Recommendations

- **Client Credentials**: Use for service accounts, daemon processes, or backend-to-backend communication
- **Authorization Code**: Preferred for most user-facing applications, especially third-party applications
- **Resource Owner Password**: Use only for first-party applications with high trust, legacy systems, or when other flows aren't viable