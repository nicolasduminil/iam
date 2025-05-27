package fr.simplex_software.iam.domain.schema.request;

import jakarta.ws.rs.core.*;

import java.net.*;
import java.util.*;

public class ResourceOwnerPasswordTokenRequest extends ClientBase implements OAuth20Request
{
  private String userName;
  private String password;
  private String clientSecret;

  public ResourceOwnerPasswordTokenRequest()
  {
    super(GrantType.PASSWORD);
  }

  public ResourceOwnerPasswordTokenRequest(String clientId, List<String> scopes)
  {
    super(clientId, scopes);
    super.setGrantType(GrantType.PASSWORD);
  }

  public ResourceOwnerPasswordTokenRequest(String clientId, GrantType grantType, List<String> scopes, String userName, String password, String clientSecret)
  {
    super(clientId, grantType, scopes);
    this.userName = userName;
    this.password = password;
    this.clientSecret = clientSecret;
  }

  public String getUserName()
  {
    return userName;
  }

  public void setUserName(String userName)
  {
    this.userName = userName;
  }

  public String getPassword()
  {
    return password;
  }

  public void setPassword(String password)
  {
    this.password = password;
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
      .queryParam("scope", String.join(" ", getScopes()))
      .queryParam("username", userName)
      .queryParam("password", password);
    return builder.build();
  }

  public Form toForm()
  {
    return new Form()
      .param("grant_type", getGrantType().toString())
      .param("client_id", this.getClientId())
      .param("client_secret", this.getClientSecret())
      .param("scope", String.join(" ", getScopes()))
      .param("username", getUserName())
      .param("password", password);
  }
}
