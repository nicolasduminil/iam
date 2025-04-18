package fr.simplex_software.iam.fe.view;

import fr.simplex_software.iam.domain.schema.*;
import fr.simplex_software.iam.fe.exceptions.*;
import fr.simplex_software.iam.fe.service.*;
import io.quarkus.runtime.annotations.*;
import io.smallrye.config.*;
import jakarta.enterprise.context.*;
import jakarta.faces.application.*;
import jakarta.faces.component.*;
import jakarta.faces.context.*;
import jakarta.inject.*;
import jakarta.json.*;
import jakarta.json.bind.*;
import jakarta.servlet.http.*;
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
import java.util.stream.*;

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
  SmallRyeConfig config;
  @Inject
  Oauth20AuthorizationRequest oauth20AuthorizationRequest;
  @ConfigProperty(name = "quarkus.oidc.auth-server-url")
  String issuer;
  @ConfigProperty(name = "keycloak.discovery.endpoint")
  String discoveryEndpoint;
  @ConfigProperty(name = "keycloak.realm")
  String realm;
  @ConfigProperty(name = "iam-frontend.sandbox-redirect")
  String sandBoxRedirect;
  @ConfigProperty(name = "iam-frontend.secured.service.url")
  String securedServiceUrl;
  @ConfigProperty(name = "iam-frontend.public.service.url")
  String publicServiceUrl;
  private Map<String, Object> discovery;
  private String discoveryJson = null;
  private boolean showDiscoveryJson;
  private String authenticationRequest;
  private boolean showAuthenticationRequest;
  private String authorizationRequest;
  private boolean showAuthorizationRequest;
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
  private String publicServiceResponse;
  private String securedServiceResponse;

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
    if (authCode == null && showAuthenticationRequest)
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

  public Oauth20AuthorizationRequest getOauth20AuthorizationRequest()
  {
    return oauth20AuthorizationRequest;
  }

  public String getAuthorizationRequest()
  {
    return authorizationRequest;
  }

  public boolean isShowAuthorizationRequest()
  {
    return showAuthorizationRequest;
  }

  public String getSecuredServiceUrl()
  {
    return securedServiceUrl;
  }

  public String getPublicServiceUrl()
  {
    return publicServiceUrl;
  }

  public String getPublicServiceResponse()
  {
    return publicServiceResponse;
  }

  public String getSecuredServiceResponse()
  {
    return securedServiceResponse;
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
      try
      {
        String redirectUri = getRedirectUri();
        //validateOidcScope();
        oidcAuthenticationRequest.setRedirectUri(redirectUri);
        URI authUri = oidcAuthenticationRequest.buildAuthenticationUri(authEndpoint);
        authenticationRequest = authUri.toString();
        formattedAuthRequest = formatRequest(authUri);
        showAuthenticationRequest = true;
      }
      catch (NoSuchClientException ex)
      {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
          "There is no Keycloak client with ID %s".formatted(oidcAuthenticationRequest.getClientId()), null);
        facesContext.addMessage(null, message);
        showAuthenticationRequest = false;
      }
    }
    else
      showAuthenticationRequest = !showAuthenticationRequest;
  }

  public void generateAuthorizationRequest()
  {
    if (discovery == null || discovery.isEmpty())
    {
      FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
        "Discovery document not loaded", null);
      facesContext.addMessage(null, message);
      showAuthorizationRequest = false;
    }
    else if (StringUtils.isEmpty(authorizationRequest))
    {
      String authEndpoint = (String) discovery.get("authorization_endpoint");
      try
      {
        String redirectUri = getRedirectUri();
        oauth20AuthorizationRequest.setRedirectUri(redirectUri);
        URI authUri = oauth20AuthorizationRequest.buildAuthorizationUri(authEndpoint);
        authorizationRequest = authUri.toString();
        formattedAuthRequest = formatRequest(authUri);
        showAuthorizationRequest = true;
      }
      catch (NoSuchClientException ex)
      {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
          "There is no Keycloak client with ID %s".formatted(oidcAuthenticationRequest.getClientId()), null);
        facesContext.addMessage(null, message);
        showAuthenticationRequest = false;
      }
    }
    else
      showAuthorizationRequest = !showAuthorizationRequest;
  }

  public void sendAuthenticationRequest() throws IOException
  {
    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
    try
    {
      if (!authenticationRequest.contains("?"))
        authenticationRequest += "?";
      else if (!authenticationRequest.endsWith("&"))
        authenticationRequest += "&";
      authenticationRequest += "error_page=" + URLEncoder.encode("/login-error.xhtml", "UTF-8");
      externalContext.redirect(authenticationRequest);
    }
    catch (Exception e)
    {
      externalContext.redirect(request.getContextPath() + "/system-error.xhtml");
    }
  }

  public void sendAuthorizationRequest() throws IOException
  {
    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
    try
    {
      if (!authorizationRequest.contains("?"))
        authorizationRequest += "?";
      else if (!authorizationRequest.endsWith("&"))
        authorizationRequest += "&";
      authorizationRequest += "error_page=" + URLEncoder.encode("/login-error.xhtml", "UTF-8");
      externalContext.redirect(authorizationRequest);
    }
    catch (Exception e)
    {
      externalContext.redirect(request.getContextPath() + "/system-error.xhtml");
    }
  }

  public void sendTokenRequest() throws InvalidTokenException
  {
    try (Response response = actionGetTokenResponse())
    {
      Map<String, Object> map = response.readEntity(new GenericType<>() {});
      idToken = (String) map.get("id_token");
      if (idToken == null)
        throw new InvalidTokenException("id_token is null");
      accessToken = (String) map.get("access_token");
      refreshToken = (String) map.get("refresh_token");
      String[] idTokenParts = idToken.split("\\.");
      headerJson = prettyPrintJsonB(new String(Base64.getUrlDecoder().decode(idTokenParts[0]), StandardCharsets.UTF_8));
      payloadJson = prettyPrintJsonB(new String(Base64.getUrlDecoder().decode(idTokenParts[1]), StandardCharsets.UTF_8));
      idTokenSignature = idTokenParts[2];
      tokenResponse = prettyPrintJsonB(truncateTokens(map));
    }
    catch (InvalidTokenException e)
    {
      FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
        e.getMessage(), null);
      facesContext.addMessage(null, message);
    }
  }

  public void sendAccessTokenRequest()
  {
    try (Response response = actionGetTokenResponse())
    {
      Map<String, Object> map = response.readEntity(new GenericType<>() {});
       accessToken = (String) map.get("access_token");
      if (accessToken == null)
        throw new InvalidTokenException("id_token is null");
      String[] accessTokenParts = accessToken.split("\\.");
      headerJson = prettyPrintJsonB(new String(Base64.getUrlDecoder().decode(accessTokenParts[0]), StandardCharsets.UTF_8));
      payloadJson = prettyPrintJsonB(new String(Base64.getUrlDecoder().decode(accessTokenParts[1]), StandardCharsets.UTF_8));
      tokenResponse = prettyPrintJsonB(truncateTokens(map));
    }
    catch (InvalidTokenException e)
    {
      FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
        e.getMessage(), null);
      facesContext.addMessage(null, message);
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

  public void invokePublicService()
  {
    publicServiceResponse = clientManager.getClient()
      .target(publicServiceUrl).request().get(String.class);
  }

  public void invokeSecuredService()
  {
    securedServiceResponse = clientManager.getClient()
      .target(securedServiceUrl).request().get(String.class);
  }

  private String getRedirectUri() throws NoSuchClientException
  {
    try
    {
      return keycloak.realm(realm).clients()
        .findByClientId(oidcAuthenticationRequest.getClientId()).getFirst().getRedirectUris().getFirst();
    }
    catch (Exception ex)
    {
      throw new NoSuchClientException("");
    }
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
      oidcAuthenticationRequest.getClientId(), oidcAuthenticationRequest.getScopes());
    String tokenEndpoint = (String) discovery.get("token_endpoint");
    formattedRefreshRequest = formatRequest(refreshRequest.buildTokenUri(tokenEndpoint));
    try (Response response = clientManager.getClient().target(tokenEndpoint)
      .request(MediaType.APPLICATION_JSON)
      .post(Entity.form(refreshRequest.toForm())))
    {
      Map<String, Object> map = response.readEntity(new GenericType<>() {});
      idToken = (String) map.get("id_token");
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

  public void reset() throws Exception
  {
    discoveryJson = null;
    discovery = null;
    showDiscoveryJson = false;
    authenticationRequest = null;
    showAuthenticationRequest = false;
    formattedAuthRequest = null;
    authorizationRequest = null;
    showAuthorizationRequest = false;
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
    externalContext.redirect(UriBuilder
      .fromPath(externalContext.getRequestContextPath() + "/" + sandBoxRedirect)
      .queryParam("activeIndex", "0").build().toString());
  }

  public List<String> getRealmScopes()
  {
    return keycloak.realm(realm).clientScopes().
      findAll().stream().map(ClientScopeRepresentation::getName)
      .collect(Collectors.toList());
  }

  public List<String> getRealmClients()
  {
    return keycloak.realm(realm).clients()
      .findAll().stream().map(ClientRepresentation::getClientId)
      .collect(Collectors.toList());
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

  private void reinitializeOidcAuthenticationRequest() throws NoSuchClientException
  {
    String loginHint = config.getOptionalValue("oauth2.login.hint", String.class)
      .orElse("");
    oidcAuthenticationRequest = new OidcAuthenticationRequest(config.getValue("oauth2.client.id", String.class),
      getRedirectUri(),
      config.getValue("oauth2.response.type", String.class),
      Arrays.stream(config.getValue("oauth2.scope", String.class)
        .split("\\s+")).toList(),
      config.getOptionalValue("oauth2.prompt", String.class),
      config.getOptionalValue("oauth2.max.age", String.class),
      config.getOptionalValue("oauth2.login.hint", String.class));
  }

  private void validateOidcScope() throws InvalidScopeException
  {
    List<String> requestedScopes = oidcAuthenticationRequest.getScopes();
    if (requestedScopes == null || requestedScopes.isEmpty())
      throw new InvalidScopeException("Scope cannot be empty");
    if (!requestedScopes.contains("openid"))
      throw new InvalidScopeException("'openid' scope is required for OIDC authentication");
    List<ClientScopeRepresentation> allRealmScopes = keycloak.realm(realm).clientScopes().findAll();
    Set<String> availableScopes = allRealmScopes != null ?
      allRealmScopes.stream().map(ClientScopeRepresentation::getName).collect(Collectors.toSet())
      : new HashSet<>();
    Optional<String> invalidScope = requestedScopes.stream()
      .filter(scope -> !"openid".equals(scope))
      .filter(scope -> !availableScopes.contains(scope))
      .findFirst();
    if (invalidScope.isPresent())
      throw new InvalidScopeException("Invalid scope: " + invalidScope.get());
  }
}