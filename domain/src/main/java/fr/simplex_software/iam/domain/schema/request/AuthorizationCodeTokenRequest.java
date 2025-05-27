package fr.simplex_software.iam.domain.schema.request;

import jakarta.ws.rs.core.*;

import java.net.*;
import java.util.*;

public class AuthorizationCodeTokenRequest extends ClientWithRedirectUri implements OAuth20Request
{
  private String authorizationCode;
  private String clientSecret;

  public AuthorizationCodeTokenRequest()
  {
    super(GrantType.AUTHORIZATION_CODE);
  }

  public AuthorizationCodeTokenRequest(String clientId, List<String> scopes, String authorizationCode, String redirectUri)
  {
    super(clientId, scopes, redirectUri);
    this.authorizationCode = authorizationCode;
    super.setGrantType(GrantType.AUTHORIZATION_CODE);
  }

  public AuthorizationCodeTokenRequest(AuthorizationCodeLoginRequest authRequest, String authorizationCode)
  {
    this(authRequest.getClientId(), authRequest.getScopes(),
      authorizationCode, authRequest.getRedirectUri());
    super.setGrantType(GrantType.AUTHORIZATION_CODE);
  }

  public AuthorizationCodeTokenRequest(AuthorizationCodeLoginRequest authRequest, String authorizationCode, String clientSecret)
  {
    this(authRequest.getClientId(), authRequest.getScopes(),
      authorizationCode, authRequest.getRedirectUri());
    super.setGrantType(GrantType.AUTHORIZATION_CODE);
    this.clientSecret = clientSecret;
  }

  public AuthorizationCodeTokenRequest(String clientId, GrantType grantType, List<String> scopes, String redirectUri, String authorizationCode, String clientSecret)
  {
    super(clientId, grantType, scopes, redirectUri);
    this.authorizationCode = authorizationCode;
    this.clientSecret = clientSecret;
  }

  public String getClientSecret()
  {
    return clientSecret;
  }

  public void setClientSecret(String clientSecret)
  {
    this.clientSecret = clientSecret;
  }

  public URI buildTokenUri(String tokenEndpoint)
  {
    UriBuilder builder = UriBuilder.fromUri(super.buildAuthUri(tokenEndpoint))
      .queryParam("client_secret", clientSecret)
      .queryParam("code", authorizationCode.substring(0, 10) + "...");
    return builder.build();
  }

  public Form toForm()
  {
    return new Form()
      .param("grant_type", getGrantType().toString())
      .param("code", this.authorizationCode)
      .param("client_id", this.getClientId())
      .param("client_secret", this.clientSecret)
      .param("scope", String.join(" ", getScopes()))
      .param("redirect_uri", this.getRedirectUri());
  }
}