package fr.simplex_software.iam.fe.view;

import fr.simplex_software.iam.domain.schema.*;
import io.quarkus.runtime.annotations.*;
import jakarta.enterprise.context.*;
import jakarta.faces.application.*;
import jakarta.faces.context.*;
import jakarta.inject.*;
import jakarta.json.*;
import jakarta.json.bind.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;
import org.apache.commons.lang3.*;
import org.eclipse.microprofile.config.inject.*;
import org.keycloak.admin.client.*;
import org.primefaces.event.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;

@Named
@ApplicationScoped
@RegisterForReflection(serialization = true)
@Path("/callback")
public class OpenIdConnectView implements Serializable
{
  @Inject
  Keycloak keycloak;
  @Inject
  FacesContext facesContext;
  @Inject
  AuthorizationRequest authorizationRequest;
  @ConfigProperty(name = "quarkus.oidc.auth-server-url")
  String issuer;
  @ConfigProperty(name = "quarkus.discovery.endpoint")
  String discoveryEndpoint;
  @ConfigProperty(name = "keycloak.realm")
  String realm;
  private Map<String, Object> discovery;
  private String discoveryJson;
  private boolean showDiscoveryJson;
  private String authenticationRequest;
  private boolean showAuthenticationRequest;
  private String formattedAuthRequest;
  private final Client client = ClientBuilder.newClient();
  private String authCode;
  private String idToken;
  private String tokenResponse;
  private String headerJson;
  private String payloadJson;
  private String idTokenSignature;
  private final Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
  private String formattedRefreshRequest;
  private String sendRefreshRequest;
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

  public void setAuthenticationRequest(String authenticationRequest)
  {
    this.authenticationRequest = authenticationRequest;
  }

  public void setDiscoveryJson(String discoveryJson)
  {
    this.discoveryJson = discoveryJson;
  }

  public String getFormattedAuthRequest()
  {
    return formattedAuthRequest;
  }

  public void setFormattedAuthRequest(String formattedAuthRequest)
  {
    this.formattedAuthRequest = formattedAuthRequest;
  }

  public String getAuthCode()
  {
    return authCode;
  }

  public void setAuthCode(String authCode)
  {
    this.authCode = authCode;
  }

  public String getAccessToken()
  {
    return accessToken;
  }

  public void setAccessToken(String accessToken)
  {
    this.accessToken = accessToken;
  }

  public String getIdToken()
  {
    return idToken;
  }

  public void setIdToken(String idToken)
  {
    this.idToken = idToken;
  }

  public String getRefreshToken()
  {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken)
  {
    this.refreshToken = refreshToken;
  }

  public AuthorizationRequest getAuthorizationRequest()
  {
    return authorizationRequest;
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

  public String getSendRefreshRequest()
  {
    return sendRefreshRequest;
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

  @GET
  public Response handleCallback(@QueryParam("code") String code)
  {
    authCode = code;
    return Response.temporaryRedirect(UriBuilder.fromPath("index.xhtml")
      .queryParam("activeIndex", "1").build()).build();
  }


  public void loadDiscovery()
  {
    if (StringUtils.isEmpty(discoveryJson))
    {
      discovery = client.target(issuer + discoveryEndpoint)
        .request(MediaType.APPLICATION_JSON).get(Map.class);
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
      authorizationRequest.setRedirectUri(getRedirectUri());
      URI authUri = authorizationRequest.buildAuthorizationUri(authEndpoint);
      authenticationRequest = authUri.toString();
      formattedAuthRequest = formatRequest(authUri);
      showAuthenticationRequest = true;
    }
    else
      showAuthenticationRequest = !showAuthenticationRequest;
  }

  public void sendAuthenticationRequest() throws IOException
  {
    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    externalContext.redirect(authenticationRequest);
  }

  public void sendTokenRequest()
  {
    Map<String, Object> map = actionGetTokenResponse().readEntity(Map.class);
    idToken = (String) map.get("id_token");
    accessToken = (String) map.get("access_token");
    refreshToken = (String) map.get("refresh_token");
    String[] idTokenParts = idToken.split("\\.");
    headerJson = prettyPrintJsonB(new String(Base64.getUrlDecoder().decode(idTokenParts[0]), StandardCharsets.UTF_8));
    payloadJson = prettyPrintJsonB(new String(Base64.getUrlDecoder().decode(idTokenParts[1]), StandardCharsets.UTF_8));
    idTokenSignature = idTokenParts[2];
    tokenResponse = prettyPrintJsonB(truncateTokens(map));
  }

  public Response actionGetTokenResponse()
  {
    TokenRequest tokenRequest = new TokenRequest(authorizationRequest, authCode);
    String tokenEndpoint = (String) discovery.get("token_endpoint");
    formattedTokenRequest = formatRequest(tokenRequest.buildTokenUri(tokenEndpoint));
    return client.target(tokenEndpoint)
      .request(MediaType.APPLICATION_JSON)
      .post(Entity.form(tokenRequest.toForm()));
  }

  public void onTabChange(TabChangeEvent event)
  {
    FacesMessage msg = new FacesMessage("Tab Changed", "Active Tab: " + event.getTab().getTitle());
    FacesContext.getCurrentInstance().addMessage(null, msg);
  }

  public void onTabClose(TabCloseEvent event)
  {
    FacesMessage msg = new FacesMessage("Tab Closed", "Closed tab: " + event.getTab().getTitle());
    FacesContext.getCurrentInstance().addMessage(null, msg);
  }

  public void handleInputChange()
  {
    discoveryJson = null;
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
  }

  private String getRedirectUri()
  {
    String uri = keycloak.realm(realm).clients().findByClientId(authorizationRequest.getClientId())
      .getFirst().getRedirectUris().getFirst();
    return uri;
  }

  public static String formatRequest(URI authRequest)
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
      authorizationRequest.getClientId(), authorizationRequest.getScope());
    String tokenEndpoint = (String) discovery.get("token_endpoint");
    formattedRefreshRequest = formatRequest(refreshRequest.buildTokenUri(tokenEndpoint));
    Map<String, Object> map = client.target(tokenEndpoint)
      .request(MediaType.APPLICATION_JSON)
      .post(Entity.form(refreshRequest.toForm())).readEntity(Map.class);
    idToken = (String) map.get("id_token");
    accessToken = (String) map.get("access_token");
    refreshToken = (String) map.get("refresh_token");
    refreshResponse = prettyPrintJsonB(truncateTokens(map));
    String[] idTokenParts = idToken.split("\\.");
    refreshPayloadJson = prettyPrintJsonB(new String(Base64.getUrlDecoder().decode(idTokenParts[1]), StandardCharsets.UTF_8));
  }

  public void sendUserInfoRequest()
  {
    String userInfoEndpoint = (String) discovery.get("userinfo_endpoint");
    formattedUserInfoRequest = formatRequest(URI.create(userInfoEndpoint));
    userInfoResponse = prettyPrintJsonB(client.target(userInfoEndpoint)
      .request(MediaType.APPLICATION_JSON)
      .header("Authorization", "Bearer " + accessToken)
      .get(String.class));
  }

  public void reset() throws IOException
  {
    handleInputChange();
    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    externalContext.redirect(UriBuilder.fromPath("index.xhtml")
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
}

