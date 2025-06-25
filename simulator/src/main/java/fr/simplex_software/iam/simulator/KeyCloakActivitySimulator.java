package fr.simplex_software.iam.simulator;

import fr.simplex_software.iam.common.api.*;
import fr.simplex_software.iam.common.api.exceptions.*;
import fr.simplex_software.iam.domain.schema.data.*;
import fr.simplex_software.iam.domain.schema.request.*;
import io.quarkus.runtime.*;
import jakarta.annotation.*;
import jakarta.enterprise.context.*;
import jakarta.inject.*;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.config.inject.*;

import java.util.*;
import java.util.stream.*;

@Startup
@ApplicationScoped
public class KeyCloakActivitySimulator
{
  @ConfigProperty(name = "quarkus.oidc.token.issuer")
  String issuer;
  @ConfigProperty(name = "keycloak.activity.simulator.realm")
  String realm;
  @ConfigProperty(name = "keycloak.activity.simulator.client_id")
  String clientId;
  private String clientSecret;
  @ConfigProperty(name = "keycloak.discovery.endpoint")
  String discoveryEndpoint;
  private DiscoveryData discoveryData = new DiscoveryData();
  private List<UserData> userDataList = List.of(
    new UserData("user1", "jack.doe@email.com", "jack",
      "doe", "password1", new ArrayList<>(List.of("user"))),
    new UserData("user2", "jim.doe@email.com", "jim",
      "doe", "password1", new ArrayList<>(List.of("user"))),
    new UserData("user3", "jeff.doe@email.com", "jeff",
      "doe", "password1", new ArrayList<>(List.of("user"))),
    new UserData("user4", "janice.doe@email.com", "janice",
      "doe", "password1", new ArrayList<>(List.of("user"))),
    new UserData("user5", "jennifer.doe@email.com", "jannifer",
      "doe", "password1", new ArrayList<>(List.of("user")))
  );
  @Inject
  Util util;
  @Inject
  ClientManager clientManager;

  @PostConstruct
  public void run()
  {
    discoveryData.setDiscovery(clientManager.getClient()
      .target(issuer + discoveryEndpoint)
      .request(MediaType.APPLICATION_JSON)
      .get(new GenericType<>() {}));
    clientSecret = util.getClientSecret(realm, clientId);
    createUsers(userDataList);
    simulateLoginActivity(userDataList);
    assignRolesToUsers(new RolePairData("user", "manager"), userDataList);
    simulateClientCredentialsFlow(clientId, clientSecret, 10);
    Quarkus.asyncExit(0);
  }

  private void createUsers(List<UserData> users)
  {
    users.forEach(user ->
    {
      try
      {
        util.createRealmUser(realm, user);
        System.out.println("✅ Created user %s".formatted(user.username()));
      }
      catch (UserCreationException e)
      {
        System.out.println("❌ User %s alrady exists".formatted(user.username()));
      }
    });
  }

  private void assignRolesToUsers(RolePairData rolePair, List<UserData> users)
  {
    IntStream.range(0, 50).forEach(i ->
    {
      UserData userData = users.get(new Random().nextInt(users.size()));
      String roleName;
      roleName = Math.random() < 0.2 ? "wrongrole" :
        Math.random() > 0.5 ? rolePair.first() : rolePair.second();
      userData.roles().add(roleName);
      try
      {
        util.assignRoleToUser(realm, util.getRealmUser(realm, userData.username()).getId(),
          util.getRealmRole(realm, roleName));
        System.out.println("✅ Assigned role %s to user %s".formatted(roleName, userData.username()));
      }
      catch (Exception ex)
      {
        System.out.println("❌ Failed to assign role %s to user %s".formatted(roleName, userData.username()));
      }
    });
  }

  private void simulateLoginActivity(List<UserData> users)
  {
    IntStream.range(0, 50).forEach(i ->
    {
      UserData userData = users.get(new Random().nextInt(users.size()));
      String user = users.get(new Random().nextInt(users.size())).username();
      simulateLogin(userData.username(), userData.password());
      if (Math.random() < 0.2)
        simulateLogin(user, "wrongpassword");
    });
    System.out.println("✅ Simulated login activity complete.");
  }

  private void simulateLogin(String username, String password)
  {
    ResourceOwnerPasswordTokenRequest tokenRequest =
      new ResourceOwnerPasswordTokenRequest(clientId, username, password, clientSecret);
    tokenRequest.setClientSecret(clientSecret);
    String tokenEndpoint = (String) discoveryData.getDiscovery().get("token_endpoint");
    Response response = clientManager.getClient().target(tokenEndpoint)
      .request(MediaType.APPLICATION_JSON)
      .post(Entity.form(tokenRequest.toForm()));
    System.out.println(response.getStatus() == 200 ?
      "✅ User %s logged in.".formatted(username) :
      "❌ User %s has failed to login with password %s, status code: %d"
        .formatted(username, password, response.getStatus()));
  }

  private void simulateClientCredentialsFlow(String clientId, String clientSecret, int iterations)
  {
    String tokenEndpoint = (String) discoveryData.getDiscovery().get("token_endpoint");
    IntStream.range(0, iterations).forEach(i ->
    {
      ServiceAccountTokenRequest tokenRequest = new ServiceAccountTokenRequest();
      tokenRequest.setClientSecret(clientSecret);
      clientManager.getClient().target(tokenEndpoint)
        .request(MediaType.APPLICATION_JSON)
        .post(Entity.form(tokenRequest.toForm()));
    });
  }
}
