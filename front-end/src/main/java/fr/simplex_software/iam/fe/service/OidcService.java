package fr.simplex_software.iam.fe.service;

import jakarta.enterprise.context.*;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;

import java.util.Map;

@ApplicationScoped
public class OidcService
{
  private String authCode;
  private String clientId;
  private String scope;
  private String redirectUri;
  private Map<String, Object> discovery;
  private String secret;


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

  public void exchangeCodeForTokens()
  {
    try (Client client = ClientBuilder.newClient())
    {
      Form form = new Form()
        .param("grant_type", "authorization_code")
        .param("code", authCode)
        .param("client_id", clientId)
        .param("secret", scope)
        .param("redirect_uri", redirectUri);
      String tokenEndpoint = (String) discovery.get("token_endpoint");
      System.out.println(">>> exchangeCodeForTokens(): We got the token_endpoint: " + tokenEndpoint);
      System.out.println(">>> exchangeCodeForTokens(): Getting token for authCoce " + authCode
        + " and client " + clientId + " with secret " + secret + " and redirect_uri" + redirectUri);
      Response response = client.target(tokenEndpoint)
        .request(MediaType.APPLICATION_JSON)
        .post(Entity.form(form));
      //Map<String, Object> tokens = response.readEntity(Map.class);
      String tokens = response.readEntity(String.class);
      System.out.println(">>> exchangeCodeForTokens(): We got the tokens: " + tokens);
      /*refreshToken = (String) tokens.get("refresh_token");
      idToken = (String) tokens.get("id_token");
      accessToken = (String) tokens.get("access_token");
      System.out.println(">>> exchangeCodeForTokens(): We got the access_token: " + accessToken +
        " the refresh token " + refreshToken + " and the id token " + idToken);*/
    }
  }
}
