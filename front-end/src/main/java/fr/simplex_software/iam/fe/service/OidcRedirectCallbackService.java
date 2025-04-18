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

  @ConfigProperty(name = "iam-frontend.error-redirect", defaultValue = "")
  String errorRedirect;

  private String authCode;

  @GET
  public Response handleCallback(@QueryParam("code") String code,
                                 @QueryParam("error") String error,
                                 @QueryParam("error_description") String errorDescription)
  {
    Response response = null;
    if (error != null)
    {
      switch (error)
      {
        case "access_denied":
          response = Response.temporaryRedirect(UriBuilder.fromPath(errorRedirect)
            .queryParam("error", "access_denied")
            .queryParam("message", "Access was denied")
            .build()).build();
          break;
        case "invalid_request":
          response = Response.temporaryRedirect(UriBuilder.fromPath(errorRedirect)
            .queryParam("error", "invalid_request")
            .queryParam("message", "An invalid request has been received")
            .build()).build();
          break;
        default:
          response = Response.temporaryRedirect(UriBuilder.fromPath(errorRedirect)
            .queryParam("error", "login_failure")
            .queryParam("message",
              "The login has failed. Consider adjusting the\"prompt\" parameter")
            .build()).build();
      }
    }
    else
    {
      authCode = code;
      response = Response.temporaryRedirect(UriBuilder.fromPath(sandBoxRedirect)
        .queryParam("activeIndex", "1").build()).build();
    }
    return response;
  }

  public String getAuthCode()
  {
    return authCode;
  }
}
