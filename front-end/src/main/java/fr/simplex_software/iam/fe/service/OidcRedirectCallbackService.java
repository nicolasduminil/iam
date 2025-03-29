package fr.simplex_software.iam.fe.service;

import jakarta.enterprise.context.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.config.inject.*;

@ApplicationScoped
@Path("/callback")
public class OidcRedirectCallbackService
{
  @ConfigProperty(name = "iam-frontend.sandbox-redirect")
  String sandBoxRedirect;

  private String authCode;

  @GET
  public Response handleCallback(@QueryParam("code") String code)
  {
    authCode = code;
    return Response.temporaryRedirect(UriBuilder.fromPath(sandBoxRedirect)
      .queryParam("activeIndex", "1").build()).build();
  }

  public String getAuthCode()
  {
    return authCode;
  }
}
