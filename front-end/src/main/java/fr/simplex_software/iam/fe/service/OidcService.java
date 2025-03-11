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
  @Inject
  AuthorizationRequest authorizationRequest;
  private String authCode;
  private Map<String, Object> discovery;
  private String accessToken;
  private String idToken;
  private String refreshToken;


  public void setAuthCode(String authCode)
  {
    this.authCode = authCode;
  }

  public void setDiscovery(Map<String, Object> discovery)
  {
    this.discovery = discovery;
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
        .param("client_id", authorizationRequest.getClientId())
        .param("client_secret", authorizationRequest.getSecret())
        .param("scope", authorizationRequest.getScope())
        .param("redirect_uri", authorizationRequest.getRedirectUri());
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
