package fr.simplex_software.iam.fe.view;

import fr.simplex_software.iam.domain.schema.*;
import fr.simplex_software.iam.fe.service.*;
import io.quarkus.runtime.annotations.*;
import io.smallrye.config.*;
import jakarta.el.*;
import jakarta.enterprise.context.*;
import jakarta.faces.application.*;
import jakarta.faces.component.*;
import jakarta.faces.context.*;
import jakarta.faces.event.*;
import jakarta.faces.validator.*;
import jakarta.inject.*;
import jakarta.json.*;
import jakarta.json.bind.*;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;
import org.apache.commons.lang3.*;
import org.eclipse.microprofile.config.inject.*;
import org.keycloak.admin.client.*;
import org.keycloak.representations.idm.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;

@Named
@ApplicationScoped
@RegisterForReflection(serialization = true)
public class OpenIdConnectView implements Serializable
{
  @Inject
  Keycloak keycloak;
  @Inject
  FacesContext facesContext;
  @Inject
  OidcAuthenticationRequest oidcAuthenticationRequest;
  @Inject
  ClientManager clientManager;
  @Inject
  OidcRedirectCallbackService oidcRedirectCallbackService;
  @Inject
  private SmallRyeConfig config;
  @ConfigProperty(name = "quarkus.oidc.auth-server-url")
  String issuer;
  @ConfigProperty(name = "quarkus.discovery.endpoint")
  String discoveryEndpoint;
  @ConfigProperty(name = "keycloak.realm")
  String realm;
  @ConfigProperty(name = "iam-frontend.sandbox-redirect")
  String sandBoxRedirect;
  private Map<String, Object> discovery;
  private String discoveryJson = null;
  private boolean showDiscoveryJson;
  private String authenticationRequest;
  private boolean showAuthenticationRequest;
  private String formattedAuthRequest;
  private String authCode;
  private String idToken;
  private String tokenResponse;
  private String headerJson;
  private String payloadJson;
  private String idTokenSignature;
  private final Jsonb jsonb = JsonbBuilder.create(new JsonbConfig()
    .withFormatting(true));
  private String formattedRefreshRequest;
  private String refreshResponse;
  private String refreshPayloadJson;
  private String userInfoResponse;
  private String formattedUserInfoRequest;
  private String formattedTokenRequest;
  private String accessToken;
  private String refreshToken;

  public boolean isShowDiscoveryJson()
  {
    return showDiscoveryJson;
  }

  public boolean isShowAuthenticationRequest()
  {
    return showAuthenticationRequest;
  }

  public String getIssuer()
  {
    return issuer;
  }

  public String getDiscoveryJson()
  {
    return discoveryJson;
  }

  public String getAuthenticationRequest()
  {
    return authenticationRequest;
  }

  public void setIssuer(String issuer)
  {
    this.issuer = issuer;
  }

  public String getFormattedAuthRequest()
  {
    return formattedAuthRequest;
  }

  public String getAuthCode()
  {
    if (authCode == null)
      authCode = oidcRedirectCallbackService.getAuthCode();
    return authCode;
  }

  public OidcAuthenticationRequest getOidcAuthenticationRequest()
  {
    return oidcAuthenticationRequest;
  }

  public String getTokenResponse()
  {
    return tokenResponse;
  }

  public String getHeaderJson()
  {
    return headerJson;
  }

  public String getPayloadJson()
  {
    return payloadJson;
  }

  public String getIdTokenSignature()
  {
    return idTokenSignature;
  }

  public String getFormattedRefreshRequest()
  {
    return formattedRefreshRequest;
  }

  public String getRefreshResponse()
  {
    return refreshResponse;
  }

  public String getRefreshPayloadJson()
  {
    return refreshPayloadJson;
  }

  public String getUserInfoResponse()
  {
    return userInfoResponse;
  }

  public String getFormattedUserInfoRequest()
  {
    return formattedUserInfoRequest;
  }

  public String getFormattedTokenRequest()
  {
    return formattedTokenRequest;
  }

  public void loadDiscovery()
  {
    if (StringUtils.isEmpty(discoveryJson))
    {
      discovery = clientManager.getClient().target(issuer + discoveryEndpoint)
        .request(MediaType.APPLICATION_JSON)
        .get(new GenericType<>()
        {
        });
      discoveryJson = prettyPrintJsonB(discovery);
      showDiscoveryJson = true;
    }
    else
      showDiscoveryJson = !showDiscoveryJson;
  }

  public void generateAuthenticationRequest()
  {
    System.out.println(">>> generateAuthenticationRequest() client ID: " + oidcAuthenticationRequest.getClientId());
    if (discovery == null || discovery.isEmpty())
    {
      FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
        "Discovery document not loaded", null);
      facesContext.addMessage(null, message);
      showAuthenticationRequest = false;
    }
    else if (StringUtils.isEmpty(authenticationRequest))
    {
      String authEndpoint = (String) discovery.get("authorization_endpoint");
      String redirectUri = getRedirectUri();
      if (redirectUri == null)
      {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
          "Redirect URI is not set", null);
        facesContext.addMessage(null, message);
        showAuthenticationRequest = false;
      }
      else
      {
        oidcAuthenticationRequest.setRedirectUri(getRedirectUri());
        URI authUri = oidcAuthenticationRequest.buildAuthenticationUri(authEndpoint);
        authenticationRequest = authUri.toString();
        formattedAuthRequest = formatRequest(authUri);
        showAuthenticationRequest = true;
      }
    }
    else
      showAuthenticationRequest = !showAuthenticationRequest;
  }

  public void validateClientId(FacesContext context, UIComponent component, Object value)
  {
    System.out.println(">>> validateClientId() client ID: " + oidcAuthenticationRequest.getClientId());
    String clientId = (String) value;
    if (!isClientIdValid(clientId))
    {
      throw new ValidatorException(
        new FacesMessage(FacesMessage.SEVERITY_ERROR,
          "Invalid Client ID", "The specified Client ID does not exist"));
    }
    System.out.println("Client ID validation passed");
    System.out.println("Context is valid? " + context.isValidationFailed());
    Iterator<FacesMessage> messages = context.getMessages();
    while (messages.hasNext())
    {
      FacesMessage msg = messages.next();
      System.out.println("Message: " + msg.getSummary() + " - " + msg.getDetail());
    }
  }

  public void sendAuthenticationRequest() throws IOException
  {
    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    externalContext.redirect(authenticationRequest);
  }

  public void sendTokenRequest()
  {
    try (Response response = actionGetTokenResponse())
    {
      Map<String, Object> map = response.readEntity(new GenericType<>()
      {
      });
      idToken = (String) map.get("id_token");
      accessToken = (String) map.get("access_token");
      refreshToken = (String) map.get("refresh_token");
      String[] idTokenParts = idToken.split("\\.");
      headerJson = prettyPrintJsonB(new String(Base64.getUrlDecoder().decode(idTokenParts[0]), StandardCharsets.UTF_8));
      payloadJson = prettyPrintJsonB(new String(Base64.getUrlDecoder().decode(idTokenParts[1]), StandardCharsets.UTF_8));
      idTokenSignature = idTokenParts[2];
      tokenResponse = prettyPrintJsonB(truncateTokens(map));
    }
  }

  public Response actionGetTokenResponse()
  {
    TokenRequest tokenRequest = new TokenRequest(oidcAuthenticationRequest, getAuthCode());
    String tokenEndpoint = (String) discovery.get("token_endpoint");
    formattedTokenRequest = formatRequest(tokenRequest.buildTokenUri(tokenEndpoint));
    return clientManager.getClient().target(tokenEndpoint)
      .request(MediaType.APPLICATION_JSON)
      .post(Entity.form(tokenRequest.toForm()));
  }

  public void handleInputChange(AjaxBehaviorEvent event)
  {
    UIInput source = (UIInput) event.getSource();
    String componentId = source.getId();
    String newValue = (String) source.getValue();
    String submittedValue = (String) source.getSubmittedValue();
    String currentValue = getComponentValue(componentId);
    switch (componentId)
    {
      case "client-id":
        oidcAuthenticationRequest.setClientId(newValue);
        break;
      case "scope":
        oidcAuthenticationRequest.setScope(newValue);
        break;
      case "prompt":
        oidcAuthenticationRequest.setPrompt(newValue);
        break;
      case "max-age":
        oidcAuthenticationRequest.setMaxAge(newValue);
        break;
      case "login-hint":
        oidcAuthenticationRequest.setLoginHint(newValue);
        break;
    }
  }

  private String getRedirectUri()
  {
    String redirectUri = null;
    ClientRepresentation clientRepresentation = null;
    String clientId = oidcAuthenticationRequest.getClientId();
    try
    {
      redirectUri = keycloak.realm(realm).clients()
        .findByClientId(clientId).getFirst().getRedirectUris().getFirst();
    }
    catch (Exception e)
    {
      FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
        "There is no Keycloak client with ID %s".formatted(clientId), null);
      facesContext.addMessage(null, message);
    }
    return redirectUri;
  }

  private boolean isClientIdValid(String clientId)
  {
    return keycloak.realm(realm).clients()
      .findByClientId(clientId).size() == 1;
  }

  public String formatRequest(URI authRequest)
  {
    StringBuilder formattedAuthRequest = new StringBuilder();
    formattedAuthRequest.append(authRequest.getScheme())
      .append("://").append(authRequest.getHost());
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
        formattedAuthRequest.append("  ").append(param).append("\n");
      }
    }
    return formattedAuthRequest.toString();
  }

  public void sendRefreshRequest()
  {
    RefreshRequest refreshRequest = new RefreshRequest("refresh_token", refreshToken,
      oidcAuthenticationRequest.getClientId(), oidcAuthenticationRequest.getScope());
    String tokenEndpoint = (String) discovery.get("token_endpoint");
    formattedRefreshRequest = formatRequest(refreshRequest.buildTokenUri(tokenEndpoint));
    try (Response response = clientManager.getClient().target(tokenEndpoint)
      .request(MediaType.APPLICATION_JSON)
      .post(Entity.form(refreshRequest.toForm())))
    {
      Map<String, Object> map = response.readEntity(new GenericType<>()
      {
      });
      idToken = (String) map.get("id_token");
      //accessToken = (String) map.get("access_token");
      refreshToken = (String) map.get("refresh_token");
      refreshResponse = prettyPrintJsonB(truncateTokens(map));
      String[] idTokenParts = idToken.split("\\.");
      refreshPayloadJson = prettyPrintJsonB(new String(Base64.getUrlDecoder().decode(idTokenParts[1]), StandardCharsets.UTF_8));
    }
  }

  public void sendUserInfoRequest()
  {
    String userInfoEndpoint = (String) discovery.get("userinfo_endpoint");
    formattedUserInfoRequest = formatRequest(URI.create(userInfoEndpoint));
    userInfoResponse = prettyPrintJsonB(clientManager.getClient().target(userInfoEndpoint)
      .request(MediaType.APPLICATION_JSON)
      .header("Authorization", "Bearer " + accessToken)
      .get(String.class));
  }

  public void reset() throws IOException
  {
    discoveryJson = null;
    discovery = null;
    showDiscoveryJson = false;
    authenticationRequest = null;
    showAuthenticationRequest = false;
    formattedAuthRequest = null;
    authCode = null;
    accessToken = null;
    idToken = null;
    refreshToken = null;
    userInfoResponse = null;
    formattedRefreshRequest = null;
    refreshResponse = null;
    refreshPayloadJson = null;
    formattedTokenRequest = null;
    headerJson = null;
    payloadJson = null;
    idTokenSignature = null;
    formattedUserInfoRequest = null;
    tokenResponse = null;
    reinitializeOidcAuthenticationRequest();
    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    String uri = UriBuilder
      .fromPath(externalContext.getRequestContextPath() + "/" + sandBoxRedirect)
      .queryParam("activeIndex", "0").build().toString();
    externalContext.redirect(UriBuilder
      .fromPath(externalContext.getRequestContextPath() + "/" + sandBoxRedirect)
      .queryParam("activeIndex", "0").build().toString());
  }

  private Map<String, Object> truncateTokens(Map<String, Object> tokens)
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

  private String prettyPrintJsonB(String uglyJson)
  {
    return jsonb.toJson(jsonb.fromJson(uglyJson, JsonObject.class));
  }

  private String prettyPrintJsonB(Map<String, Object> uglyJson)
  {
    return jsonb.toJson(jsonb.fromJson(jsonb.toJson(uglyJson), JsonObject.class));
  }

  private String getComponentValue(String componentId)
  {
    FacesContext context = FacesContext.getCurrentInstance();
    final Object[] value = new Object[1];
    context.getViewRoot().invokeOnComponent(context, componentId,
      (ctx, target) ->
      {
        if (target instanceof UIInput)
          value[0] = ((UIInput) target).getValue();
      });
    return (String) value[0];
  }

  private void reinitializeOidcAuthenticationRequest()
  {
    String loginHint = config.getOptionalValue("oauth2.login.hint", String.class)
      .orElse("");
    oidcAuthenticationRequest = new OidcAuthenticationRequest(config.getValue("oauth2.client.id", String.class),
      config.getValue("oauth2.scope", String.class),
      config.getOptionalValue("oauth2.prompt", String.class),
      config.getOptionalValue("oauth2.max.age", String.class),
      config.getOptionalValue("oauth2.login.hint", String.class));
  }
}