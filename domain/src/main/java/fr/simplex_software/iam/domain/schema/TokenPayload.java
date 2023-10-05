package fr.simplex_software.iam.domain.schema;

import jakarta.json.bind.annotation.*;
import org.eclipse.microprofile.openapi.annotations.media.*;

@Schema(description = "The token payload metadata")
public class TokenPayload
{
  public String exp;
  public String iat;
  @JsonbProperty("auth_time")
  public String authTime;
  public String jti;
  public String iss;
  public String aud;
  public String sub;
  public String typ;
  public String azp;
  @JsonbProperty("session_state")
  public String sessionState;
  @JsonbProperty("at_hash")
  public String atHash;
  public String acr;
  public String sid;
  @JsonbProperty("email_verified")
  public boolean emailVerified;
  @JsonbProperty("preferred_user_name")
  public String preferredUserName;
}
