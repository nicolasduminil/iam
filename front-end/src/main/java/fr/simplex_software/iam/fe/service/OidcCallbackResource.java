package fr.simplex_software.iam.fe.service;

import jakarta.enterprise.context.*;
import jakarta.inject.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

@ApplicationScoped
@Path("/callback")
public class OidcCallbackResource
{
  @Inject
  OidcService oidcService;

  @GET
  public Response handleCallback(@QueryParam("code") String code)
  {
    oidcService.setAuthCode(code);
    return Response.temporaryRedirect(UriBuilder.fromPath("index.xhtml")
      .queryParam("activeIndex", "1").build()).build();
  }
}
