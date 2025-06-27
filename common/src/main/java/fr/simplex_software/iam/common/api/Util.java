package fr.simplex_software.iam.common.api;

import fr.simplex_software.iam.common.api.exceptions.*;
import fr.simplex_software.iam.common.api.mappers.*;
import fr.simplex_software.iam.domain.schema.data.*;
import jakarta.enterprise.context.*;
import jakarta.faces.application.*;
import jakarta.faces.context.*;
import jakarta.inject.*;
import jakarta.json.*;
import jakarta.json.bind.*;
import jakarta.ws.rs.core.*;
import org.keycloak.admin.client.*;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.*;

import java.net.*;
import java.nio.charset.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.stream.*;

@ApplicationScoped
@Named
public class Util
{
  @Inject
  FacesContext facesContext;
  @Inject
  Keycloak keycloak;
  private final Jsonb jsonb = JsonbBuilder.create(new JsonbConfig()
    .withFormatting(true));
  @Named
  @Inject
  ClientManager clientManager;

  public void facesErrorMessage(String message)
  {
    FacesMessage fmsg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
      "Faces Error", message);
    facesContext.addMessage(null, fmsg);
  }

  public String getRedirectUri(String realm, String clientId) throws NoSuchClientException
  {
    try
    {
      String uri = keycloak.realm(realm).clients()
        .findByClientId(clientId).getFirst().getRedirectUris().getFirst();
      if (!isValidUrl(uri))
        throw new IllegalArgumentException("Invalid redirect URI %s for client ID %s"
          .formatted(uri, clientId));
      return uri;
    }
    catch (Exception ex)
    {
      if (ex instanceof IllegalArgumentException)
        throw new NoSuchClientException(ex.getMessage());
      throw new NoSuchClientException("There is no valid Keycloak client with ID %s"
        .formatted(clientId));
    }
  }

  public String formatRequest(URI authRequest)
  {
    StringBuilder formattedAuthRequest = new StringBuilder();
    formattedAuthRequest.append(authRequest.getScheme())
      .append("://quarkus.oidc.client-id").append(authRequest.getHost());
    if (authRequest.getPort() != -1)
      formattedAuthRequest.append(":").append(authRequest.getPort());
    formattedAuthRequest.append(authRequest.getPath()).append("\n");
    String query = authRequest.getRawQuery();
    if (query != null)
    {
      String[] params = query.split("&");
      for (String param : params)
      {
        if (param.startsWith("redirect_uri"))
          param = URLDecoder.decode(param, StandardCharsets.UTF_8);
        if (param.startsWith("password"))
        {
          String[] parts = param.split("=", 2);
          if (parts.length > 1)
            param = "password=" + "*".repeat(parts[1].length());
        }
        else if (param.startsWith("client_secret"))
        {
          String[] parts = param.split("=", 2);
          if (parts.length > 1)
            param = "client_secret=" + "*".repeat(parts[1].length());
        }
        formattedAuthRequest.append("  ").append(param).append("\n");
      }
    }
    return formattedAuthRequest.toString();
  }

  public List<String> getRealmScopes(String realm)
  {
    return keycloak.realm(realm).clientScopes().
      findAll().stream().map(ClientScopeRepresentation::getName)
      .collect(Collectors.toList());
  }

  public List<String> getRealmUsers(String realm)
  {
    return keycloak.realm(realm).users().list().stream()
      .map(AbstractUserRepresentation::getUsername)
      .collect(Collectors.toList());
  }

  public UserRepresentation getRealmUser(String realm, String user)
  {
    return keycloak.realm(realm).users().search(user).get(0);
  }

  public RolesResource getRealmRolesResource(String realm)
  {
    return keycloak.realm(realm).roles();
  }

  public RoleRepresentation getRealmRole(String realm, String role)
  {
    return getRealmRolesResource(realm).get(role).toRepresentation();
  }

  public void createRealmUserRepresentation(String realm, UserRepresentation user) throws UserCreationException
  {
    Response response = keycloak.realm(realm).users().create(user);
    if (response.getStatus() != Response.Status.CREATED.getStatusCode())
      throw new UserCreationException("Unexpected exception while trying to creat user %s".formatted(user.getUsername()));
    String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
    keycloak.realm(realm).users().get(userId).resetPassword(user.getCredentials().getFirst());
  }

  public void createRealmUser(String realm, UserData userData) throws UserCreationException
  {
    UserRepresentation user = KcUserMapper.INSTANCE.toRepresentation(userData);
    user.setCredentials(List.of(createCredentialRepresentation(userData.password())));
    user.setEnabled(true);
    createRealmUserRepresentation(realm, user);
  }

  public CredentialRepresentation createCredentialRepresentation(String password)
  {
    CredentialRepresentation cred = new CredentialRepresentation();
    cred.setType(CredentialRepresentation.PASSWORD);
    cred.setValue(password);
    cred.setTemporary(false);
    return cred;
  }

  public void assignRoleToUser(String realm, String user, RoleRepresentation role)
  {
    keycloak.realm(realm).users().get(user).roles().realmLevel().add(Collections.singletonList(role));
  }

  public List<String> getRealmClients(String realm)
  {
    return keycloak.realm(realm).clients()
      .findAll().stream().map(ClientRepresentation::getClientId)
      .collect(Collectors.toList());
  }

  public String getClientSecret(String realm, String clientId)
  {
    return keycloak.realm(realm).clients()
      .findByClientId(clientId).getFirst().getSecret();
  }

  public int countFailedLoginsLastNDays(String serverUrl, String realm, int days) throws FailedToGetEventException
  {
    List<EventRepresentation> events = getEvents(serverUrl, realm, "LOGIN_ERROR", days);
    return events.size();
  }

  public int countSuccessfulLoginsLastNDays(String serverUrl, String realm, int days) throws FailedToGetEventException
  {
    List<EventRepresentation> events = getEvents(serverUrl, realm,"LOGIN", days);
    return events.size();
  }

  public int countClientCredentialTokenRequests(String serverUrl, String realm, String clientId, int days) throws FailedToGetEventException
  {
    List<EventRepresentation> events = getEvents(serverUrl, realm,"CLIENT_LOGIN", days);
    return (int) events.stream()
      .filter(event -> clientId.equals(event.getClientId()))
      .count();
  }

  public List<String> getUsersByRole(String realm, String roleName) throws FailedToGetUsersByRole
  {
    try
    {
      Set<UserRepresentation> users = keycloak.realm(realm)
        .roles()
        .get(roleName)
        .getRoleUserMembers();
      return users.stream()
        .map(UserRepresentation::getUsername)
        .collect(Collectors.toList());
    }
    catch (Exception e)
    {
      throw new FailedToGetUsersByRole("Fail to get users for role %s".formatted(roleName));
    }
  }

  public List<String> getAllRealmRoles(String realm)
  {
    List<RoleRepresentation> roles = keycloak.realm(realm)
      .roles()
      .list();
    return roles.stream()
      .map(RoleRepresentation::getName)
      .collect(Collectors.toList());
  }

  public int countClientCredentialGrants(String serverUrl, String realm, String clientId, int daysBack) throws FailedToGetEventException
  {
    String token = keycloak.tokenManager().getAccessToken().getToken();
    String url = String.format("%s/admin/realms/%s/events", serverUrl, realm);
    String dateFrom = iso8601Date(Instant.now().minusSeconds(daysBack * 86400L));
    Response response = clientManager.getClient().target(url)
      .queryParam("type", "CLIENT_LOGIN")
      .queryParam("dateFrom", dateFrom)
      .request()
      .header("Authorization", "Bearer " + token)
      .get();
    if (response.getStatus() != 200)
      throw new FailedToGetEventException("Failed to get events: HTTP status code %d".formatted(response.getStatus()));
    List<EventRepresentation> events = response.readEntity(new GenericType<>() {});
    List<EventRepresentation> matchingEvents = events.stream()
      .filter(e -> clientId.equals(e.getClientId()))
      .toList();
    return matchingEvents.size();
  }

  public Map<String, Object> truncateTokens(Map<String, Object> tokens)
  {
    Map<String, Object> truncatedTokens = new HashMap<>();
    tokens.forEach((key, value) ->
    {
      if (key.equals("access_token") || key.equals("refresh_token") || key.equals("id_token"))
        truncatedTokens.put(key, ((String) value).substring(0, 10) + "...");
      else
        truncatedTokens.put(key, value);
    });
    return truncatedTokens;
  }

  public String prettyPrintJsonB(String uglyJson)
  {
    return jsonb.toJson(jsonb.fromJson(uglyJson, JsonObject.class));
  }

  public String prettyPrintJsonB(Map<String, Object> uglyJson)
  {
    return jsonb.toJson(jsonb.fromJson(jsonb.toJson(uglyJson), JsonObject.class));
  }

  public boolean isValidUrl(String urlString)
  {
    try
    {
      URL url = new URL(urlString);
      return url.getProtocol() != null
        && (url.getProtocol().equals("http") || url.getProtocol().equals("https"))
        && url.getHost() != null;
    }
    catch (MalformedURLException e)
    {
      return false;
    }
  }

  public void logout(String realm)
  {
    keycloak.realm(realm).logoutAll();
  }

  private List<EventRepresentation> getEvents(String serverUrl, String realm,
    String eventType, int daysBack) throws FailedToGetEventException
  {
    long since = Instant.now().minusSeconds(daysBack * 86400L).toEpochMilli();
    String url = String.format("%s/admin/realms/%s/events", serverUrl, realm);
    Response response = clientManager.getClient()
      .target(url)
      .queryParam("type", eventType)
      .queryParam("dateFrom", iso8601Date(Instant.ofEpochMilli(since)))
      .request()
      .get();
    if (response.getStatus() != 200)
      throw new FailedToGetEventException("Failed to get events: HTTP status code %d".formatted(response.getStatus()));
    return response.readEntity(List.class);
  }

  private String iso8601Date(Instant instant)
  {
    return DateTimeFormatter.ISO_INSTANT.format(instant);
  }
}
