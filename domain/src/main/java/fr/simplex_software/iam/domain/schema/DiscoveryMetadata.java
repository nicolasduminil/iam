package fr.simplex_software.iam.domain.schema;

import jakarta.json.bind.annotation.*;
import org.eclipse.microprofile.openapi.annotations.media.*;

import java.util.*;

@Schema(description = "The discovery metadata")
public class DiscoveryMetadata implements AutoCloseable
{
  public String issuer;
  @JsonbProperty("authorization_endpoint")
  public String authorizationEndpoint;
  @JsonbProperty("token_endpoint")
  public String tokenEndpoint;
  @JsonbProperty("introspection_endpoint")
  public String introspectionEndpoint;
  @JsonbProperty("userinfo_endpoint")
  public String userInfoEndpoint;
  @JsonbProperty("end_session_endpoint")
  public String endSessionEndpoint;
  @JsonbProperty("front_channel_logout_session_suported")
  public boolean frontChannelLogoutSessionSupported;
  @JsonbProperty("front_channel_logout_supported")
  public boolean frontChannelLogoutSupported;
  @JsonbProperty("jwks_uri")
  public String jwksUri;
  @JsonbProperty("check_session_iframe")
  public String checkSessionIframe;
  @JsonbProperty("grant_types_supported")
  public List<String> grantTypesSupported;

  @Override
  public void close() {}
}
