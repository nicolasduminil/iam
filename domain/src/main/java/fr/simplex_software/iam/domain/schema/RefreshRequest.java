package fr.simplex_software.iam.domain.schema;

import jakarta.json.bind.annotation.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.openapi.annotations.media.*;

import java.net.*;

@Schema(description = "The refresh request metadata")
public class RefreshRequest
{
  @JsonbProperty("grant_type")
  private String grantType;
  @JsonbProperty("refresh_token")
  private String refreshToken;
  @JsonbProperty("client_id")
  private String clientId;
  @JsonbProperty("scope")
  private String scope;

  public RefreshRequest()
  {
  }

  public RefreshRequest(String grantType, String refreshToken, String clientId, String scope)
  {
    this.grantType = grantType;
    this.refreshToken = refreshToken;
    this.clientId = clientId;
    this.scope = scope;
  }

  public String getGrantType()
  {
    return grantType;
  }

  public String getRefreshToken()
  {
    return refreshToken;
  }

  public String getClientId()
  {
    return clientId;
  }

  public String getScope()
  {
    return scope;
  }

  public URI buildTokenUri(String tokenEndpoint)
  {
    UriBuilder builder = UriBuilder.fromUri(tokenEndpoint)
      .queryParam("grant_type", grantType)
      .queryParam("refresh_token", refreshToken.substring(0, 10) + "...")
      .queryParam("client_id", clientId)
      .queryParam("scope", scope);
    return builder.build();
  }

  public Form toForm()
  {
    return new Form()
      .param("grant_type", this.getGrantType())
      .param("refresh_token", this.refreshToken)
      .param("client_id", this.getClientId())
      .param("scope", this.getScope());
  }
}
