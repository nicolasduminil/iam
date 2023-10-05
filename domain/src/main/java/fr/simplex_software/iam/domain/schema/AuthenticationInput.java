package fr.simplex_software.iam.domain.schema;

import jakarta.json.bind.annotation.*;
import org.eclipse.microprofile.openapi.annotations.media.*;

@Schema(description = "The authentication input metadata")
public class AuthenticationInput
{
  @JsonbProperty("client_id")
  public String clientId;
  public String scope;
  public String prompt;
  @JsonbProperty("max_age")
  public String maxAge;
  @JsonbProperty("login_hint")
  public String loginHint;
}
