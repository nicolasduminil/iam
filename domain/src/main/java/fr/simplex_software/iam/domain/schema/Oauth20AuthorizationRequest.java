package fr.simplex_software.iam.domain.schema;

import io.smallrye.config.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.openapi.annotations.media.*;

import java.io.Serializable;
import java.net.*;

@ConfigMapping(prefix = "oauth2")
@Schema(description = "The authorization request metadata")
public class Oauth20AuthorizationRequest implements Serializable
{
  @Schema(required = true)
  @WithName("client.id")
  private String clientId;
  @Schema(required = true)
  @WithName("client.redirectUri")
  @WithDefault("##placehorlder##")
  private String redirectUri;
  @Schema(required = true)
  @WithName("client.responseType")
  @WithDefault("code")
  private String responseType;

  public Oauth20AuthorizationRequest()
  {
  }

  public Oauth20AuthorizationRequest(String clientId, String redirectUri, String responseType)
  {
    this.clientId = clientId;
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

  public URI buildAuthorizationUri(String authorizationEndpoint)
  {
    UriBuilder builder = UriBuilder.fromUri(authorizationEndpoint)
      .queryParam("client_id", getClientId())
      .queryParam("response_type", getResponseType())
      .queryParam("redirect_uri", getRedirectUri());
    return builder.build();
  }
}
