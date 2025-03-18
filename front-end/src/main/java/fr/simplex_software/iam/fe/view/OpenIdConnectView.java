package fr.simplex_software.iam.fe.view;

import fr.simplex_software.iam.domain.schema.*;
import fr.simplex_software.iam.fe.service.*;
import io.quarkus.runtime.annotations.*;
import jakarta.enterprise.context.*;
import jakarta.faces.application.*;
import jakarta.faces.context.*;
import jakarta.inject.*;
import jakarta.json.*;
import jakarta.json.bind.*;
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
import java.util.HashMap;

@Named
@SessionScoped
@RegisterForReflection(serialization = true)
public class OpenIdConnectView implements Serializable
{
  @Inject
  Keycloak keycloak;
  @Inject
  FacesContext facesContext;
  @Inject
  OidcService oidcService;
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
  private Client client = ClientBuilder.newClient();
  private String authenticationRequestOutput;
  private String authCode;
  private String accessToken;
  private String idToken;
  private String refreshToken;
  private String tokenResponse;
  private String headerJson;
  private String payloadJson;
  private String idTokenSignature;
  private Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
  private String formattedRefreshRequest;
  private String sendRefreshRequest;
  private String refreshResponse;
  private String refreshPayloadJson;

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

  public String getAuthenticationRequestOutput()
  {
    return authenticationRequestOutput;
  }

  public void setAuthenticationRequestOutput(String authenticationRequestOutput)
  {
    this.authenticationRequestOutput = authenticationRequestOutput;
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

  public void loadDiscovery()
  {
    if (StringUtils.isEmpty(discoveryJson))
    {
      discovery = client.target(issuer + discoveryEndpoint)
        .request(MediaType.APPLICATION_JSON).get(Map.class);
      discoveryJson = prettyPrintJsonB(discovery);
      showDiscoveryJson = true;
    } else
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
    } else if (StringUtils.isEmpty(authenticationRequest))
    {
      String authEndpoint = (String) discovery.get("authorization_endpoint");
      authorizationRequest.setRedirectUri(getRedirectUri());
      URI authUri = authorizationRequest.buildAuthorizationUri(authEndpoint);
      authenticationRequest = authUri.toString();
      formattedAuthRequest = formatRequest(authUri);
      showAuthenticationRequest = true;
    } else
      showAuthenticationRequest = !showAuthenticationRequest;
  }

  public void sendAuthenticationRequest() throws IOException
  {
    oidcService.setAuthorizationRequest(authorizationRequest);
    oidcService.setDiscovery(discovery);
    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    externalContext.redirect(authenticationRequest);
  }

  public void sendTokenRequest()
  {
    Map<String, Object> map = oidcService.getTokenResponse().readEntity(Map.class);
    idToken = (String) map.get("id_token");
    String[] idTokenParts = idToken.split("\\.");
    headerJson = prettyPrintJsonB(new String(Base64.getUrlDecoder().decode(idTokenParts[0]), StandardCharsets.UTF_8));
    payloadJson = prettyPrintJsonB(new String(Base64.getUrlDecoder().decode(idTokenParts[1]), StandardCharsets.UTF_8));
    idTokenSignature = idTokenParts[2];
    tokenResponse = prettyPrintJsonB(truncateTokens(oidcService.getTokenResponse().readEntity(Map.class)));
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
    authenticationRequestOutput = null;
    authCode = null;
    accessToken = null;
    idToken = null;
    refreshToken = null;
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

