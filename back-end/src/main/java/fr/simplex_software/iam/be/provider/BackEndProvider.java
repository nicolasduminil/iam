package fr.simplex_software.iam.be.provider;

import fr.simplex_software.iam.common.api.*;
import fr.simplex_software.iam.domain.schema.*;
import jakarta.annotation.*;
import jakarta.enterprise.context.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.config.inject.*;

import java.net.*;

@ApplicationScoped
@Path("/api/be")
public class BackEndProvider implements BackEndAPI
{
  @ConfigProperty(name = "keycloak.server-url")
  String serverUrl;
  @ConfigProperty(name = "keycloak.issuer-uri")
  String issuerUri;
  @ConfigProperty(name = "keycloak.authorization-uri")
  String authorizationUri;
  private WebTarget webTarget;

  @PostConstruct
  public void postConstruct()
  {
    webTarget = ClientBuilder.newClient().target(serverUrl);
  }

  @Override
  public Response loadDiscoveryMetadata()
  {
    /*System.out.println ("### issuerURI: " + issuerUri);
    WebTarget path = webTarget.path(issuerUri);
    System.out.println ("### path: " + path);
    URI uri = path.getUri();
    System.out.println ("### URI: " + uri.toString());*/
    //String str = path.request(MediaType.APPLICATION_JSON).get(String.class);
    //ClientBuilder.newClient();
    /*String str = "ResponseResponseResponse";
    System.out.println ("### Response: " + str);*/
    return Response.ok(webTarget.path(issuerUri).request(MediaType.APPLICATION_JSON).get(String.class)).build();
    /*Response response = Response.ok(str).build();
    System.out.println ("### Returning: " + response.readEntity(String.class));
    return response;*/
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
