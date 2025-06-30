package fr.simplex_software.iam.ai.resources;

import fr.simplex_software.iam.ai.agents.*;
import jakarta.inject.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

@Path("/nlq")
public class KeycloakResource
{
  @Inject
  KeycloakAgent keycloakAgent;

  @POST
  @Path("/ask")
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.TEXT_PLAIN)
  public String askQuestion(String userInput)
  {
    return keycloakAgent.chat(userInput);
  }
}
