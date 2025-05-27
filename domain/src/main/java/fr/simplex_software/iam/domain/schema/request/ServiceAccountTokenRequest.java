package fr.simplex_software.iam.domain.schema.request;

import jakarta.ws.rs.core.*;

import java.net.*;
import java.util.*;

public class ServiceAccountTokenRequest extends ClientBase implements OAuth20Request
{
  private String clientSecret;

  public ServiceAccountTokenRequest()
  {
    super(GrantType.CLIENT_CREDENTIALS);
  }

  public ServiceAccountTokenRequest(String clientId, GrantType grantType, List<String> scopes, String clientSecret)
  {
    super(clientId, grantType, scopes);
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
    UriBuilder builder = UriBuilder.fromUri(tokenEndpoint)
      .queryParam("grant_type", getGrantType())
      .queryParam("client_id", getClientId())
      .queryParam("client_secret", getClientSecret())
      .queryParam("scope", String.join(" ", getScopes()));
    return builder.build();
  }

  public Form toForm()
  {
    return new Form()
      .param("grant_type", getGrantType().toString())
      .param("client_id", this.getClientId())
      .param("client_secret", this.getClientSecret())
      .param("scope", String.join(" ", getScopes()));
  }
}
