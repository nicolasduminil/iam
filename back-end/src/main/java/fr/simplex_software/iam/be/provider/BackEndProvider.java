package fr.simplex_software.iam.be.provider;

import fr.simplex_software.iam.common.api.*;
import fr.simplex_software.iam.domain.schema.*;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.config.inject.*;

public class BackEndProvider implements BackEndAPI
{
  @ConfigProperty(name = "keycloak.server-url")
  private String serverUrl;
  @ConfigProperty(name = "keycloak.issuer-uri")
  private String issuerUri;
  @ConfigProperty(name = "keycloak.authorization-uri")
  private String authorizationUri;
  private WebTarget webTarget = ClientBuilder.newClient().target(serverUrl);

  @Override
  public Response loadDiscoveryMetadata()
  {
    return Response.ok(webTarget.path(issuerUri).request(MediaType.APPLICATION_JSON).get(DiscoveryMetadata.class)).build();
  }

  @Override
  public Response sendAuthenticationRequest(AuthenticationInput authenticationInput)
  {
    return Response.ok(webTarget.path(authorizationUri).request(MediaType.APPLICATION_JSON).get(String.class)).build();
  }

  @Override
  public Response sendTokenRequest(TokenRequest tokenRequest)
  {
    return null;
  }

  @Override
  public Response sendRefreshRequest(TokenRequest tokenRequest)
  {
    return null;
  }

  @Override
  public Response sendUserInfoRequest()
  {
    return null;
  }
}
