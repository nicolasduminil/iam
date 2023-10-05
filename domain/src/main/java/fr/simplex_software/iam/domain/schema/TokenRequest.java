package fr.simplex_software.iam.domain.schema;

import jakarta.json.bind.annotation.*;
import org.eclipse.microprofile.openapi.annotations.media.*;

@Schema(description = "The token request metadata")
public class TokenRequest
{
  @JsonbProperty("grant_type")
  public String grantType;
  @JsonbProperty("authorization_code")
  public String authorizationCode;
  @JsonbProperty("client_id")
  public String clientId;
  @JsonbProperty("redirect_uri")
  public String redirectUri;
}
