package fr.simplex_software.iam.domain.schema;

import jakarta.json.bind.annotation.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.openapi.annotations.media.*;

import java.net.*;

@Schema(description = "The token request metadata")
public class TokenRequest
{
  @JsonbProperty("grant_type")
  private String grantType;
  @JsonbProperty("authorization_code")
  private String authorizationCode;
  @JsonbProperty("client_id")
  private String clientId;
  @JsonbProperty("scope")
  private String scope;
  @JsonbProperty("redirect_uri")
  private String redirectUri;

  public TokenRequest()
  {
  }

  public TokenRequest(String grantType, String authorizationCode, String clientId, String scope, String redirectUri)
  {
    this.grantType = grantType;
    this.authorizationCode = authorizationCode;
    this.scope = scope;
    this.clientId = clientId;
    this.redirectUri = redirectUri;
  }

  public TokenRequest(OidcAuthenticationRequest oidcAuthenticationRequest, String authorizationCode)
  {
    this.grantType = "authorization_code";
    this.authorizationCode = authorizationCode;
    this.scope = oidcAuthenticationRequest.getScope();
    this.clientId = oidcAuthenticationRequest.getClientId();
    this.redirectUri = oidcAuthenticationRequest.getRedirectUri();
  }

  public TokenRequest(Oauth20AuthorizationRequest oauth20AuthorizationRequest, String authorizationCode)
  {
    this.grantType = "authorization_code";
    this.authorizationCode = authorizationCode;
    this.clientId = oauth20AuthorizationRequest.getClientId();
    this.redirectUri = oauth20AuthorizationRequest.getRedirectUri();
  }


  public String getGrantType()
  {
    return grantType;
  }

  public String getClientId()
  {
    return clientId;
  }

  public String getRedirectUri()
  {
    return redirectUri;
  }

  public String getScope()
  {
    return scope;
  }

  public URI buildTokenUri(String tokenEndpoint)
  {
    UriBuilder builder = UriBuilder.fromUri(tokenEndpoint)
      .queryParam("grant_type", grantType)
      .queryParam("code", authorizationCode.substring(0, 10) + "...")
      .queryParam("client_id", clientId)
      .queryParam("scope", scope)
      .queryParam("redirect_uri", redirectUri);
    return builder.build();
  }

  public Form toForm()
  {
    return new Form()
      .param("grant_type", this.getGrantType())
      .param("code", this.authorizationCode)
      .param("client_id", this.getClientId())
      .param("scope", this.getScope())
      .param("redirect_uri", this.getRedirectUri());
  }
}