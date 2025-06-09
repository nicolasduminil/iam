# Resource Owner Password Credentials Grant Flow

```mermaid
sequenceDiagram
    participant RO as Resource Owner (User)
    participant C as Client Application
    participant AS as Authorization Server
    participant RS as Resource Server

    RO->>C: 1. Provides username & password
    C->>AS: 2. POST /token<br>grant_type=password<br>username=user&password=pwd<br>client_id=client&client_secret=secret
    AS->>AS: 3. Validates credentials
    AS->>C: 4. Returns access token<br>{access_token, token_type, expires_in, refresh_token}
    C->>RS: 5. API request with access token<br>Authorization: Bearer token
    RS->>RS: 6. Validates token
    RS->>C: 7. Returns protected resource
```

## Flow Description

1. **Resource Owner provides credentials**: The user provides their username and password directly to the client application.

2. **Token Request**: The client sends these credentials along with its own identification to the authorization server.

3. **Validation**: The authorization server validates the resource owner's credentials and the client's authentication.

4. **Token Response**: If valid, the authorization server issues an access token (and optionally a refresh token).

5. **Resource Request**: The client uses the access token to request the protected resource.

6. **Token Validation**: The resource server validates the access token.

7. **Resource Response**: If the token is valid, the resource server provides the requested resource.

## Important Notes

- This grant type should only be used when there is a high degree of trust between the resource owner and the client.
- The client must be capable of securely storing the resource owner's credentials.
- This flow is not recommended for third-party applications as it requires direct handling of user credentials.
- Modern applications typically prefer authorization code flow with PKCE for better security.