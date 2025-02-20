package fr.simplex_software.iam.be.provider;

import fr.simplex_software.iam.common.api.*;
import fr.simplex_software.iam.domain.schema.*;
import jakarta.annotation.*;
import jakarta.enterprise.context.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.config.inject.*;

@RequestScoped
@Path("/api/be")
public class BackEndProvider implements BackEndAPI
{
  @ConfigProperty(name = "keycloak.server-url")
  String serverUrl;
  private WebTarget webTarget;
  @ConfigProperty(name = "keycloak.authorization-uri-fmt")
  String fmt;

  @PostConstruct
  public void postConstruct()
  {
    webTarget = ClientBuilder.newClient().target(serverUrl);
  }

  @Override
  public Response loadDiscoveryMetadata(String issuerUri)
  {
    return Response.ok(webTarget.path(issuerUri).request(MediaType.APPLICATION_JSON).get(String.class)).build();
  }

  @Override
  public Response sendAuthorizationRequest(AuthorizationRequest authorizationRequest)
  {
    System.out.println ("### authorizationUri: " + authorizationRequest.authorizationUri + " client_id " + authorizationRequest.clientId
      + " response_type " + authorizationRequest.responseType + " redirectUri " + authorizationRequest.redirectUri + " scope " + authorizationRequest.scope);
    /*System.out.println ("### FMT: " + fmt);
    String url = String.format(fmt, clientId, respnseType, redirectUri);
    System.out.println ("### URL: " + url);
    return Response.status(Response.Status.SEE_OTHER)
      .header(HttpHeaders.LOCATION, url)
      .build();*/
    return Response.ok(webTarget.path(authorizationRequest.authorizationUri).queryParam("client_id", authorizationRequest.clientId)
      .queryParam("response_type", authorizationRequest.responseType).queryParam("redirect_uri", authorizationRequest.redirectUri)
      .queryParam("scope", "openid").request(MediaType.APPLICATION_JSON).get(String.class)).build();

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
