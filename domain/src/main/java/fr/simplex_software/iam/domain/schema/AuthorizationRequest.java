package fr.simplex_software.iam.domain.schema;

import io.smallrye.config.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.openapi.annotations.media.*;

import java.io.*;
import java.net.*;
import java.util.*;

@ConfigMapping(prefix = "oauth2")
@Schema(description = "The authorization request metadata")
public class AuthorizationRequest implements Serializable
{
  @Schema(required = true)
  @WithName("client.id")
  private String clientId;
  @Schema(required = true)
  @WithName("client.responseType")
  @WithDefault("")
  private String redirectUri;
  @Schema(required = true)
  @WithName("client.responseType")
  @WithDefault("code")
  private String responseType;
  @Schema(required = true)
  @WithDefault("openid")
  private String scope;
  @Schema
  private Optional<String> prompt;
  @Schema
  @WithName("max.age")
  private Optional<String> maxAge;
  @Schema
  @WithName("login.hint")
  private Optional<String> loginHint;

  public AuthorizationRequest()
  {
  }

  public AuthorizationRequest(String clientId, String secret, String redirectUri, String responseType, String scope, Optional<String> prompt, Optional<String> maxAge, Optional<String> loginHint)
  {
    this.clientId = clientId;
    this.redirectUri = redirectUri;
    this.responseType = responseType;
    this.scope = scope;
    this.prompt = prompt;
    this.maxAge = maxAge;
    this.loginHint = loginHint;
  }

  public String getClientId()
  {
    return clientId;
  }

  public void setClientId(String clientId)
  {
    this.clientId = clientId;
  }

  public String getRedirectUri()
  {
    return redirectUri;
  }

  public void setRedirectUri(String redirectUri)
  {
    this.redirectUri = redirectUri;
  }

  public String getResponseType()
  {
    return responseType;
  }

  public void setResponseType(String responseType)
  {
    this.responseType = responseType;
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
    return prompt.orElse(null);
  }

  public void setPrompt(String prompt)
  {
    this.prompt = Optional.of(prompt);
  }

  public String getMaxAge()
  {
    return maxAge.orElse(null);
  }

  public void setMaxAge(String maxAge)
  {
    this.maxAge = Optional.of(maxAge);
  }

  public String getLoginHint()
  {
    return loginHint.orElse(null);
  }

  public void setLoginHint(String loginHint)
  {
    this.loginHint = Optional.of(loginHint);
  }

  public URI buildAuthorizationUri(String authorizationEndpoint)
  {
    UriBuilder builder = UriBuilder.fromUri(authorizationEndpoint)
      .queryParam("client_id", clientId)
      .queryParam("response_type", responseType)
      .queryParam("scope", scope)
      .queryParam("redirect_uri", redirectUri);
    prompt.ifPresent(p -> builder.queryParam("prompt", p));
    maxAge.ifPresent(m -> builder.queryParam("max_age", m));
    loginHint.ifPresent(h -> builder.queryParam("login_hint", h));
    return builder.build();
  }
}
