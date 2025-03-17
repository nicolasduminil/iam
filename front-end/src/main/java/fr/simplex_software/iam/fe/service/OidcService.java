package fr.simplex_software.iam.fe.service;

import fr.simplex_software.iam.domain.schema.*;
import fr.simplex_software.iam.fe.view.*;
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
  private String formattedTokenRequest;


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

  public String getFormattedTokenRequest()
  {
    return formattedTokenRequest;
  }

  public void exchangeCodeForTokens()
  {
    Map<String, Object> tokens = getTokenResponse().readEntity(Map.class);
    refreshToken = (String) tokens.get("refresh_token");
    idToken = (String) tokens.get("id_token");
    accessToken = (String) tokens.get("access_token");
  }

  public Response getTokenResponse()
  {
    try (Client client = ClientBuilder.newClient())
    {
      TokenRequest tokenRequest = new TokenRequest(authorizationRequest, authCode);
      String tokenEndpoint = (String) discovery.get("token_endpoint");
      formattedTokenRequest = OpenIdConnectView.formatRequest(tokenRequest.buildTokenUri(tokenEndpoint));
      return client.target(tokenEndpoint)
        .request(MediaType.APPLICATION_JSON)
        .post(Entity.form(tokenRequest.toForm()));
    }
  }
}
