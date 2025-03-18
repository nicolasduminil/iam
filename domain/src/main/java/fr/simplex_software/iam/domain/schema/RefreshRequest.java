package fr.simplex_software.iam.domain.schema;

import jakarta.json.bind.annotation.*;
import org.eclipse.microprofile.openapi.annotations.media.*;

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
}
