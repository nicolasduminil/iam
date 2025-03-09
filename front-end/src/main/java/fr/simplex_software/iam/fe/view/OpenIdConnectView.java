package fr.simplex_software.iam.fe.view;

import fr.simplex_software.iam.fe.service.*;
import io.quarkus.runtime.annotations.*;
import jakarta.enterprise.context.*;
import jakarta.faces.application.*;
import jakarta.faces.context.*;
import jakarta.inject.*;
import jakarta.json.bind.*;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;
import org.apache.commons.lang3.*;
import org.eclipse.microprofile.config.inject.*;
import org.eclipse.microprofile.rest.client.inject.*;
import org.keycloak.admin.client.*;
import org.primefaces.event.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;

@Named
@SessionScoped
@RegisterForReflection(serialization = true)
public class OpenIdConnectView implements Serializable
{
  @Inject
  Keycloak keycloak;
  @RestClient
  private IamServiceClient iamServiceClient;
  @Inject
  OidcService oidcService;
  @ConfigProperty(name = "quarkus.oidc.auth-server-url")
  String issuer;
  @ConfigProperty(name = "quarkus.discovery.endpoint")
  String discoveryEndpoint;
  @ConfigProperty(name = "quarkus.oidc.client-id")
  String clientId;
  @ConfigProperty(name = "quarkus.oidc.credentials.client-secret.value")
  String secret;
  @ConfigProperty(name = "keycloak.realm")
  String realm;
  private Map<String, Object> discovery;
  private String discoveryJson;
  private boolean showDiscoveryJson;
  private String authenticationRequest;
  private boolean showAuthenticationRequest;
  private String scope = "openid";
  private String prompt;
  private String maxAge;
  private String loginHint;
  private String formattedAuthRequest;
  private Client client = ClientBuilder.newClient();
  private String authenticationRequestOutput;
  private String authCode = "code";
  private String accessToken;
  private String idToken;
  private String refreshToken;
  private String redirectUri;

  public String getClientId()
  {
    return clientId;
  }

  public void setClientId(String clientId)
  {
    this.clientId = clientId;
  }

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

  public String getScope()
  {
    return scope;
  }

  public void setScope(String scope)
  {
    this.scope = scope;
  }

  public String getPrompt()
  {
    return prompt;
  }

  public void setPrompt(String prompt)
  {
    this.prompt = prompt;
  }

  public String getMaxAge()
  {
    return maxAge;
  }

  public void setMaxAge(String maxAge)
  {
    this.maxAge = maxAge;
  }

  public String getLoginHint()
  {
    return loginHint;
  }

  public void setLoginHint(String loginHint)
  {
    this.loginHint = loginHint;
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

  public String getSecret()
  {
    return secret;
  }

  public void setSecret(String secret)
  {
    this.secret = secret;
  }

  public void loadDiscovery()
  {
    if (StringUtils.isEmpty(discoveryJson))
    {
      discovery = client.target(issuer + discoveryEndpoint)
        .request(MediaType.APPLICATION_JSON).get(Map.class);
      discoveryJson = JsonbBuilder.create(new JsonbConfig()
          .withFormatting(true))
        .toJson(discovery);
      showDiscoveryJson = true;
    } else
      showDiscoveryJson = !showDiscoveryJson;
  }

  public void generateAuthenticationRequest()
  {
    if (StringUtils.isEmpty(authenticationRequest))
    {
      String authEndpoint = (String) discovery.get("authorization_endpoint");
      redirectUri = getRedirectUri();
      UriBuilder builder = UriBuilder.fromUri(authEndpoint)
        .queryParam("client_id", clientId)
        .queryParam("client_secret", secret)
        .queryParam("redirect_uri", redirectUri)
        .queryParam("response_type", "code")
        .queryParam("scope", scope);
      URI authUri = builder.build();
      authenticationRequest = authUri.toString();
      formattedAuthRequest = formatAuthRequest(authUri);
      showAuthenticationRequest = true;
    } else
      showAuthenticationRequest = !showAuthenticationRequest;
  }

  public void sendAuthenticationRequest() throws IOException
  {
    oidcService.setAuthCode(authCode);
    oidcService.setClientId(clientId);
    oidcService.setScope(scope);
    oidcService.setRedirectUri(redirectUri);
    oidcService.setDiscovery(discovery);
    oidcService.setSecret(secret);
    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    externalContext.redirect(authenticationRequest);
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

  public void exchangeCodeForTokens()
  {

  }

  private String getRedirectUri()
  {
    String uri = keycloak.realm(realm).clients().findByClientId(clientId).getFirst().getRedirectUris().getFirst();
    return uri;
  }

  private String formatAuthRequest(URI authRequest)
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
}

