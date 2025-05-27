package fr.simplex_software.iam.be.provider;

import io.quarkus.security.identity.*;
import jakarta.annotation.security.*;
import jakarta.enterprise.context.*;
import jakarta.inject.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.slf4j.*;

@ApplicationScoped
@Path("be")
@Produces(MediaType.TEXT_PLAIN)
public class BackEndService
{
  private final static Logger log = LoggerFactory.getLogger(BackEndService.class);
  @Inject
  SecurityIdentity securityIdentity;

  @GET
  @Path("secured")
  @RolesAllowed("manager")
  public Response getSecuredMessage()
  {
    log.info("Principal: %s".formatted(securityIdentity.getPrincipal().getName()));
    log.info("Roles: %s".formatted(securityIdentity.getRoles()));
    log.info("Is Anonymous: " + securityIdentity.isAnonymous());
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
