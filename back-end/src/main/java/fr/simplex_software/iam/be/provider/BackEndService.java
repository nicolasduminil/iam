package fr.simplex_software.iam.be.provider;

import jakarta.annotation.security.*;
import jakarta.enterprise.context.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

@ApplicationScoped
@Path("be")
@Produces(MediaType.TEXT_PLAIN)
public class BackEndService
{
  @GET
  @Path("secured")
  @RolesAllowed({"user", "manager"})
  public Response getSecuredMessage()
  {
    return Response.ok().entity("Secured message").build();
  }

  @GET
  @Path("public")
  @PermitAll
  public Response getPublicMessage()
  {
    return Response.ok().entity("Public message").build();
  }
}
