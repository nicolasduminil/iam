package fr.simplex_software.iam.fe.view;

import fr.simplex_software.iam.domain.schema.*;
import fr.simplex_software.iam.fe.service.*;
import jakarta.enterprise.context.*;
import jakarta.faces.application.*;
import jakarta.faces.context.*;
import jakarta.faces.view.*;
import jakarta.inject.*;
import jakarta.json.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
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
  @ConfigProperty(name = "keycloak.issuer-uri")
  String issuerUrl;
  @ConfigProperty(name="keycloak.redirect-uri")
  String redirectUri;
  private String metaData = null;
  private String clientId;
  private String scope;
  private String header;
  private String payload;
  private String signature;
  private String encoded;
  private String authorizationEndpoint;
  private String tokenEndpoint;
  private String userInfoEndpoint;
  private String script;

  public String getIssuerUrl()
  {
    return issuerUrl;
  }

  public void setIssuerUrl(String issuerUrl)
  {
    this.issuerUrl = issuerUrl;
  }

  public boolean isMetadata()
  {
    return metaData != null;
  }

  public void getDiscoveryMetadata()
  {
    System.out.println ("### getDiscoveryMetadata(): issuerUrl " + issuerUrl);
    final String fmt = "issuer: %s%nauthorization_endpoint: %s%ntoken_endpoint: %s%nuserinfo_endpoint: %s";
    Response r = iamServiceClient.loadDiscoveryMetadata(issuerUrl);
    System.out.println ("### getDiscoveryMetadata(): returned from iamServiceClient");
    r.readEntity(String.class);
    System.out.println ("### getDiscoveryMetadata(): readEntity OK");
    JsonObject jsonObject =
      Json.createReader(new StringReader(iamServiceClient.loadDiscoveryMetadata(issuerUrl).readEntity(String.class))).readObject();
    System.out.println ("### getDiscoveryMetadata(): returned from backend");
    authorizationEndpoint = jsonObject.getString("authorization_endpoint");
    tokenEndpoint = jsonObject.getString("token_endpoint");
    userInfoEndpoint = jsonObject.getString("userinfo_endpoint");
    metaData = String.format(fmt, jsonObject.getString("issuer"), authorizationEndpoint, tokenEndpoint, userInfoEndpoint);
  }

  public void authorizationRequest()
  {
    System.out.println ("### OpenIdConnectView.authorizationRequest(): authorizationEndpoint " + authorizationEndpoint + " " + clientId);
    Response response = iamServiceClient.sendAuthorizationRequest(new AuthorizationRequest(clientId, "code", scope, redirectUri, authorizationEndpoint));
    System.out.println ("### OpenIdConnectView.authorizationRequest(): Have called iam service");
    if (response.getStatus() != 200)
    {
      System.out.println ("### OpenIdConnectView.authorizationRequest(): throwing WebApplicationException with " + response.getStatus());
    }
    else
      System.out.println ("### OpenIdConnectView.authorizationRequest(): no exception thrown " + response.getStatus());
    String readEntity = response.readEntity(String.class);
    System.out.println ("### OpenIdConnectView.authorizationRequest(): readEnity " + readEntity);
    JsonObject jsonObject = Json.createReader(new StringReader(readEntity)).readObject();
    System.out.println ("### OpenIdConnectView.authorizationRequest(): jsonObject " + jsonObject.getString("access_token"));
    String accessToken[] = jsonObject.getString("access_token").split(".");
    header = accessToken[0];
    payload = accessToken[1];
    signature = accessToken[2];
    //FacesContext.getCurrentInstance()
  }

  public String getMetaData()
  {
    return metaData;
  }

  public String getClientId()
  {
    return clientId;
  }

  public void setClientId(String clientId)
  {
    this.clientId = clientId;
  }

  public String getScope()
  {
    return scope;
  }

  public void setScope(String scope)
  {
    this.scope = scope;
  }

  public String getHeader()
  {
    return header;
  }

  public void setHeader(String header)
  {
    this.header = header;
  }

  public String getPayload()
  {
    return payload;
  }

  public void setPayload(String payload)
  {
    this.payload = payload;
  }

  public String getSignature()
  {
    return signature;
  }

  public void setSignature(String signature)
  {
    this.signature = signature;
  }

  public String getEncoded()
  {
    return encoded;
  }

  public void setEncoded(String encoded)
  {
    this.encoded = encoded;
  }

  public String getAuthorizationEndpoint()
  {
    return authorizationEndpoint;
  }

  public void setAuthorizationEndpoint(String authorizationEndpoint)
  {
    this.authorizationEndpoint = authorizationEndpoint;
  }

  public String getScript()
  {
    return script;
  }

  public void setScript(String script)
  {
    this.script = script;
  }

  public String getTokenEndpoint()
  {
    return tokenEndpoint;
  }

  public void setTokenEndpoint(String tokenEndpoint)
  {
    this.tokenEndpoint = tokenEndpoint;
  }

  public String getUserInfoEndpoint()
  {
    return userInfoEndpoint;
  }

  public void setUserInfoEndpoint(String userInfoEndpoint)
  {
    this.userInfoEndpoint = userInfoEndpoint;
  }
}
