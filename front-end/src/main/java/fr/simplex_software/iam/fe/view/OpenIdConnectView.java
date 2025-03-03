package fr.simplex_software.iam.fe.view;

import fr.simplex_software.iam.fe.service.*;
import io.quarkus.runtime.annotations.*;
import jakarta.enterprise.context.*;
import jakarta.faces.application.*;
import jakarta.faces.context.*;
import jakarta.faces.view.*;
import jakarta.inject.*;
import jakarta.json.bind.*;
import jakarta.servlet.http.*;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;
import org.apache.commons.lang3.*;
import org.eclipse.microprofile.config.inject.*;
import org.eclipse.microprofile.rest.client.inject.*;
import org.primefaces.event.*;

import java.io.*;
import java.util.*;

@Named
@ViewScoped
@RegisterForReflection(serialization = true)
public class OpenIdConnectView implements Serializable
{
  @RestClient
  private IamServiceClient iamServiceClient;
  @Inject
  FacesContext facesContext;
  @ConfigProperty(name = "quarkus.oidc.auth-server-url")
  String issuer;
  @ConfigProperty(name = "quarkus.oidc.client-id")
  String clientId;
  private Map<String, Object> discovery;
  private String discoveryJson;
  private boolean showDiscoveryJson;
  private String authenticationRequest;
  private boolean showAuthenticationRequest;

  public String getClientId()
  {
    return clientId;
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

  public void loadDiscovery()
  {
    System.out.println ("### Entry loadDiscovery(): discoveryJson is "  + (StringUtils.isEmpty(discoveryJson) ? "empty" : "not empty" + " and showDiscoveryJson is " + showDiscoveryJson));
    if (StringUtils.isEmpty(discoveryJson))
      try (Client client = ClientBuilder.newClient())
      {
        WebTarget target = client.target(issuer + "/.well-known/openid-configuration");
        discovery = target.request(MediaType.APPLICATION_JSON).get(Map.class);
        discoveryJson = JsonbBuilder.create(new JsonbConfig()
          .withFormatting(true))
          .toJson(discovery);
        showDiscoveryJson = true;
      }
      catch (Exception e)
      {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
          "Error loading discovery document", e.getMessage());
        facesContext.addMessage(null, message);
        showDiscoveryJson = false;
      }
    else
      showDiscoveryJson = !showDiscoveryJson;
    System.out.println ("### Exit loadDiscovery(): discoveryJson is "  + (StringUtils.isEmpty(discoveryJson) ? "empty" : "not empty" + " and showDiscoveryJson is " + showDiscoveryJson));
  }

  public void generateAuthenticationRequest()
  {
    System.out.println(">>> Entry generateAuthenticationRequest(): discovery is " + (discovery == null || discovery.isEmpty() ? "empty" : "not empty")
      + " authenticationRequest is " + (StringUtils.isEmpty(authenticationRequest) ? "empty" : "not empty")
      + " showAuthenticationRequest is " + showAuthenticationRequest);
    if (discovery == null || discovery.isEmpty())
    {
      FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
        "Discovery document not loaded", null);
      facesContext.addMessage(null, message);
      showAuthenticationRequest = false;
    }
    else
    {
      if (StringUtils.isEmpty(authenticationRequest))
      {
        String authEndpoint = (String) discovery.get("authorization_endpoint");
        String redirectUri = getRedirectUri();
        UriBuilder builder = UriBuilder.fromUri(authEndpoint)
          .queryParam("client_id", clientId)
          .queryParam("response_type", "code")
          .queryParam("redirect_uri", redirectUri);
        authenticationRequest = builder.build().toString();
        showAuthenticationRequest = true;
      }
      else
        showAuthenticationRequest = !showAuthenticationRequest;
    }
    System.out.println(">>> Exit generateAuthenticationRequest(): discovery is " + (discovery == null || discovery.isEmpty() ? "empty" : "not empty")
      + " authenticationRequest is " + (StringUtils.isEmpty(authenticationRequest) ? "empty" : "not empty")
      + " showAuthenticationRequest is " + showAuthenticationRequest);
  }

  public void testAction()
  {
    System.out.println ("########################################################");
  }

  /*public void handleCallback()
  {
    Map<String, String> params = facesContext.getExternalContext()
      .getRequestParameterMap();
    code = params.get("code");
    if (code != null)
    {
      exchangeCodeForTokens();
    }
  }*/

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

  /*private void exchangeCodeForTokens()
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
    } catch (Exception e)
    {
      FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
        "Error exchanging code for tokens", e.getMessage());
      facesContext.addMessage(null, message);
    }
  }*/

  private String getRedirectUri()
  {
    HttpServletRequest request = (HttpServletRequest) facesContext
      .getExternalContext().getRequest();
    String uri = request.getRequestURL().toString();
    return uri.substring(0, uri.lastIndexOf("/"));
  }
}
