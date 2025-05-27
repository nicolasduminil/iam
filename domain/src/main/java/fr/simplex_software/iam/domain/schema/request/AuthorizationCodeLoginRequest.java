package fr.simplex_software.iam.domain.schema.request;

import jakarta.ws.rs.core.*;

import java.net.*;
import java.util.*;

public class AuthorizationCodeLoginRequest extends ClientWithRedirectUri implements OAuth20Request
{
  private PromptType prompt = PromptType.LOGIN;
  private int maxAge = 3600;
  private String loginHint = "";
  private ResponseType responseType = ResponseType.CODE;
  private String clientSecret;

  public AuthorizationCodeLoginRequest()
  {
    super(GrantType.AUTHORIZATION_CODE);
  }

  public AuthorizationCodeLoginRequest(String clientId, List<String> scopes)
  {
    super(clientId, scopes);
  }

  public AuthorizationCodeLoginRequest(String clientId, List<String> scopes, String redirectUri)
  {
    super(clientId, scopes, redirectUri);
  }

  public AuthorizationCodeLoginRequest(String clientId, List<String> scopes, ResponseType responseType)
  {
    super(clientId, scopes);
    this.responseType = responseType;
  }

  public AuthorizationCodeLoginRequest(String clientId, List<String> scopes,
    String redirectUri, ResponseType responseType)
  {
    super(clientId, scopes, redirectUri);
    this.responseType = responseType;
  }

  public AuthorizationCodeLoginRequest(String clientId, List<String> scopes,
    PromptType prompt, int maxAge, String loginHint)
  {
    this(clientId, scopes);
    this.prompt = prompt;
    this.maxAge = maxAge;
    this.loginHint = loginHint;
  }

  public AuthorizationCodeLoginRequest(String clientId, List<String> scopes,
    String redirectUri, PromptType prompt, int maxAge, String loginHint)
  {
    this(clientId, scopes, redirectUri);
    this.prompt = prompt;
    this.maxAge = maxAge;
    this.loginHint = loginHint;
  }

  public AuthorizationCodeLoginRequest(String clientId, List<String> scopes,
    ResponseType responseType, PromptType prompt, int maxAge, String loginHint)
  {
    this(clientId, scopes, responseType);
    this.prompt = prompt;
    this.maxAge = maxAge;
    this.loginHint = loginHint;
  }

  public AuthorizationCodeLoginRequest(String clientId, List<String> scopes,
    String redirectUri, ResponseType responseType, PromptType prompt, int maxAge, String loginHint)
  {
    this(clientId, scopes, redirectUri, responseType);
    this.prompt = prompt;
    this.maxAge = maxAge;
    this.loginHint = loginHint;
  }

  public AuthorizationCodeLoginRequest(String clientId, GrantType grantType, List<String> scopes, String redirectUri, PromptType prompt, int maxAge, String loginHint, ResponseType responseType, String clientSecret)
  {
    super(clientId, grantType, scopes, redirectUri);
    this.prompt = prompt;
    this.maxAge = maxAge;
    this.loginHint = loginHint;
    this.responseType = responseType;
    this.clientSecret = clientSecret;
  }

  public PromptType getPrompt()
  {
    return prompt;
  }

  public PromptType[] getPromptTypes()
  {
    return PromptType.values();
  }

  public void setPrompt(PromptType prompt)
  {
    this.prompt = prompt;
  }

  public int getMaxAge()
  {
    return maxAge;
  }

  public void setMaxAge(int maxAge)
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

  public ResponseType getResponseType()
  {
    return responseType;
  }

  public void setResponseType(ResponseType responseType)
  {
    this.responseType = responseType;
  }

  public String getClientSecret()
  {
    return clientSecret;
  }

  public void setClientSecret(String clientSecret)
  {
    this.clientSecret = clientSecret;
  }

  public ResponseType[] getResponseTypes()
  {
    return ResponseType.values();
  }

  public URI buildAuthUri(String authorizationEndpoint)
  {
    UriBuilder builder = UriBuilder.fromUri(super.buildAuthUri(authorizationEndpoint))
      .queryParam("response_type", responseType.getValue())
      .queryParam("prompt", prompt.getValue())
      .queryParam("max_age", maxAge);
    if (loginHint != null)
      builder.queryParam("login_hint", loginHint);
    return builder.build();
  }

  @Override
  public URI buildTokenUri(String tokenEndpoint)
  {
    throw new UnsupportedOperationException("### The AuthorizationCodeLoginRequest class doesn't support the buildTokenUri() operation");
  }

  @Override
  public Form toForm()
  {
    throw new UnsupportedOperationException("### The AuthorizationCodeLoginRequest class doesn't support the toForm() operation");
  }
}
