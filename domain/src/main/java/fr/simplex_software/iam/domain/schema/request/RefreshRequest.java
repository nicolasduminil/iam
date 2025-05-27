package fr.simplex_software.iam.domain.schema.request;

import jakarta.ws.rs.core.*;

import java.net.*;
import java.util.*;

public class RefreshRequest extends ClientBase
{
  private String refreshToken;
  private String clientSecret;

  public RefreshRequest(String clientId, List<String> scopes)
  {
    super(clientId, scopes);
    super.setGrantType(GrantType.REFRESH_TOKEN);
  }

  public RefreshRequest(String clientId, List<String> scopes, String refreshToken)
  {
    this(clientId, scopes);
    super.setGrantType(GrantType.REFRESH_TOKEN);
    this.refreshToken = refreshToken;
  }

  public RefreshRequest(String clientId, GrantType grantType, List<String> scopes, String refreshToken, String clientSecret)
  {
    super(clientId, grantType, scopes);
    this.refreshToken = refreshToken;
    this.clientSecret = clientSecret;
  }

  public String getRefreshToken()
  {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken)
  {
    this.refreshToken = refreshToken;
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
    UriBuilder builder = UriBuilder.fromUri(tokenEndpoint)
      .queryParam("grant_type", getGrantType().toString())
      .queryParam("refresh_token", refreshToken.substring(0, 10) + "...")
      .queryParam("client_id", getClientId())
      .queryParam("client_secret", getClientSecret())
      .queryParam("scope", String.join(" ", getScopes()));
    return builder.build();
  }

  public Form toForm()
  {
    return new Form()
      .param("grant_type", getGrantType().toString())
      .param("refresh_token", refreshToken)
      .param("client_id", getClientId())
      .param("client_secret", getClientSecret())
      .param("scope", String.join(" ", getScopes()));
  }
}
