package fr.simplex_software.iam.domain.schema.request;

import jakarta.ws.rs.core.*;

import java.net.*;
import java.util.*;

public class ClientWithRedirectUri extends ClientBase
{
  private String redirectUri;

  public ClientWithRedirectUri()
  {
  }

  public ClientWithRedirectUri(GrantType grantType)
  {
    super(grantType);
  }

  public ClientWithRedirectUri(String clientId, List<String> scopes)
  {
    super(clientId, scopes);
  }

  public ClientWithRedirectUri(String clientId, List<String> scopes, String redirectUri)
  {
    this(clientId, scopes);
    this.redirectUri = redirectUri;
  }

  public ClientWithRedirectUri(String clientId,
    GrantType grantType, List<String> scopes, String redirectUri)
  {
    super(clientId, grantType, scopes);
    this.redirectUri = redirectUri;
  }

  public String getRedirectUri()
  {
    return redirectUri;
  }

  public void setRedirectUri(String redirectUri)
  {
    this.redirectUri = redirectUri;
  }

  public URI buildAuthUri(String authorizationEndpoint)
  {
    if (!getScopes().contains("opedid"))
      getScopes().add("openid");
    UriBuilder builder = UriBuilder.fromUri(authorizationEndpoint)
      .queryParam("client_id", getClientId())
      .queryParam("redirect_uri", getRedirectUri())
      .queryParam("scope", String.join(" ", getScopes()));
    return builder.build();
  }
}
