package fr.simplex_software.iam.fe.view;

import fr.simplex_software.iam.domain.schema.*;
import fr.simplex_software.iam.fe.service.*;
import jakarta.enterprise.context.*;
import jakarta.faces.application.*;
import jakarta.faces.context.*;
import jakarta.faces.view.*;
import jakarta.inject.*;
import jakarta.json.*;
import jakarta.json.bind.*;
import jakarta.servlet.http.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;
import org.apache.commons.lang3.*;
import org.eclipse.microprofile.config.inject.*;
import org.eclipse.microprofile.rest.client.inject.*;
import org.primefaces.*;

import java.io.*;
import java.util.*;

@Named
@ViewScoped
public class OpenIdConnectView implements Serializable
{
  @RestClient
  private IamServiceClient iamServiceClient;
  @Inject
  private FacesContext facesContext;
  @ConfigProperty(name = "quarkus.oidc.auth-server-url")
  String issuer;
  @ConfigProperty(name = "quarkus.oidc.client-id")
  String clientId;
  private String scope;
  private String prompt;
  private String maxAge;
  private String loginHint;
  private String code;
  private String discoveryJson;
  private String idToken;

  private String accessToken;
  private String refreshToken;
  private Map<String, Object> discovery;
  private String authenticationRequest;
  private String metaData = null;

  public String getClientId()
  {
    return clientId;
  }

  public void setClientId(String clientId)
  {
    this.clientId = clientId;
  }

  public String getIssuer()
  {
    return issuer;
  }

  public void setIssuer(String issuer)
  {
    this.issuer = issuer;
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

  public String getCode()
  {
    return code;
  }

  public void setCode(String code)
  {
    this.code = code;
  }

  public String getDiscoveryJson()
  {
    return discoveryJson;
  }

  public void setDiscoveryJson(String discoveryJson)
  {
    this.discoveryJson = discoveryJson;
  }

  public String getIdToken()
  {
    return idToken;
  }

  public void setIdToken(String idToken)
  {
    this.idToken = idToken;
  }

  public String getAccessToken()
  {
    return accessToken;
  }

  public void setAccessToken(String accessToken)
  {
    this.accessToken = accessToken;
  }

  public String getRefreshToken()
  {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken)
  {
    this.refreshToken = refreshToken;
  }

  public Map<String, Object> getDiscovery()
  {
    return discovery;
  }

  public void setDiscovery(Map<String, Object> discovery)
  {
    this.discovery = discovery;
  }

  public String getAuthenticationRequest()
  {
    return authenticationRequest;
  }

  public void setAuthenticationRequest(String authenticationRequest)
  {
    this.authenticationRequest = authenticationRequest;
  }

  public boolean isMetadata()
  {
    return metaData != null;
  }

  public void loadDiscovery()
  {
    try (Client client = ClientBuilder.newClient())
    {
      WebTarget target = client.target(issuer + "/.well-known/openid-configuration");
      discovery = target.request(MediaType.APPLICATION_JSON).get(Map.class);
      discoveryJson = JsonbBuilder.create().toJson(discovery);
    }
    catch (Exception e)
    {
      FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
        "Error loading discovery document", e.getMessage());
      facesContext.addMessage(null, message);
    }
  }

  public String generateAuthenticationRequest()
  {
    if (discovery == null || discovery.isEmpty())
    {
      FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
        "Discovery document not loaded", null);
      facesContext.addMessage(null, message);
      return null;
    }

    String authEndpoint = (String) discovery.get("authorization_endpoint");
    String redirectUri = getRedirectUri();

    UriBuilder builder = UriBuilder.fromUri(authEndpoint)
      .queryParam("client_id", clientId)
      .queryParam("response_type", "code")
      .queryParam("redirect_uri", redirectUri);

    if (StringUtils.isNotBlank(scope))
    {
      builder.queryParam("scope", scope);
    }
    if (StringUtils.isNotBlank(prompt))
    {
      builder.queryParam("prompt", prompt);
    }
    if (StringUtils.isNotBlank(maxAge))
    {
      builder.queryParam("max_age", maxAge);
    }
    if (StringUtils.isNotBlank(loginHint))
    {
      builder.queryParam("login_hint", loginHint);
    }

    return builder.build().toString();
  }

  public void handleCallback()
  {
    Map<String, String> params = facesContext.getExternalContext()
      .getRequestParameterMap();
    code = params.get("code");
    if (code != null)
    {
      exchangeCodeForTokens();
    }
  }

  private void exchangeCodeForTokens()
  {
    Client client = ClientBuilder.newClient();
    Form form = new Form()
      .param("grant_type", "authorization_code")
      .param("code", code)
      .param("client_id", clientId)
      .param("redirect_uri", getRedirectUri());

    try
    {
      String tokenEndpoint = (String) discovery.get("token_endpoint");
      Response response = client.target(tokenEndpoint)
        .request(MediaType.APPLICATION_JSON)
        .post(Entity.form(form));

      Map<String, Object> tokens = response.readEntity(Map.class);
      refreshToken = (String) tokens.get("refresh_token");
      idToken = (String) tokens.get("id_token");
      accessToken = (String) tokens.get("access_token");
    }
    catch (Exception e)
    {
      FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
        "Error exchanging code for tokens", e.getMessage());
      facesContext.addMessage(null, message);
    }
  }

  private String getRedirectUri()
  {
    HttpServletRequest request = (HttpServletRequest) facesContext
      .getExternalContext().getRequest();
    String uri = request.getRequestURL().toString();
    return uri.substring(0, uri.lastIndexOf("/"));
  }
}
