package fr.simplex_software.iam.domain.schema;

import org.eclipse.microprofile.openapi.annotations.media.*;

@Schema(description = "The token metadata")
public class Token
{
  public TokenHeader header;
  public TokenPayload payload;
  public String signature;
}
