package fr.simplex_software.iam.domain.schema;

import org.eclipse.microprofile.openapi.annotations.media.*;

@Schema(description = "The token response metadata")
public class TokenResponse
{
  public String accessToken;
  public String refreshToken;
  public String IdToken;
}
