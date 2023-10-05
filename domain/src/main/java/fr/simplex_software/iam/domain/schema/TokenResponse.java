package fr.simplex_software.iam.domain.schema;

import jakarta.json.bind.annotation.*;
import org.eclipse.microprofile.openapi.annotations.media.*;

import java.util.*;

@Schema(description = "The token response metadata")
public class TokenResponse
{
  @JsonbProperty("access-token")
  public Token accessToken;
  @JsonbProperty("expires-in")
  public int expiresIn;
  @JsonbProperty("refresh-token")
  public Token refreshToken;
  @JsonbProperty("token-type")
  public TokenType tokenType;
  @JsonbProperty("id-token")
  public Token IdToken;
  @JsonbProperty("not-before-policy")
  public int notBeforePolicy;
  @JsonbProperty("session-state")
  public String sessionState;
  public List<String> scope;
}
