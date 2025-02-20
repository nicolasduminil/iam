package fr.simplex_software.iam.domain.schema;

import jakarta.json.bind.annotation.*;
import org.eclipse.microprofile.openapi.annotations.media.*;

@Schema(description = "The authorization input metadata")
public class AuthorizationRequest
{
  @JsonbProperty("client_id")
  public String clientId;
  @JsonbProperty("response_type")
  public String responseType;
  public String scope;
  @JsonbProperty("redirect_uri")
  public String redirectUri;
  @JsonbProperty("authorization_uri")
  public String authorizationUri;

  public AuthorizationRequest(String clientId, String responseType, String scope, String redirectUri, String authorizationUri)
  {
    this.clientId = clientId;
    this.responseType = responseType;
    this.scope = scope;
    this.redirectUri = redirectUri;
    this.authorizationUri = authorizationUri;
  }
}
