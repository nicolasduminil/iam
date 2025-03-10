package fr.simplex_software.iam.fe.service;

import fr.simplex_software.iam.domain.schema.*;
import jakarta.enterprise.context.*;
import jakarta.inject.*;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;

import java.util.*;

@ApplicationScoped
@Named
public class OidcService
{
  private AuthorizationRequest authorizationRequest = new AuthorizationRequest();
  private String authCode;
  private String clientId;
  private String scope;
  private String redirectUri;
  private Map<String, Object> discovery;
  private String secret;
  private String accessToken;
  private String idToken;
  private String refreshToken;


  public void setAuthCode(String authCode)
  {
    this.authCode = authCode;
  }

  public void setClientId(String clientId)
  {
    this.clientId = clientId;
  }

  public void setScope(String scope)
  {
    this.scope = scope;
  }

  public void setRedirectUri(String redirectUri)
  {
    this.redirectUri = redirectUri;
  }

  public void setDiscovery(Map<String, Object> discovery)
  {
    this.discovery = discovery;
  }

  public void setSecret(String secret)
  {
    this.secret = secret;
  }

  public String getAuthCode()
  {
    return authCode;
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

  public String getAuthorizationRequest()
  {
    return authorizationRequest.toString();
  }

  public void setAuthorizationRequest(AuthorizationRequest authorizationRequest)
  {
    this.authorizationRequest = authorizationRequest;
  }

  public void exchangeCodeForTokens()
  {
    try (Client client = ClientBuilder.newClient())
    {
      Form form = new Form()
        .param("grant_type", "authorization_code")
        .param("code", authCode)
        .param("client_id", clientId)
        .param("client_secret", secret)
        .param("scope", scope)
        .param("redirect_uri", redirectUri);
      String tokenEndpoint = (String) discovery.get("token_endpoint");
      Response response = client.target(tokenEndpoint)
        .request(MediaType.APPLICATION_JSON)
        .post(Entity.form(form));
      Map<String, Object> tokens = response.readEntity(Map.class);
      refreshToken = (String) tokens.get("refresh_token");
      idToken = (String) tokens.get("id_token");
      accessToken = (String) tokens.get("access_token");
    }
  }
}
