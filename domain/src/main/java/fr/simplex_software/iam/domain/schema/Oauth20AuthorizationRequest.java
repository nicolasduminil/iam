package fr.simplex_software.iam.domain.schema;

import jakarta.ws.rs.core.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class Oauth20AuthorizationRequest implements Serializable
{
  private String clientId;
  private String redirectUri;
  private String responseType;
  private List<String> scopes = new ArrayList<>();

  public Oauth20AuthorizationRequest()
  {
    scopes.add("openid");
  }

  public Oauth20AuthorizationRequest(String clientId, String redirectUri, String responseType)
  {
    this();
    this.clientId = clientId;
    this.redirectUri = redirectUri;
    this.responseType = responseType;
  }

  public Oauth20AuthorizationRequest(String clientId, List<String> scopes, String redirectUri, String responseType)
  {
    this();
    this.clientId = clientId;
    this.scopes.addAll(scopes);
    this.redirectUri = redirectUri;
    this.responseType = responseType;
  }


  public String getClientId()
  {
    return clientId;
  }

  public void setClientId(String clientId)
  {
    this.clientId = clientId;
  }

  public String getRedirectUri()
  {
    return redirectUri;
  }

  public void setRedirectUri(String redirectUri)
  {
    this.redirectUri = redirectUri;
  }

  public String getResponseType()
  {
    return responseType;
  }

  public void setResponseType(String responseType)
  {
    this.responseType = responseType;
  }

  public List<String> getScopes()
  {
    return scopes;
  }

  public void setScopes(List<String> scopes)
  {
    this.scopes = new ArrayList<>(scopes);
  }

  public URI buildAuthorizationUri(String authorizationEndpoint)
  {
    if (!scopes.contains("opedid"))
      scopes.add("openid");
    UriBuilder builder = UriBuilder.fromUri(authorizationEndpoint)
      .queryParam("client_id", getClientId())
      .queryParam("response_type", getResponseType())
      .queryParam("redirect_uri", getRedirectUri())
      .queryParam("scope", String.join(" ", scopes));
    return builder.build();
  }
}
