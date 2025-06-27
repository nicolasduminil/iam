package fr.simplex_software.iam.ai.tools;

import dev.langchain4j.agent.tool.*;
import fr.simplex_software.iam.common.api.*;
import fr.simplex_software.iam.common.api.exceptions.*;
import jakarta.enterprise.context.*;
import jakarta.inject.*;
import org.eclipse.microprofile.config.inject.*;

import java.util.*;

@ApplicationScoped
public class KeycloakDataService
{
  @ConfigProperty(name = "quarkus.keycloak.admin-client.server-url")
  String serverUrl;
  @ConfigProperty(name = "keycloak.data.service.realm")
  String realm;

  @Inject
  Util util;

  @Tool("Returns the number of failed login attempts in the last N days")
  public int countFailedLoginsLastNDays(int days) throws FailedToGetEventException
  {
    return util.countFailedLoginsLastNDays(serverUrl, realm, days);
  }

  @Tool("Returns a list of users assigned to a specific realm role")
  public List<String> getUsersWithRole(String roleName) throws FailedToGetUsersByRole
  {
    return util.getUsersByRole(realm, roleName);
  }

  @Tool("Returns the number of client_credentials token requests for a given client ID in the last N days")
  public int countClientCredentialRequests(String clientId, int days) throws FailedToGetEventException
  {
    return util.countClientCredentialGrants(serverUrl, realm, clientId, days);
  }
}
