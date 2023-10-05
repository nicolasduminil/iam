package fr.simplex_software.iam.domain.schema;

import jakarta.json.bind.annotation.*;
import org.eclipse.microprofile.openapi.annotations.media.*;

@Schema(description = "The authorization request metadata")
public class AuthenticationRequest
{
  @JsonbProperty("authentication_input")
  public AuthenticationInput authenticationInput;
  @JsonbProperty("response_type")
  public String responseType;
  @JsonbProperty("redirect_uri")
  public String redirectUri;
}
